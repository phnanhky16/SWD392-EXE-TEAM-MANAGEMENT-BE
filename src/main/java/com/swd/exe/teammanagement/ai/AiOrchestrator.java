package com.swd.exe.teammanagement.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swd.exe.teammanagement.ai.client.GeminiClient;
import com.swd.exe.teammanagement.ai.router.IntentRouter;
import com.swd.exe.teammanagement.dto.response.AiChatResponse;
import com.swd.exe.teammanagement.dto.response.GroupSummaryResponse;
import com.swd.exe.teammanagement.entity.ChatSession;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.chat.ChatRole;
import com.swd.exe.teammanagement.service.ChatHistoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiOrchestrator {

    GeminiClient geminiClient;
    IntentRouter intentRouter;
    ChatHistoryService chatHistoryService;
    ObjectMapper mapper = new ObjectMapper();


    /**
     * Hàm chính: nhận message của user, quyết định gọi function, trả về
     * - answer: câu trả lời natural language
     * - attachments["groups"]: List<AiGroupSummaryResponse> nếu intent là FIND_GROUP
     */
    public Mono<AiChatResponse> handleUserMessage(User user, String userMessage) {

        // 0) Lấy / tạo session & lưu tin nhắn USER
        ChatSession session = chatHistoryService.getOrCreateActiveSession(user);
        chatHistoryService.saveMessage(session, ChatRole.USER, userMessage);

        // 1) Prompt LẦN 1: để model quyết định có gọi function hay không
        String functionDecisionPrompt = """
                Bạn là bộ điều phối cho chatbot hỗ trợ sinh viên ghép nhóm học tập được tạo ra bởi nhóm sinh viên đại học FPT.

                Nhiệm vụ:
                - Đọc câu hỏi của người dùng.
                - QUYẾT ĐỊNH xem có cần gọi HÀM HỆ THỐNG hay không.

                Hệ thống hiện có đúng 1 hàm:
                - find_groups(keyword: string, limit: integer)
                  + Tác dụng: tìm các nhóm học tập trong hệ thống theo từ khóa.
                  + limit: số lượng nhóm tối đa (mặc định 5 nếu không chắc).

                Hãy TRẢ VỀ DUY NHẤT MỘT JSON OBJECT, không thêm text nào khác, với cấu trúc:

                {
                  "call_function": true hoặc false,
                  "function": null hoặc "find_groups",
                  "args": null hoặc { "keyword": string, "limit": number },
                  "answer": null hoặc "câu trả lời trực tiếp cho người dùng"
                }

                Quy tắc:
                - Nếu câu hỏi chỉ là thắc mắc chung (FAQ, cách dùng hệ thống, quy định, ...),
                  bạn có thể trả lời luôn, khi đó:
                    "call_function": false,
                    "function": null,
                    "args": null,
                    "answer": "câu trả lời của bạn"
                - Nếu người dùng muốn TÌM hoặc XEM danh sách nhóm (ví dụ: "nhóm AI", "có nhóm đồ họa nào không"),
                  hãy GỌI HÀM:
                    "call_function": true,
                    "function": "find_groups",
                    "args": { "keyword": "từ khóa chính", "limit": số nguyên > 0 },
                    "answer": null

                Câu hỏi của người dùng: "%s"
                """.formatted(userMessage);

        // 2) Gọi Gemini lần 1 để nhận JSON quyết định function
        return geminiClient.generateJson(functionDecisionPrompt)
                .flatMap(jsonText -> {
                    System.out.println("=== GEMINI DECISION ===");
                    System.out.println(jsonText);

                    JsonNode root;
                    try {
                        root = mapper.readTree(jsonText);
                    } catch (Exception e) {
                        // Nếu không parse được JSON, coi luôn output là câu trả lời text
                        e.printStackTrace();
                        return Mono.just(
                                AiChatResponse.builder()
                                        .answer(jsonText)
                                        .attachments(Collections.emptyMap())
                                        .build()
                        );
                    }

                    boolean callFunction = root.path("call_function").asBoolean(false);
                    String functionName = root.path("function").asText(null);

                    // Nếu model tự trả lời, không gọi function
                    if (!callFunction || !"find_groups".equalsIgnoreCase(functionName)) {
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

                    // 3) Model yêu cầu gọi function find_groups(...)
                    JsonNode argsNode = root.path("args");
                    String keyword = argsNode.path("keyword").asText(userMessage);
                    int limit = argsNode.path("limit").asInt(5);

                    List<?> results = intentRouter.route("FIND_GROUP", keyword, limit);

                    // 4) Build context cho Gemini lần 2 (tóm tắt dữ liệu nhóm)
                    StringBuilder ctx = new StringBuilder();
                    ctx.append("Bạn là trợ lý cho hệ thống ghép nhóm học tập của sinh viên.\n\n");

                    ctx.append("Người dùng hỏi: ").append(userMessage).append("\n");
                    ctx.append("Hàm hệ thống đã được gọi: find_groups\n");
                    ctx.append("Tham số:\n");
                    ctx.append("  - keyword = ").append(keyword).append("\n");
                    ctx.append("  - limit   = ").append(limit).append("\n\n");

                    ctx.append("KẾT QUẢ TỪ HỆ THỐNG (dữ liệu thật, không được bịa thêm):\n");

                    if (results.isEmpty()) {
                        ctx.append("- Không có nhóm nào phù hợp với từ khóa trên.\n");
                    } else {
                        for (Object r : results) {
                            if (r instanceof Group g) {
                                ctx.append("- ID: ").append(g.getId())
                                        .append(" | Tên nhóm: ").append(g.getTitle())
                                        .append(" | Mô tả: ").append(
                                                g.getDescription() == null ? "" : g.getDescription()
                                        )
                                        .append(" | Trạng thái: ").append(g.getStatus())
                                        .append("\n");
                            } else {
                                ctx.append("- ").append(r.toString()).append("\n");
                            }
                        }
                    }

                    ctx.append("""
                            
                            Hãy:
                            - Tóm tắt lại các nhóm ở trên cho sinh viên (nếu có).
                            - Gợi ý nhóm phù hợp nhất với nhu cầu của họ.
                            - Nhắc sinh viên rằng họ có thể bấm vào tên nhóm hoặc ID trên giao diện để xem chi tiết và gửi yêu cầu tham gia.
                            - Trả lời bằng tiếng Việt, thân thiện, rõ ràng.
                            """);

                    // 5) Gọi Gemini lần 2 để sinh câu trả lời tự nhiên
                    return geminiClient.generateText(ctx.toString())
                            .map(answerText -> {
                                chatHistoryService.saveMessage(session, ChatRole.ASSISTANT, answerText);
                                // Map kết quả DB -> danh sách GroupSummary để gắn vào attachments["groups"]
                                List<GroupSummaryResponse> groupSummaries = results.stream()
                                        .filter(r -> r instanceof Group)
                                        .map(r -> (Group) r)
                                        .map(g -> new GroupSummaryResponse(g.getId(), g.getTitle()))
                                        .collect(Collectors.toList());

                                Map<String, Object> attachments = new HashMap<>();
                                attachments.put("groups", groupSummaries);
                                // Sau này có thể thêm:
                                // attachments.put("users", userList);
                                // attachments.put("references", referenceList);

                                return AiChatResponse.builder()
                                        .answer(answerText)
                                        .attachments(attachments)
                                        .build();
                            });
                });
    }
}
