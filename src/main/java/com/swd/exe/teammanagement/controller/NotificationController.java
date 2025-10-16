package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.entity.Notification;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notification Management", description = "APIs for managing user notifications")
public class NotificationController {
    NotificationService notificationService;
    UserRepository userRepository;

    @Operation(
            summary = "Get my notifications",
            description = "Retrieve all notifications for the current authenticated user, ordered by creation date (newest first)"
    )
    @GetMapping
    public ApiResponse<List<Notification>> getMyNotifications(Authentication authentication) {
        String email = authentication.getName();
        return ApiResponse.success("Get notifications successfully", 
                notificationService.getMyNotifications(email));
    }

    @Operation(
            summary = "Mark notification as read",
            description = "Mark a specific notification as read. Only the notification receiver can mark it as read."
    )
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        notificationService.markAsRead(notificationId, user.getId());
        return ApiResponse.success("Notification marked as read successfully", null);
    }
}
