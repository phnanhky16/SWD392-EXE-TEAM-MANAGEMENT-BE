package com.swd.exe.teammanagement.config;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import com.swd.exe.teammanagement.repository.GroupRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {
    
    private final UserRepository userRepository;
    
    @Override
    public void run(String... args) {
        initDefaultAdmin();
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
}
