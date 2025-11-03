package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.MajorRequest;
import com.swd.exe.teammanagement.dto.response.MajorResponse;
import com.swd.exe.teammanagement.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/majors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Major Management", description = "APIs for managing academic majors and specializations")
public class MajorController {
    MajorService majorService;
    
    @Operation(
            summary = "Get major by id",
            description = "Retrieve a specific major by its unique id identifier"
    )
    @GetMapping("/{id}")
    public ApiResponse<MajorResponse> getMajorById(@PathVariable Long id) {
        return ApiResponse.success("Get major successfully", majorService.getMajorById(id));
    }
    
    @Operation(
            summary = "Get all majors",
            description = "Retrieve all available majors in the system"
    )
    @GetMapping
    public ApiResponse<List<MajorResponse>> getAllMajors() {
        return ApiResponse.success("Get all majors successfully", majorService.getAllMajors());
    }
    
    @Operation(
            summary = "Create new major",
            description = "Create a new academic major. Requires admin privileges."
    )
    @PostMapping
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') ")
    public ApiResponse<MajorResponse> createMajor(@Valid @RequestBody MajorRequest request) {
        return ApiResponse.created("Create major successfully", majorService.createMajor(request));
    }
    
    @Operation(
            summary = "Update major",
            description = "Update an existing major by id. Requires admin privileges."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR')or hasRole('ADMIN')")
    public ApiResponse<MajorResponse> updateMajor(@PathVariable Long id, @Valid @RequestBody MajorRequest request) {
        return ApiResponse.success("Update major successfully", majorService.updateMajor(id, request));
    }
    
    @Operation(
            summary = "Delete major",
            description = "Delete a major by id. Requires admin privileges."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR')or hasRole('ADMIN')")
    public ApiResponse<String> deleteMajor(@PathVariable Long id) {
        return ApiResponse.success("Delete major successfully", majorService.deleteMajor(id));
    }
    
    @Operation(
            summary = "Activate major",
            description = "Activate a specific major (set active = true). Requires moderator privileges."
    )
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('MODERATOR')or hasRole('ADMIN')")
    public ApiResponse<MajorResponse> activateMajor(@PathVariable Long id) {
        return ApiResponse.success("Activate major successfully", majorService.activateMajor(id));
    }

    @Operation(
            summary = "Deactivate major",
            description = "Deactivate a specific major (set active = false). Requires moderator privileges."
    )
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('MODERATOR')or hasRole('ADMIN')")
    public ApiResponse<MajorResponse> deactivateMajor(@PathVariable Long id) {
        return ApiResponse.success("Deactivate major successfully", majorService.deactivateMajor(id));
    }

    @Operation(
            summary = "Toggle major active status",
            description = "Toggle active status of a major (active ↔ inactive). Requires moderator privileges."
    )
    @PutMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('MODERATOR')or hasRole('ADMIN')")
    public ApiResponse<MajorResponse> toggleMajorActive(@PathVariable Long id) {
        return ApiResponse.success("Toggle major active status successfully", majorService.changeMajorActiveStatus(id));
    }
}
