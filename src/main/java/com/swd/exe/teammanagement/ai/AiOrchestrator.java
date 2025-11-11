package com.swd.exe.teammanagement.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swd.exe.teammanagement.ai.client.GeminiClient;
import com.swd.exe.teammanagement.ai.router.IntentRouter;
import com.swd.exe.teammanagement.ai.router.IntentExecutionResult;
import com.swd.exe.teammanagement.dto.response.AiChatResponse;
import com.swd.exe.teammanagement.entity.ChatSession;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.chat.ChatRole;
import com.swd.exe.teammanagement.service.ChatHistoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiOrchestrator {

    GeminiClient geminiClient;
    IntentRouter intentRouter;
    ChatHistoryService chatHistoryService;
    ObjectMapper mapper = new ObjectMapper();

    /**
     * Hàm chính: nhận message của user, quyết định gọi intent handler nào
     * rồi sinh câu trả lời + attachments cho FE.
     */
    public Mono<AiChatResponse> handleUserMessage(User user, String userMessage) {

        // 0) Lấy / tạo session & lưu tin nhắn USER
        ChatSession session = chatHistoryService.getOrCreateActiveSession(user);
        chatHistoryService.saveMessage(session, ChatRole.USER, userMessage);

        // 1) Prompt LẦN 1: model phân loại intent + args
        String decisionPrompt = """
                Bạn là bộ điều phối cho chatbot hỗ trợ sinh viên ghép nhóm học tập
                trong hệ thống quản lý nhóm của Đại học FPT.

                Nhiệm vụ:
                - Đọc câu hỏi của người dùng.
                - Xác định intent phù hợp.
                - Quyết định có cần gọi HÀM HỆ THỐNG (function) hay không.

                Các intent hợp lệ:

                1. FIND_GROUP
                   - Khi user muốn tìm/ xem danh sách nhóm theo từ khóa.
                   - args: { "keyword": string, "limit": number (mặc định 5) }
                   - Gọi function: true

                2. LIST_GROUP_TEACHERS
                   - Khi user muốn biết giáo viên/giảng viên phụ trách của một nhóm.
                   - args: { "groupId": number }
                   - Gọi function: true

                3. LIST_GROUP_MEMBERS
                   - Khi user muốn xem thành viên của một nhóm.
                   - args: { "groupId": number }
                   - Gọi function: true

                4. CHECK_MY_GROUP_STATUS
                   - Khi user hỏi về tình trạng nhóm hiện tại của chính mình
                     (nhóm đã đủ người chưa, đã đa dạng chuyên ngành chưa, đang FORMING hay LOCKED,...).
                   - args: {} (có thể bỏ trống, vì lấy theo user hiện tại)
                   - Gọi function: true

                5. FAQ_RULES
                   - Câu hỏi chung về luật chơi, cách dùng hệ thống, quy định, hướng dẫn,
                     nhưng KHÔNG cần đọc dữ liệu thật từ DB.
                   - Ví dụ: "làm sao để tham gia nhóm", "nhóm tối đa bao nhiêu người", "leader có quyền gì?".
                   - Gọi function: false
                   - Trả answer trực tiếp.

                6. UNKNOWN
                   - Khi bạn thực sự không hiểu câu hỏi hoặc ngoài phạm vi hệ thống.
                   - Gọi function: false
                   - Trả lời lịch sự rằng bạn không hiểu và gợi ý user hỏi lại theo cách khác.

                Định dạng JSON trả về (chỉ duy nhất một object, không thêm text khác):

                {
                  "intent": "FIND_GROUP" | "LIST_GROUP_TEACHERS" | "LIST_GROUP_MEMBERS"
                             | "CHECK_MY_GROUP_STATUS" | "FAQ_RULES" | "UNKNOWN",
                  "call_function": true hoặc false,
                  "args": null hoặc object (tùy intent),
                  "answer": null hoặc "câu trả lời trực tiếp cho người dùng nếu không gọi function"
                }

                Quy tắc:
                - Với các intent cần dữ liệu thật (FIND_GROUP, LIST_GROUP_TEACHERS,
                  LIST_GROUP_MEMBERS, CHECK_MY_GROUP_STATUS) -> bắt buộc "call_function": true.
                - Với FAQ_RULES hoặc UNKNOWN -> "call_function": false và điền "answer".
                - Hãy luôn chọn intent sát nghĩa nhất với câu hỏi của người dùng.

                Câu hỏi của người dùng: "%s"
                """.formatted(userMessage);

        // 2) Gọi Gemini lần 1 để nhận JSON quyết định intent
        return geminiClient.generateJson(decisionPrompt)
                .flatMap(jsonText -> {
                    System.out.println("=== GEMINI DECISION ===");
                    System.out.println(jsonText);

                    JsonNode root;
                    try {
                        root = mapper.readTree(jsonText);
                    } catch (Exception e) {
                        // Nếu không parse được JSON, coi output là câu trả lời text luôn
                        e.printStackTrace();
                        chatHistoryService.saveMessage(session, ChatRole.ASSISTANT, jsonText);
                        return Mono.just(
                                AiChatResponse.builder()
                                        .answer(jsonText)
                                        .attachments(Collections.emptyMap())
                                        .build()
                        );
                    }

                    String intent = root.path("intent").asText("UNKNOWN");
                    boolean callFunction = root.path("call_function").asBoolean(false);
                    JsonNode argsNode = root.path("args").isMissingNode() ? mapper.createObjectNode() : root.path("args");

                    // 2.a. Trường hợp KHÔNG gọi function (FAQ_RULES / UNKNOWN / ...)
                    if (!callFunction) {
                        String directAnswer = root.path("answer").asText(
                                "Xin lỗi, hiện tại tôi chưa có câu trả lời cho yêu cầu này."
                        );
                        chatHistoryService.saveMessage(session, ChatRole.ASSISTANT, directAnswer);

                        return Mono.just(
                                AiChatResponse.builder()
                                        .answer(directAnswer)
                                        .attachments(Collections.emptyMap())
                                        .build()
                        );
                    }

                    // 2.b. Trường hợp CÓ gọi function -> dùng IntentRouter + handler tương ứng
                    Optional<IntentExecutionResult> optResult =
                            intentRouter.execute(intent, user, argsNode);

                    if (optResult.isEmpty()) {
                        String fallback = "Xin lỗi, hiện tại tôi chưa hỗ trợ thao tác cho yêu cầu này (intent: "
                                + intent + ").";
                        chatHistoryService.saveMessage(session, ChatRole.ASSISTANT, fallback);
                        return Mono.just(
                                AiChatResponse.builder()
                                        .answer(fallback)
                                        .attachments(Collections.emptyMap())
                                        .build()
                        );
                    }

                    IntentExecutionResult execResult = optResult.get();

                    // 3) Build context cho Gemini lần 2 từ kết quả handler
                    StringBuilder ctx = new StringBuilder();
                    ctx.append("Bạn là trợ lý cho hệ thống ghép nhóm học tập của sinh viên.\n\n");
                    ctx.append("Người dùng vừa hỏi: ").append(userMessage).append("\n\n");

                    // phần context cụ thể do handler build (groups, teachers, members, status,...)
                    ctx.append(execResult.getContextForPrompt()).append("\n\n");

                    ctx.append("""
                            Hãy:
                            - Dựa hoàn toàn trên THÔNG TIN THẬT ở trên, không được bịa thêm dữ liệu.
                            - Tóm tắt lại cho sinh viên những gì hệ thống tìm được.
                            - Nếu có danh sách (nhóm / giáo viên / thành viên...), hãy liệt kê rõ ràng, dễ đọc.
                            - Nếu phù hợp, gợi ý lựa chọn tốt nhất cho sinh viên.
                            - Nhắc sinh viên rằng họ có thể bấm vào tên nhóm, ID nhóm hoặc xem chi tiết trên giao diện để thực hiện thao tác tiếp.
                            - Trả lời bằng tiếng Việt, thân thiện, rõ ràng.
                            """);

                    // 4) Gọi Gemini lần 2 để sinh câu trả lời tự nhiên
                    return geminiClient.generateText(ctx.toString())
                            .map(answerText -> {
                                chatHistoryService.saveMessage(session, ChatRole.ASSISTANT, answerText);

                                Map<String, Object> attachments =
                                        execResult.getAttachments() != null
                                                ? execResult.getAttachments()
                                                : Collections.emptyMap();

                                return AiChatResponse.builder()
                                        .answer(answerText)
                                        .attachments(attachments)
                                        .build();
                            });
                });
    }
}
