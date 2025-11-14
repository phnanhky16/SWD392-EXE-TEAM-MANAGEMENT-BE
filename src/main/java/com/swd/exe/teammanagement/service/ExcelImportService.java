package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.entity.WhitelistEmail;
import com.swd.exe.teammanagement.enums.user.UserRole;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelImportService {
    
    /**
     * Import whitelist emails from Excel file
     * @param file Excel file containing email list
     * @param semesterId ID of the semester
     * @param role Role to assign to imported users
     * @return Number of emails imported
     */
    String importFromExcel(MultipartFile file, Long semesterId, UserRole role);
    
    /**
     * Check if email is in whitelist and active
     * @param email Email to check
     * @return true if whitelisted and active
     */
    boolean isEmailWhitelisted(String email);
    
    /**
     * Get whitelist email entity
     * @param email Email to find
     * @return WhitelistEmail entity
     */
    WhitelistEmail getWhitelistEmail(String email);
    
    /**
     * Remove email from whitelist (soft delete)
     * @param email Email to remove
     * @return Success message
     */
    String removeFromWhitelist(String email);
    
    /**
     * Clear all whitelist entries for a semester and role (HARD DELETE)
     * @param semesterId Semester ID
     * @param role User role
     * @return Success message
     */
    String clearWhitelist(Long semesterId, UserRole role);
    
    /**
     * Get all whitelist emails for a semester
     * @param semesterId Semester ID
     * @return List of whitelist emails
     */
    List<WhitelistEmail> getWhitelistBySemester(Long semesterId);
}
