package com.swd.exe.teammanagement.config;

import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.user.UserRole;
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

    @Override
    public void run(String... args) {
        initUsers();
    }

    private void initUsers() {
        log.info("Initializing default users...");

        // Danh sách email cần kiểm tra
        List<String> requiredEmails = Arrays.asList(
            "quanlydaotaofpt@gmail.com",
            "teacher1@fe.edu.vn",
            "teacher2@fe.edu.vn", 
            "teacher3@fe.edu.vn",
            "teacher4@fe.edu.vn",
            "teacher5@fe.edu.vn"
        );

        // Kiểm tra xem có thiếu email nào không
        List<String> existingEmails = userRepository.findAllEmailsByEmailIn(requiredEmails);
        List<String> missingEmails = requiredEmails.stream()
            .filter(email -> !existingEmails.contains(email))
            .toList();

        if (missingEmails.isEmpty()) {
            log.info("All required default users already exist, skipping initialization");
            return;
        }

        log.info("Creating missing default users: {}", missingEmails);

        List<User> defaultUsers = Arrays.asList(
            // Admin user
            createUser("quanlydaotaofpt@gmail.com", "System Administrator", UserRole.ADMIN),
            // Teacher users
            createUser("teacher1@fe.edu.vn", "Nguyễn Văn An", UserRole.TEACHER),
            createUser("teacher2@fe.edu.vn", "Trần Thị Bình", UserRole.TEACHER),
            createUser("teacher3@fe.edu.vn", "Lê Văn Cường", UserRole.TEACHER),
            createUser("teacher4@fe.edu.vn", "Phạm Thị Dung", UserRole.TEACHER),
            createUser("teacher5@fe.edu.vn", "Hoàng Văn Em", UserRole.TEACHER)
        );

        userRepository.saveAll(defaultUsers);
        log.info("Successfully initialized {} default users", defaultUsers.size());
    }

    private User createUser(String email, String fullName, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);
        user.setIsActive(true);
        return user;
    }
}
