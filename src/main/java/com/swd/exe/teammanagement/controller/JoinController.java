package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.entity.Join;
import com.swd.exe.teammanagement.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/joins")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Join Management", description = "APIs for joining groups and handling join requests")
public class JoinController {
    JoinService joinService;

    @Operation(
            summary = "Join a group",
            description = "Join a group. If group status is FORMING, user becomes leader. If ACTIVE, user becomes member. If LOCKED, creates a join request with voting."
    )
    @PostMapping("/{groupId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Void> joinGroup(@PathVariable Long groupId) {
        joinService.joinGroup(groupId);
        return ApiResponse.created("Join request processed successfully", null);
    }

    @Operation(
            summary = "Get pending join requests for a group",
            description = "Get all pending join requests for a specific group. Only accessible by group members."
    )
    @GetMapping("/{groupId}/pending")
    public ApiResponse<List<Join>> getPendingJoinRequests(@PathVariable Long groupId) {
        return ApiResponse.success("Get pending join requests successfully", 
                joinService.getPendingJoinRequests(groupId));
    }

    @Operation(
            summary = "Get my join requests",
            description = "Get all pending join requests created by the current user."
    )
    @GetMapping("/my-requests")
    public ApiResponse<List<Join>> getMyJoinRequests() {
        return ApiResponse.success("Get my join requests successfully", 
                joinService.getMyJoinRequests());
    }

    @Operation(
            summary = "Cancel join request",
            description = "Cancel a pending join request. Only the request creator can cancel their own request."
    )
    @DeleteMapping("/{joinId}")
    @PreAuthorize("hasRole('MODERATOR')or hasRole('ADMIN')")
    public ApiResponse<Void> cancelJoinRequest(@PathVariable Long joinId) {
        joinService.cancelJoinRequest(joinId);
        return ApiResponse.success("Join request cancelled successfully", null);
    }
    @Operation(
            summary = "Activate a join",
            description = "Set the join request as active. Requires moderator privileges."
    )
    @PutMapping("/{joinId}/activate")
    @PreAuthorize("hasRole('MODERATOR')or hasRole('ADMIN')")
    public ApiResponse<Join> activateJoin(@PathVariable Long joinId) {
        return ApiResponse.success("Join activated successfully", joinService.activateJoin(joinId));
    }

    @Operation(
            summary = "Deactivate a join",
            description = "Set the join request as inactive. Requires moderator privileges."
    )
    @PutMapping("/{joinId}/deactivate")
    @PreAuthorize("hasRole('MODERATOR')or hasRole('ADMIN')")
    public ApiResponse<Join> deactivateJoin(@PathVariable Long joinId) {
        return ApiResponse.success("Join deactivated successfully", joinService.deactivateJoin(joinId));
    }

    @Operation(
            summary = "Toggle join active status",
            description = "Switch join status between active/inactive. Requires moderator privileges."
    )
    @PutMapping("/{joinId}/toggle-status")
    @PreAuthorize("hasRole('MODERATOR')or hasRole('ADMIN')")
    public ApiResponse<Join> changeJoinActiveStatus(@PathVariable Long joinId) {
        return ApiResponse.success("Join status toggled successfully", joinService.changeJoinActiveStatus(joinId));
    }
    @Operation(
            summary = "Moderator assign student to group",
            description = "Moderator can directly add a student to a group without voting."
    )
    @PutMapping("/assign/{groupId}/{studentId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ApiResponse<Void> assignStudentToGroup(
            @PathVariable Long groupId,
            @PathVariable Long studentId
    ) {
        joinService.assignStudentToGroup(groupId, studentId);
        return ApiResponse.success("Student assigned to group successfully", null);
    }
}