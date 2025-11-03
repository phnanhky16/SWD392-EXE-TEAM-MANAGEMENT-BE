package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.entity.WhitelistEmail;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.service.ExcelImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Excel Import", description = "APIs for importing whitelist emails from Excel files")
public class ExcelImportController {
    
    ExcelImportService excelImportService;
    
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Import whitelist from Excel file", 
               description = "Upload an Excel file to import email whitelist for a semester. Excel format: Column A = Email, Column B = Full Name, Column C = Student Code (optional)")
    public ApiResponse<String> importFromExcel(
            @Parameter(description = "Excel file (.xlsx)", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Semester ID", required = true, example = "1")
            @RequestParam("semesterId") Long semesterId,
            
            @Parameter(description = "User role for imported emails", required = true, example = "STUDENT")
            @RequestParam("role") UserRole role) {
        
        String result = excelImportService.importFromExcel(file, semesterId, role);
        return ApiResponse.success("Import whitelist successfully", result);
    }
    
    @GetMapping("/whitelist")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'LECTURER')")
    @Operation(summary = "Get all whitelist emails for a semester",
               description = "Retrieve all active whitelist emails for a specific semester")
    public ApiResponse<List<WhitelistEmail>> getWhitelistBySemester(
            @Parameter(description = "Semester ID", required = true, example = "1")
            @RequestParam("semesterId") Long semesterId) {
        
        List<WhitelistEmail> whitelist = excelImportService.getWhitelistBySemester(semesterId);
        return ApiResponse.success("Get whitelist successfully", whitelist);
    }
    
    @GetMapping("/whitelist/check")
    @Operation(summary = "Check if email is whitelisted",
               description = "Check if a specific email is in the active whitelist")
    public ApiResponse<Boolean> checkEmailWhitelisted(
            @Parameter(description = "Email address to check", required = true, example = "student@fpt.edu.vn")
            @RequestParam("email") String email) {
        
        boolean isWhitelisted = excelImportService.isEmailWhitelisted(email);
        return ApiResponse.success("Check email successfully", isWhitelisted);
    }
    
    @DeleteMapping("/whitelist")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Remove email from whitelist",
               description = "Soft delete an email from the whitelist (set isActive = false)")
    public ApiResponse<String> removeFromWhitelist(
            @Parameter(description = "Email address to remove", required = true, example = "student@fpt.edu.vn")
            @RequestParam("email") String email) {
        
        String result = excelImportService.removeFromWhitelist(email);
        return ApiResponse.success("Remove email successfully", result);
    }
    
    @DeleteMapping("/whitelist/clear")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Clear all whitelist entries for a semester and role",
               description = "WARNING: PERMANENTLY DELETE all whitelist entries for a specific semester and role. This is a HARD DELETE operation and CANNOT be recovered. Use with caution!")
    public ApiResponse<String> clearWhitelist(
            @Parameter(description = "Semester ID", required = true, example = "1")
            @RequestParam("semesterId") Long semesterId,
            
            @Parameter(description = "User role to clear", required = true, example = "STUDENT")
            @RequestParam("role") UserRole role) {
        
        String result = excelImportService.clearWhitelist(semesterId, role);
        return ApiResponse.success("Whitelist permanently deleted", result);
    }
}
