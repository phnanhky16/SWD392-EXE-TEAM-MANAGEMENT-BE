package com.swd.exe.teammanagement.controller;

import java.util.List;

import com.swd.exe.teammanagement.dto.response.TeacherRequestResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.entity.TeacherRequest;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.service.TeacherCheckPointService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/teacher-checkpoints")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Teacher Checkpoint Management", description = "APIs for managing teacher assignments to groups and checkpoints")
public class TeacherCheckPointController {
    TeacherCheckPointService teacherCheckPointService;

    @Operation(
            summary = "Get all teachers",
            description = "Retrieve all users with LECTURER role for teacher assignment purposes"
    )
    @GetMapping("/teachers")
    public ApiResponse<List<User>> getAllTeachers() {
        return ApiResponse.success("Get all teachers successfully", 
                teacherCheckPointService.getAllTeachers());
    }

    @Operation(
            summary = "Request teacher to group",
            description = "Group leader sends a request to assign a teacher/lecturer to their group. Group must be LOCKED status and not have a teacher yet."
    )
    @PostMapping("/teachers/{teacherId}")
    public ApiResponse<Void> assignTeacherToGroup(@PathVariable Long teacherId) {
        teacherCheckPointService.assignTeacherToGroup(teacherId);
        return ApiResponse.created("Teacher request sent successfully", null);
    }

    @Operation(
            summary = "Moderator assigns teacher to group",
            description = "Moderator directly assigns a teacher to a group without request-approval flow. System ensures even distribution of groups among teachers in the same semester. Only MODERATOR role can use this endpoint."
    )
    @PostMapping("/moderator/groups/{groupId}/teachers/{teacherId}")
    public ApiResponse<Void> moderatorAssignTeacherToGroup(
            @PathVariable Long groupId,
            @PathVariable Long teacherId
    ) {
        teacherCheckPointService.moderatorAssignTeacherToGroup(groupId, teacherId);
        return ApiResponse.created("Teacher assigned to group successfully", null);
    }

    @Operation(
            summary = "Teacher responds to group request",
            description = "Teacher accepts or rejects a request to be assigned to a group. System checks for teacher overload before accepting."
    )
    @PatchMapping("/requests/{requestId}")
    public ApiResponse<Void> teacherResponseToGroup(
            @PathVariable Long requestId,
            @RequestParam boolean isAccepted
    ) {
        teacherCheckPointService.teacherResponseToGroup(requestId, isAccepted);
        return ApiResponse.success("Response submitted successfully", null);
    }

    @Operation(
            summary = "Get pending requests for teacher",
            description = "Retrieve all pending teacher assignment requests for the current authenticated teacher"
    )
    @GetMapping("/requests/pending")
    public ApiResponse<List<TeacherRequest>> getPendingRequestsForTeacher() {
        return ApiResponse.success("Get pending requests successfully", 
                teacherCheckPointService.getPendingRequestsForTeacher());
    }

    @Operation(
            summary = "Get rejected groups",
            description = "Retrieve all groups that the current teacher has rejected and are still without a teacher"
    )
    @GetMapping("/groups/rejected")
    public ApiResponse<List<GroupResponse>> getGroupsRejected() {
        return ApiResponse.success("Get rejected groups successfully", 
                teacherCheckPointService.getGroupsRejected());
    }

    @Operation(
            summary = "Get unregistered groups",
            description = "Retrieve all LOCKED groups that the current teacher has not yet requested to be assigned to and are still without a teacher"
    )
    @GetMapping("/groups/unregistered")
    public ApiResponse<List<GroupResponse>> getGroupsUnregistered() {
        return ApiResponse.success("Get unregistered groups successfully", 
                teacherCheckPointService.getGroupsUnregistered());
    }
    
    @Operation(
            summary = "Get accepted groups",
            description = "Retrieve all groups where the current teacher's assignment request has been accepted (RequestStatus = ACCEPTED)"
    )
    @GetMapping("/groups/accepted")
    public ApiResponse<List<GroupResponse>> getGroupsAccepted() {
        return ApiResponse.success("Get accepted groups successfully", 
                teacherCheckPointService.getGroupsAccepted());
    }
    @GetMapping("/groups/{groupId}/my-request")
    public ApiResponse<TeacherRequestResponse> getMyRequest(@PathVariable Long groupId) {
        return ApiResponse.success("Get my group request successfully", teacherCheckPointService.getMyRequestTeacherCheckpoints(groupId));
    }
}
