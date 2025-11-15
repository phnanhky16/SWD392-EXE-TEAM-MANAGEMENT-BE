package com.swd.exe.teammanagement.config;

import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.repository.MajorRepository;
import com.swd.exe.teammanagement.repository.SemesterRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    private final MajorRepository majorRepository;
    
    @Override
    public void run(String... args) {
        initDefaultAdmin();
        initDefaultSemester();
        initDefaultMajors();
    }

    private void initDefaultAdmin() {
        String adminEmail = "quanlydaotaofpt@gmail.com";
        
        try {
            // Check if admin already exists
            if (userRepository.existsByEmail(adminEmail)) {
                log.info("Default admin already exists: {}", adminEmail);
                
                // Ensure admin is active and has correct role
                userRepository.findByEmail(adminEmail).ifPresent(admin -> {
                    if (admin.getRole() != UserRole.ADMIN || !Boolean.TRUE.equals(admin.getIsActive())) {
                        admin.setRole(UserRole.ADMIN);
                        admin.setIsActive(true);
                        userRepository.save(admin);
                        log.info("Updated existing user to admin: {}", adminEmail);
                    }
                });
            } else {
                // Create new admin
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setFullName("Quản Lý Đào Tạo FPT");
                admin.setRole(UserRole.ADMIN);
                admin.setIsActive(true);
                
                userRepository.save(admin);
                log.info("Created default admin account: {}", adminEmail);
            }
        } catch (Exception e) {
            log.error("Error initializing default admin", e);
        }
    }
    
    private void initDefaultSemester() {
        String semesterName = "SPRING2026";
        
        try {
            // Check if semester already exists
            if (semesterRepository.findByName(semesterName).isPresent()) {
                log.info("Semester already exists: {}", semesterName);
            } else {
                // Create new semester
                Semester semester = Semester.builder()
                        .name(semesterName)
                        .active(true)
                        .isComplete(false)
                        .build();
                
                semesterRepository.save(semester);
                log.info("Created default semester: {}", semesterName);
            }
        } catch (Exception e) {
            log.error("Error initializing default semester", e);
        }
    }
    
    private void initDefaultMajors() {
        List<String> majorNames = Arrays.asList(
                "Kỹ Thuật Phần Mềm",
                "Kinh Doanh Quốc Tế", 
                "Trí Tuệ Nhân Tạo",
                "An Toàn Thông Tin",
                "Marketing",
                "Thiết Kế Đồ Hoạ"
        );
        
        try {
            for (String majorName : majorNames) {
                // Check if major already exists
                if (majorRepository.findByName(majorName).isPresent()) {
                    log.info("Major already exists: {}", majorName);
                } else {
                    // Create new major
                    Major major = Major.builder()
                            .name(majorName)
                            .active(true)
                            .build();
                    
                    majorRepository.save(major);
                    log.info("Created major: {}", majorName);
                }
            }
        } catch (Exception e) {
            log.error("Error initializing default majors", e);
        }
    }
}
