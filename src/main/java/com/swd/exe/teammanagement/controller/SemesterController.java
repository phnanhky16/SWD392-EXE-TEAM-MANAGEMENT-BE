package com.swd.exe.teammanagement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.SemesterRequest;
import com.swd.exe.teammanagement.dto.response.SemesterResponse;
import com.swd.exe.teammanagement.service.SemesterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Semester Management", description = "APIs for managing academic semesters and semester registrations")
public class SemesterController {
    SemesterService semesterService;

    // ========== SEMESTER CRUD OPERATIONS ==========

    @Operation(
            summary = "Get all semesters",
            description = "Retrieve all semesters in the system with their active status"
    )
    @GetMapping
    public ApiResponse<List<SemesterResponse>> getAllSemesters() {
        return ApiResponse.success("Get all semesters successfully", semesterService.getAllSemesters());
    }

    @Operation(
            summary = "Get semester by ID",
            description = "Retrieve a specific semester by its unique identifier"
    )
    @GetMapping("/{id}")
    public ApiResponse<SemesterResponse> getSemesterById(@PathVariable Long id) {
        return ApiResponse.success("Get semester successfully", semesterService.getSemesterById(id));
    }

    @Operation(
            summary = "Create new semester",
            description = "Create a new academic semester. Name should be unique (e.g., SPRING, SUMMER, FALL)"
    )
    @PostMapping
    public ApiResponse<SemesterResponse> createSemester(@Valid @RequestBody SemesterRequest request) {
        return ApiResponse.created("Create semester successfully", semesterService.createSemester(request));
    }

    @Operation(
            summary = "Update semester",
            description = "Update semester information including name and active status"
    )
    @PutMapping("/{id}")
    public ApiResponse<SemesterResponse> updateSemester(
            @PathVariable Long id,
            @Valid @RequestBody SemesterRequest request
    ) {
        return ApiResponse.success("Update semester successfully", semesterService.updateSemester(id, request));
    }

    @Operation(
            summary = "Change active semester",
            description = "Set a semester as active/inactive. Only one semester should be active at a time."
    )
    @PatchMapping("/{id}/active")
    public ApiResponse<Void> changeActiveSemester(@PathVariable Long id) {
        semesterService.changeActiveSemester(id);
        return ApiResponse.success("Change active semester successfully", null);
    }

}
