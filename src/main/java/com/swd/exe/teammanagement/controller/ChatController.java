package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.ChatMessageRequest;
import com.swd.exe.teammanagement.dto.response.ChatMessageResponse;
import com.swd.exe.teammanagement.dto.response.TypingIndicatorResponse;
import com.swd.exe.teammanagement.dto.response.UserStatusResponse;
import com.swd.exe.teammanagement.service.MessageService;
import com.swd.exe.teammanagement.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Chat Management", description = "APIs for chat functionality")
public class ChatController {

    MessageService messageService;
    UserStatusService userStatusService;
    SimpMessagingTemplate messagingTemplate;

    @Operation(
            summary = "Send a message",
            description = "Send a message to a group chat"
    )
    @PostMapping("/messages")
    public ApiResponse<ChatMessageResponse> sendMessage(@RequestBody ChatMessageRequest request) {
        Long userId = getCurrentUserId();
        
        ChatMessageResponse response = messageService.sendMessage(request, userId);
        // Broadcast to group subscribers so others see it immediately
        messagingTemplate.convertAndSend("/topic/group." + response.getGroupId() + ".messages", response);
        return ApiResponse.success("Message sent successfully", response);
    }

    @Operation(
            summary = "Get group messages",
            description = "Retrieve messages from a specific group"
    )
    @GetMapping("/groups/{groupId}/messages")
    public ApiResponse<List<ChatMessageResponse>> getGroupMessages(
            @PathVariable Long groupId,
            @Parameter(description = "Page number (1-based)")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "50") int size) {
        
        Long userId = getCurrentUserId();
        List<ChatMessageResponse> messages = messageService.getGroupMessages(groupId, userId, page, size);
        return ApiResponse.success("Messages retrieved successfully", messages);
    }

    @Operation(
            summary = "Edit a message",
            description = "Edit a message that you sent"
    )
    @PutMapping("/messages/{messageId}")
    public ApiResponse<ChatMessageResponse> editMessage(
            @PathVariable Long messageId,
            @RequestBody String newContent) {
        
        Long userId = getCurrentUserId();
        ChatMessageResponse response = messageService.editMessage(messageId, newContent, userId);
        return ApiResponse.success("Message edited successfully", response);
    }

    @Operation(
            summary = "Delete a message",
            description = "Delete a message that you sent"
    )
    @DeleteMapping("/messages/{messageId}")
    public ApiResponse<Void> deleteMessage(@PathVariable Long messageId) {
        Long userId = getCurrentUserId();
        messageService.deleteMessage(messageId, userId);
        return ApiResponse.success("Message deleted successfully", null);
    }

    @Operation(
            summary = "Search messages",
            description = "Search for messages in a group"
    )
    @GetMapping("/groups/{groupId}/search")
    public ApiResponse<List<ChatMessageResponse>> searchMessages(
            @PathVariable Long groupId,
            @RequestParam String keyword) {
        
        Long userId = getCurrentUserId();
        List<ChatMessageResponse> messages = messageService.searchMessages(groupId, userId, keyword);
        return ApiResponse.success("Search results retrieved successfully", messages);
    }

    @Operation(
            summary = "Get message by ID",
            description = "Retrieve a specific message by its ID"
    )
    @GetMapping("/messages/{messageId}")
    public ApiResponse<ChatMessageResponse> getMessage(@PathVariable Long messageId) {
        ChatMessageResponse message = messageService.getMessageById(messageId);
        return ApiResponse.success("Message retrieved successfully", message);
    }

    @Operation(
            summary = "Get online users in group",
            description = "Get list of online users in a specific group"
    )
    @GetMapping("/groups/{groupId}/online-users")
    public ApiResponse<List<UserStatusResponse>> getOnlineUsers(@PathVariable Long groupId) {
        List<UserStatusResponse> onlineUsers = userStatusService.getOnlineUsersInGroup(groupId);
        return ApiResponse.success("Online users retrieved successfully", onlineUsers);
    }

    // Test endpoint - không cần authentication
    @PostMapping("/test/send-message")
    public ApiResponse<ChatMessageResponse> testSendMessage(@RequestBody ChatMessageRequest request) {
        // Test với user ID = 1 (hardcode cho test)
        Long userId = 1L;
        ChatMessageResponse response = messageService.sendMessage(request, userId);
        return ApiResponse.success("Test message sent successfully", response);
    }

    @Operation(
            summary = "Get user status",
            description = "Get status of a specific user"
    )
    @GetMapping("/users/{userId}/status")
    public ApiResponse<UserStatusResponse> getUserStatus(@PathVariable Long userId) {
        UserStatusResponse status = userStatusService.getUserStatus(userId);
        return ApiResponse.success("User status retrieved successfully", status);
    }

    @Operation(
            summary = "Set user status",
            description = "Set your own status (ONLINE, OFFLINE, AWAY, BUSY)"
    )
    @PostMapping("/users/status")
    public ApiResponse<Void> setUserStatus(@RequestParam String status) {
        Long userId = getCurrentUserId();
        userStatusService.setUserStatus(userId, status);
        return ApiResponse.success("Status updated successfully", null);
    }

    @Operation(
            summary = "Get typing users",
            description = "Get users currently typing in a group"
    )
    @GetMapping("/groups/{groupId}/typing")
    public ApiResponse<List<TypingIndicatorResponse>> getTypingUsers(@PathVariable Long groupId) {
        List<TypingIndicatorResponse> typingUsers = userStatusService.getTypingUsers(groupId);
        return ApiResponse.success("Typing users retrieved successfully", typingUsers);
    }

    // Test endpoint - lấy online users không cần authentication
    @GetMapping("/test/groups/{groupId}/online-users")
    public ApiResponse<List<UserStatusResponse>> testGetOnlineUsers(@PathVariable Long groupId) {
        // Tạo mock data cho test
        List<UserStatusResponse> mockUsers = List.of(
            UserStatusResponse.builder()
                .userId(1L)
                .userName("Test User 1")
                .status("ONLINE")
                .build(),
            UserStatusResponse.builder()
                .userId(2L)
                .userName("Test User 2")
                .status("ONLINE")
                .build()
        );
        return ApiResponse.success("Test online users retrieved successfully", mockUsers);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
