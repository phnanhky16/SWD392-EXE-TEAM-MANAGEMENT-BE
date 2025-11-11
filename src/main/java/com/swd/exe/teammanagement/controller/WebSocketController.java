package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.config.JwtService;
import com.swd.exe.teammanagement.dto.request.ChatMessageRequest;
import com.swd.exe.teammanagement.dto.response.ChatMessageResponse;
import com.swd.exe.teammanagement.dto.response.TypingIndicatorResponse;
import com.swd.exe.teammanagement.dto.response.UserStatusResponse;
import com.swd.exe.teammanagement.service.MessageService;
import com.swd.exe.teammanagement.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class WebSocketController {

    UserStatusService userStatusService;
    MessageService messageService;
    SimpMessagingTemplate messagingTemplate;
    JwtService jwtService; // 👈 để đọc sub từ token

    // ===================== CHAT MESSAGE =====================

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest request,
                            SimpMessageHeaderAccessor headerAccessor) {

        try {
            Long userId = resolveUserId(headerAccessor);   // 👈 lấy userId từ STOMP header
            ChatMessageResponse savedMessage = messageService.sendMessage(request, userId);

            // broadcast tới group
            messagingTemplate.convertAndSend(
                    "/topic/group." + request.getGroupId() + ".messages",
                    savedMessage
            );

        } catch (Exception e) {
            // nếu muốn báo lỗi về user:
            String user = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : null;
            if (user != null) {
                messagingTemplate.convertAndSendToUser(
                        user,
                        "/queue/errors",
                        "Failed to send message: " + e.getMessage()
                );
            }
        }
    }

    // ===================== TYPING =====================

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload TypingIndicatorResponse typing,
                             SimpMessageHeaderAccessor headerAccessor) {
        // gửi thẳng cho group
        messagingTemplate.convertAndSend(
                "/topic/group." + typing.getGroupId() + ".typing",
                typing
        );
    }

    @MessageMapping("/chat.stopTyping")
    public void handleStopTyping(@Payload TypingIndicatorResponse typing,
                                 SimpMessageHeaderAccessor headerAccessor) {
        messagingTemplate.convertAndSend(
                "/topic/group." + typing.getGroupId() + ".typing",
                typing
        );
    }

    // ===================== USER STATUS =====================

    @MessageMapping("/user.status")
    public void handleUserStatus(@Payload UserStatusResponse status,
                                 SimpMessageHeaderAccessor headerAccessor) {

        userStatusService.setUserStatus(status.getUserId(), status.getStatus());

        if (status.getCurrentGroupId() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/group." + status.getCurrentGroupId() + ".status",
                    status
            );
        }
    }

    // ===================== GROUP JOIN/LEAVE =====================

    @MessageMapping("/group.{groupId}.join")
    public void joinGroup(@DestinationVariable Long groupId,
                          SimpMessageHeaderAccessor headerAccessor) {
    }

    @MessageMapping("/group.{groupId}.leave")
    public void leaveGroup(@DestinationVariable Long groupId,
                           SimpMessageHeaderAccessor headerAccessor) {
    }

    // ===================== HELPER =====================

    /**
     * Lấy JWT từ STOMP header và rút ra userId (sub).
     * Client phải gửi header: Authorization: Bearer <token>
     */
    private Long resolveUserId(SimpMessageHeaderAccessor headerAccessor) {
        List<String> authHeaders = headerAccessor.getNativeHeader("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            throw new RuntimeException("Missing Authorization header in WebSocket frame");
        }

        String authHeader = authHeaders.get(0); // lấy cái đầu
        if (!authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authHeader.substring(7);
        return jwtService.extractUserId(token); // vì bạn đã setSubject(id)
    }
}
