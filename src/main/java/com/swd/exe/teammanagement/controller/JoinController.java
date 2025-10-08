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
    public ApiResponse<Void> cancelJoinRequest(@PathVariable Long joinId) {
        joinService.cancelJoinRequest(joinId);
        return ApiResponse.success("Join request cancelled successfully", null);
    }
}