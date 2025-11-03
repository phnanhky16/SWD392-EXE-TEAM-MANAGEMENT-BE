package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.entity.WhitelistEmail;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.SemesterRepository;
import com.swd.exe.teammanagement.repository.WhitelistEmailRepository;
import com.swd.exe.teammanagement.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelImportServiceImpl implements ExcelImportService {
    
    private final WhitelistEmailRepository whitelistEmailRepository;
    private final SemesterRepository semesterRepository;
    
    @Override
    @Transactional
    public String importFromExcel(MultipartFile file, Long semesterId, UserRole role) {
        // Validate file
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_REQUIRED);
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            throw new AppException(ErrorCode.INVALID_FILE_FORMAT);
        }
        
        // Get semester
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_NOT_FOUND));
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<WhitelistEmail> whitelistEmails = new ArrayList<>();
            
            // Skip header row (row 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                // Expected columns: Email | Full Name | Student Code (optional)
                String email = getCellValueAsString(row.getCell(0));
                String fullName = getCellValueAsString(row.getCell(1));
                String studentCode = getCellValueAsString(row.getCell(2));
                
                if (email == null || email.trim().isEmpty()) {
                    log.warn("Skipping row {} - empty email", i);
                    continue;
                }
                
                email = email.trim().toLowerCase();
                
                // Check if already exists for this semester
                boolean exists = whitelistEmailRepository.existsByEmailAndIsActiveTrue(email);
                if (exists) {
                    log.info("Email {} already in whitelist, skipping", email);
                    continue;
                }
                
                WhitelistEmail whitelistEmail = WhitelistEmail.builder()
                        .email(email)
                        .fullName(fullName != null ? fullName.trim() : null)
                        .studentCode(studentCode != null ? studentCode.trim() : null)
                        .role(role)
                        .semester(semester)
                        .isActive(true)
                        .build();
                
                whitelistEmails.add(whitelistEmail);
            }
            
            if (whitelistEmails.isEmpty()) {
                throw new AppException(ErrorCode.NO_VALID_DATA);
            }
            
            whitelistEmailRepository.saveAll(whitelistEmails);
            
            log.info("Imported {} emails for semester {} with role {}", 
                    whitelistEmails.size(), semester.getName(), role);
            
            return "Successfully imported " + whitelistEmails.size() + " emails";
            
        } catch (IOException e) {
            log.error("Error reading Excel file", e);
            throw new AppException(ErrorCode.FILE_READ_ERROR);
        }
    }
    
    @Override
    public boolean isEmailWhitelisted(String email) {
        return whitelistEmailRepository.existsByEmailAndIsActiveTrue(email.toLowerCase());
    }
    
    @Override
    public WhitelistEmail getWhitelistEmail(String email) {
        return whitelistEmailRepository.findByEmailAndIsActiveTrue(email.toLowerCase())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_WHITELISTED));
    }
    
    @Override
    @Transactional
    public String removeFromWhitelist(String email) {
        WhitelistEmail whitelistEmail = getWhitelistEmail(email);
        whitelistEmail.setIsActive(false);
        whitelistEmailRepository.save(whitelistEmail);
        
        log.info("Removed email {} from whitelist", email);
        return "Successfully removed email from whitelist";
    }
    
    @Override
    @Transactional
    public String clearWhitelist(Long semesterId, UserRole role) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_NOT_FOUND));
        
        List<WhitelistEmail> whitelistEmails = whitelistEmailRepository
                .findBySemesterAndRoleAndIsActiveTrue(semester, role);
        
        int count = whitelistEmails.size();
        
        // Hard delete - xóa hẳn khỏi database
        whitelistEmailRepository.deleteAll(whitelistEmails);
        
        log.info("Permanently deleted {} whitelist entries for semester {} and role {}", 
                count, semester.getName(), role);
        
        return "Successfully deleted " + count + " whitelist entries";
    }
    
    @Override
    public List<WhitelistEmail> getWhitelistBySemester(Long semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_NOT_FOUND));
        
        return whitelistEmailRepository.findBySemesterAndIsActiveTrue(semester);
    }
    
    /**
     * Helper method to safely get cell value as String
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
