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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/majors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Major Management", description = "APIs for managing academic majors and specializations")
public class MajorController {
    MajorService majorService;
    
    @Operation(
            summary = "Get major by code",
            description = "Retrieve a specific major by its unique code identifier"
    )
    @GetMapping("/{code}")
    public ApiResponse<MajorResponse> getMajorByCode(@PathVariable String code) {
        return ApiResponse.success("Get major successfully", majorService.getMajorByCode(code));
    }
    
    @Operation(
            summary = "Get all majors",
            description = "Retrieve all available majors in the system"
    )
    @GetMapping("/")
    public ApiResponse<List<MajorResponse>> getAllMajors() {
        return ApiResponse.success("Get all majors successfully", majorService.getAllMajors());
    }
    
    @Operation(
            summary = "Create new major",
            description = "Create a new academic major. Requires admin privileges."
    )
    @PostMapping("/")
    public ApiResponse<MajorResponse> createMajor(@Valid @RequestBody MajorRequest request) {
        return ApiResponse.created("Create major successfully", majorService.createMajor(request));
    }
    
    @Operation(
            summary = "Update major",
            description = "Update an existing major by code. Requires admin privileges."
    )
    @PutMapping("/{code}")
    public ApiResponse<MajorResponse> updateMajor(@PathVariable String code, @Valid @RequestBody MajorRequest request) {
        return ApiResponse.success("Update major successfully", majorService.updateMajor(code, request));
    }
    
    @Operation(
            summary = "Delete major",
            description = "Delete a major by code. Requires admin privileges."
    )
    @DeleteMapping("/{code}")
    public ApiResponse<Void> deleteMajor(@PathVariable String code) {
        majorService.deleteMajor(code);
        return ApiResponse.success("Delete major successfully", null);
    }
}
