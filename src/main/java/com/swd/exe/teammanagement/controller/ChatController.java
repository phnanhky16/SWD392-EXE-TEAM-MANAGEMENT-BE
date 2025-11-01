package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.ChatMessageRequest;
import com.swd.exe.teammanagement.dto.response.ChatMessageResponse;
import com.swd.exe.teammanagement.dto.response.TypingIndicatorResponse;
import com.swd.exe.teammanagement.dto.response.UserStatusResponse;
import com.swd.exe.teammanagement.service.MessageService;
import com.swd.exe.teammanagement.service.UserStatusService;
import com.swd.exe.teammanagement.config.JwtService; // 👈 thêm
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    JwtService jwtService; // 👈 inject luôn

    // -------------------------------------------------------------------------
    // MESSAGES
    // -------------------------------------------------------------------------

    @Operation(summary = "Send a message", description = "Send a message to a group chat")
    @PostMapping("/messages")
    public ApiResponse<ChatMessageResponse> sendMessage(
            @Valid @RequestBody ChatMessageRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = getCurrentUserId(httpRequest);

        ChatMessageResponse response = messageService.sendMessage(request, userId);

        // broadcast cho group
        messagingTemplate.convertAndSend(
                "/topic/group." + response.getGroupId() + ".messages",
                response
        );

        return ApiResponse.success("Message sent successfully", response);
    }

    @Operation(summary = "Get group messages", description = "Retrieve messages from a specific group")
    @GetMapping("/groups/{groupId}/messages")
    public ApiResponse<List<ChatMessageResponse>> getGroupMessages(
            @PathVariable Long groupId,
            @Parameter(description = "Page number (1-based)")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "50") int size,
            HttpServletRequest httpRequest
    ) {
        Long userId = getCurrentUserId(httpRequest);

        List<ChatMessageResponse> messages =
                messageService.getGroupMessages(groupId, userId, page, size);

        return ApiResponse.success("Messages retrieved successfully", messages);
    }

    @Operation(summary = "Edit a message", description = "Edit a message that you sent")
    @PutMapping("/messages/{messageId}")
    public ApiResponse<ChatMessageResponse> editMessage(
            @PathVariable Long messageId,
            @RequestBody String newContent,
            HttpServletRequest httpRequest
    ) {
        Long userId = getCurrentUserId(httpRequest);

        ChatMessageResponse response =
                messageService.editMessage(messageId, newContent, userId);

        return ApiResponse.success("Message edited successfully", response);
    }

    @Operation(summary = "Delete a message", description = "Delete a message that you sent")
    @DeleteMapping("/messages/{messageId}")
    public ApiResponse<Void> deleteMessage(
            @PathVariable Long messageId,
            HttpServletRequest httpRequest
    ) {
        Long userId = getCurrentUserId(httpRequest);
        messageService.deleteMessage(messageId, userId);
        return ApiResponse.success("Message deleted successfully", null);
    }

    @Operation(summary = "Search messages", description = "Search for messages in a group")
    @GetMapping("/groups/{groupId}/search")
    public ApiResponse<List<ChatMessageResponse>> searchMessages(
            @PathVariable Long groupId,
            @RequestParam String keyword,
            HttpServletRequest httpRequest
    ) {
        Long userId = getCurrentUserId(httpRequest);

        List<ChatMessageResponse> messages =
                messageService.searchMessages(groupId, userId, keyword);

        return ApiResponse.success("Search results retrieved successfully", messages);
    }

    @Operation(summary = "Get message by ID", description = "Retrieve a specific message by its ID")
    @GetMapping("/messages/{messageId}")
    public ApiResponse<ChatMessageResponse> getMessage(@PathVariable Long messageId) {
        ChatMessageResponse message = messageService.getMessageById(messageId);
        return ApiResponse.success("Message retrieved successfully", message);
    }

    // -------------------------------------------------------------------------
    // PRESENCE / STATUS
    // -------------------------------------------------------------------------

    @Operation(summary = "Get online users in group", description = "Get list of online users in a specific group")
    @GetMapping("/groups/{groupId}/online-users")
    public ApiResponse<List<UserStatusResponse>> getOnlineUsers(@PathVariable Long groupId) {
        List<UserStatusResponse> onlineUsers = userStatusService.getOnlineUsersInGroup(groupId);
        return ApiResponse.success("Online users retrieved successfully", onlineUsers);
    }

    @Operation(summary = "Get typing users", description = "Get users currently typing in a group")
    @GetMapping("/groups/{groupId}/typing")
    public ApiResponse<List<TypingIndicatorResponse>> getTypingUsers(@PathVariable Long groupId) {
        List<TypingIndicatorResponse> typingUsers = userStatusService.getTypingUsers(groupId);
        return ApiResponse.success("Typing users retrieved successfully", typingUsers);
    }

    @Operation(summary = "Get user status", description = "Get status of a specific user")
    @GetMapping("/users/{userId}/status")
    public ApiResponse<UserStatusResponse> getUserStatus(@PathVariable Long userId) {
        UserStatusResponse status = userStatusService.getUserStatus(userId);
        return ApiResponse.success("User status retrieved successfully", status);
    }

    @Operation(summary = "Set my status", description = "Set your own status (ONLINE, OFFLINE, AWAY, BUSY)")
    @PatchMapping("/users/me/status")
    public ApiResponse<Void> setMyStatus(
            @RequestParam String status,
            HttpServletRequest httpRequest
    ) {
        Long userId = getCurrentUserId(httpRequest);
        userStatusService.setUserStatus(userId, status);
        return ApiResponse.success("Status updated successfully", null);
    }

    // -------------------------------------------------------------------------
    // HELPER
    // -------------------------------------------------------------------------

    /**
     * Lấy userId từ JWT trong header Authorization.
     * Dùng JwtService để đọc sub -> id, không phụ thuộc vào việc filter set principal là email.
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing Authorization header");
        }

        String token = authHeader.substring(7);

        // sẽ đọc từ sub vì generateToken(...) đã setSubject(id)
        return jwtService.extractUserId(token);
    }
}
