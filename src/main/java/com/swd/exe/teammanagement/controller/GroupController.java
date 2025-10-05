package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Group Management", description = "APIs for managing student groups and team formation")
public class GroupController {
    GroupService groupService;

    @Operation(
            summary = "Get group by ID",
            description = "Retrieve a specific group by its unique identifier"
    )
    @GetMapping("/{id}")
    public ApiResponse<GroupResponse> getGroupById(@PathVariable Long id) {
        return ApiResponse.success("Get group successfully", groupService.getGroupById(id));
    }

    @Operation(
            summary = "Get all groups",
            description = "Retrieve all groups in the system (admin/teacher access)"
    )
    @GetMapping("/")
    public ApiResponse<List<GroupResponse>> getAllGroups() {
        return ApiResponse.success("Get all groups successfully", groupService.getAllGroups());
    }

    @Operation(
            summary = "Get user's group",
            description = "Get the group that a specific user belongs to"
    )
    @GetMapping("/user/{userId}")
    public ApiResponse<GroupResponse> getGroupByUserId(@PathVariable Long userId) {
        return ApiResponse.success("Get user's group successfully", groupService.getGroup(userId));
    }

    @Operation(
            summary = "Get available groups",
            description = "Get list of groups that current user can join (same major, not full, etc.)"
    )
    @GetMapping("/available")
    public ApiResponse<List<GroupResponse>> getAvailableGroups() {
        return ApiResponse.success("Get available groups successfully", groupService.getAvailableGroups());
    }

    @Operation(
            summary = "Change group type",
            description = "Toggle group type between PUBLIC and PRIVATE. Only group leader can perform this action."
    )
    @PatchMapping("/change-type")
    public ApiResponse<GroupResponse> changeGroupType() {
        return ApiResponse.success("Change group type successfully", groupService.changeGroupType());
    }

    @Operation(
            summary = "Finalize team",
            description = "Lock the team when it has exactly 6 members. Only group leader can perform this action."
    )
    @PatchMapping("/done")
    public ApiResponse<Void> doneTeam() {
        groupService.doneTeam();
        return ApiResponse.success("Team finalized successfully", null);
    }

    @Operation(
            summary = "Leave group",
            description = "Leave the current group. Group leader cannot leave the group."
    )
    @DeleteMapping("/leave")
    public ApiResponse<Void> leaveGroup() {
        groupService.leaveGroup();
        return ApiResponse.success("Left group successfully", null);
    }

    @Operation(
            summary = "Delete group",
            description = "Delete the entire group including all members, posts, and ideas. Only group leader can perform this action."
    )
    @DeleteMapping("/")
    public ApiResponse<Void> deleteGroup() {
        groupService.deleteGroup();
        return ApiResponse.success("Delete group successfully", null);
    }
    @Operation(
            summary = "Create empty groups",
            description = "Create N empty groups. Title formatted like 'Group EXE FALL 2025 #1'. Requires authentication."
    )
    @PostMapping("/")
    public ApiResponse<Void> createGroups(@RequestParam(name = "size", defaultValue = "1") int size) {
        groupService.createGroup(size);
        return ApiResponse.created("Created empty groups successfully", null);
    }
}
