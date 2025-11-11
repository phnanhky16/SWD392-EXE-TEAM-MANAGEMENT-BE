package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.ai.AiOrchestrator;
import com.swd.exe.teammanagement.dto.request.AiChatRequest;
import com.swd.exe.teammanagement.dto.response.AiChatMessageResponse;
import com.swd.exe.teammanagement.dto.response.AiChatResponse;
import com.swd.exe.teammanagement.entity.ChatMessage;
import com.swd.exe.teammanagement.entity.ChatSession;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ChatbotController {

    AiOrchestrator aiOrchestrator;
    UserRepository userRepository;
    ChatHistoryService chatHistoryService;

    /**
     * Endpoint chính để chat với AI.
     * Lấy currentUser từ token, truyền vào AiOrchestrator để nó tự lưu lịch sử.
     */
    @PostMapping("/chatbot")
    public Mono<AiChatResponse> chat(@RequestBody AiChatRequest req) {
        User currentUser = getCurrentUser();
        return aiOrchestrator.handleUserMessage(currentUser, req.getMessage());
    }

    /**
     * Lấy lịch sử chat của user hiện tại (ví dụ 20 message gần nhất).
     * FE có thể hiển thị role USER/ASSISTANT để render trái/phải.
     */
    @GetMapping("/chatbot/history")
    public List<AiChatMessageResponse> getHistory(
            @RequestParam(defaultValue = "20") int limit
    ) {
        User currentUser = getCurrentUser();
        ChatSession session = chatHistoryService.getOrCreateActiveSession(currentUser);
        List<ChatMessage> messages = chatHistoryService.getMessages(session, limit);

        return messages.stream()
                .map(m -> new AiChatMessageResponse(
                        m.getRole().name(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .toList();
    }

    // ====== Helper & DTO nội bộ ======

    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }
}
