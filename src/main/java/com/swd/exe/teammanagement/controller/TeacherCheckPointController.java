package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.service.TeacherCheckPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            summary = "Assign teacher to group",
            description = "Assign a teacher/lecturer to a specific group. The system will check if the teacher is already overloaded based on semester workload distribution. Each teacher should have roughly equal number of groups per semester."
    )
    @PostMapping("/groups/{groupId}/teachers/{teacherId}")
    public ApiResponse<Void> assignTeacherToGroup(
            @PathVariable Long groupId,
            @PathVariable Long teacherId
    ) {
        teacherCheckPointService.assignTeacherToGroup(groupId, teacherId);
        return ApiResponse.created("Teacher assigned to group successfully", null);
    }
}
