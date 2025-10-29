package com.swd.exe.teammanagement.controller;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.GroupCreateRequest;
import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.dto.response.PagingResponse;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;
import com.swd.exe.teammanagement.service.GroupService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

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
    @GetMapping
    public ApiResponse<PagingResponse<GroupResponse>> searchGroups(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) GroupStatus status,
            @RequestParam(required = false) GroupType type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        var data = groupService.searchGroups(q, status, type, page, size, sort, dir);
        return ApiResponse.success("List groups successfully", data);
    }

    @Operation(
            summary = "Get user's group",
            description = "Get the group that a specific user belongs to"
    )
    @GetMapping("/user/{userId}")
    public ApiResponse<GroupResponse> getGroupByUserId(@PathVariable Long userId) {
        return ApiResponse.success("Get user's group successfully", groupService.getGroupByUserId(userId));
    }

    @Operation(
            summary = "Get my group",
            description = "Get the group that the current authenticated user belongs to"
    )
    @GetMapping("/my-group")
    public ApiResponse<GroupResponse> getMyGroup() {
        return ApiResponse.success("Get my group successfully", groupService.getMyGroup());
    }

    @Operation(
            summary = "Get group members",
            description = "Get all members of a specific group by group ID"
    )
    @GetMapping("/{groupId}/members")
    public ApiResponse<List<UserResponse>> getMembersByGroupId(@PathVariable Long groupId) {
        return ApiResponse.success("Get group members successfully", groupService.getMembersByGroupId(groupId));
    }

    @Operation(
            summary = "Get group leader",
            description = "Retrieve the leader of a specific group by group ID"
    )
    @GetMapping("/{groupId}/leader")
    public ApiResponse<UserResponse> getGroupLeader(@PathVariable Long groupId) {
        return ApiResponse.success("Get group leader successfully", groupService.getGroupLeader(groupId));
    }

    @Operation(
            summary = "Get group member count",
            description = "Get the total number of members in a specific group"
    )
    @GetMapping("/{groupId}/members/count")
    public ApiResponse<Integer> getGroupMemberCount(@PathVariable Long groupId) {
        return ApiResponse.success("Get member count successfully", groupService.getGroupMemberCount(groupId));
    }

    @Operation(
            summary = "Get major distribution",
            description = "Get the list of majors represented in a specific group"
    )
    @GetMapping("/{groupId}/majors")
    public ApiResponse<Set<Major>> getMajorDistribution(@PathVariable Long groupId) {
        return ApiResponse.success("Get major distribution successfully", groupService.getMajorDistribution(groupId));
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
            summary = "Get groups by semester",
            description = "Retrieve all groups that belong to a specific semester"
    )
    @GetMapping("/semester/{semesterId}")
    public ApiResponse<List<GroupResponse>> getGroupsBySemester(@PathVariable Long semesterId) {
        return ApiResponse.success("Get groups by semester successfully", groupService.getGroupsBySemester(semesterId));
    }

    @Operation(
            summary = "Change group type",
            description = "Toggle group type between PUBLIC and PRIVATE. Only group leader can perform this action."
    )
    @PatchMapping("/change-type")
    public ApiResponse<Void> changeGroupType() {
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
            summary = "Change group leader",
            description = "Transfer leadership to another member in the group. Only current group leader can perform this action."
    )
    @PatchMapping("/change-leader/{newLeaderId}")
    public ApiResponse<Void> changeLeader(@PathVariable Long newLeaderId) {
        groupService.changeLeader(newLeaderId);
        return ApiResponse.success("Leadership transferred successfully", null);
    }

    @Operation(
            summary = "Leave group",
            description = "Leave the current group. If leader leaves and group has other members, leadership transfers to another member."
    )
    @DeleteMapping("/leave")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Void> leaveGroup() {
        groupService.leaveGroup();
        return ApiResponse.success("Left group successfully", null);
    }

    @Operation(
            summary = "Remove member from group",
            description = "Remove a member from the group. Only group leader can perform this action."
    )
    @DeleteMapping("/members/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Void> removeMember(@PathVariable Long userId) {
        groupService.removeMemberByLeader(userId);
        return ApiResponse.success("Member removed successfully", null);
    }

    @Operation(
            summary = "Update group information",
            description = "Update title and description of the group. Only group leader can perform this action."
    )
    @PutMapping("/update")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<GroupResponse> updateGroupInfo(@Valid @RequestBody GroupCreateRequest request) {
        return ApiResponse.success("Group information updated successfully", groupService.updateGroupInfo(request));
    }

    @Operation(
            summary = "Create empty groups",
            description = "Create N empty groups. Title formatted like 'Group EXE FALL 2025 #1'. Requires authentication."
    )
    @PostMapping
    @PreAuthorize("hasRole('MODERATOR')")
    public ApiResponse<Void> createGroups(@RequestParam(name = "size", defaultValue = "1") int size,
                                        @RequestParam(name = "semesterId") Long semesterId) {
        groupService.createGroup(size,semesterId);
        return ApiResponse.created("Created empty groups successfully", null);
    }
    @GetMapping("/my-assigned")
    public ApiResponse<Page<GroupResponse>> getMyAssignedGroups(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "history", defaultValue = "false") boolean includeHistory
    ) {
        return ApiResponse.success(
                "Get assigned groups successfully",
                groupService.getMyAssignedGroups(page, size, includeHistory)
        );
    }
}
