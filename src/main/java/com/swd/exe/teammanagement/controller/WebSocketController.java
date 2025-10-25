package com.swd.exe.teammanagement.controller;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class WebSocketController {

    UserStatusService userStatusService;
    MessageService messageService;
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Get current user from security context
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return;
            }
            
            // Lấy user ID từ JWT token
            Long userId = getCurrentUserIdFromAuth(auth);
            
            // Lưu tin nhắn vào database thông qua MessageService
            ChatMessageResponse savedMessage = messageService.sendMessage(request, userId);
            
            // Broadcast tin nhắn đã lưu đến tất cả members trong nhóm
            messagingTemplate.convertAndSend("/topic/group." + request.getGroupId() + ".messages", savedMessage);
            
        } catch (Exception e) {
            // Handle error - send error message back to sender
            messagingTemplate.convertAndSendToUser(
                headerAccessor.getUser().getName(), 
                "/queue/errors", 
                "Failed to send message: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        // Add user to the session
        headerAccessor.getSessionAttributes().put("username", request.getContent());
        headerAccessor.getSessionAttributes().put("groupId", request.getGroupId());
        
        // Notify group about user joining
        messagingTemplate.convertAndSend("/topic/group." + request.getGroupId() + ".users", 
            request.getContent() + " joined the chat");
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload TypingIndicatorResponse typingIndicator, SimpMessageHeaderAccessor headerAccessor) {
        // Broadcast typing indicator to group
        messagingTemplate.convertAndSend("/topic/group." + typingIndicator.getGroupId() + ".typing", typingIndicator);
    }

    @MessageMapping("/chat.stopTyping")
    public void handleStopTyping(@Payload TypingIndicatorResponse typingIndicator, SimpMessageHeaderAccessor headerAccessor) {
        // Broadcast stop typing indicator to group
        messagingTemplate.convertAndSend("/topic/group." + typingIndicator.getGroupId() + ".typing", typingIndicator);
    }

    @MessageMapping("/user.status")
    public void handleUserStatus(@Payload UserStatusResponse statusResponse, SimpMessageHeaderAccessor headerAccessor) {
        // Update user status
        userStatusService.setUserStatus(statusResponse.getUserId(), statusResponse.getStatus());
        
        // Broadcast status update to all user's groups
        // This would need to be implemented based on your group membership logic
        if (statusResponse.getCurrentGroupId() != null) {
            messagingTemplate.convertAndSend("/topic/group." + statusResponse.getCurrentGroupId() + ".status", statusResponse);
        }
    }

    @MessageMapping("/group.{groupId}.join")
    public void joinGroup(@DestinationVariable Long groupId, SimpMessageHeaderAccessor headerAccessor) {
        // Handle user joining a group
        // You might want to validate group membership here
    }

    @MessageMapping("/group.{groupId}.leave")
    public void leaveGroup(@DestinationVariable Long groupId, SimpMessageHeaderAccessor headerAccessor) {
        // Handle user leaving a group
    }
    
    /**
     * Lấy user ID từ authentication context
     * JWT token có user ID trong subject field
     */
    private Long getCurrentUserIdFromAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        
        // JWT subject chứa user ID
        String subject = auth.getName();
        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID in token: " + subject);
        }
    }
}
