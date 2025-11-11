package com.swd.exe.teammanagement.service.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.auth.FirebaseAuthException;
import com.swd.exe.teammanagement.config.JwtService;
import com.swd.exe.teammanagement.dto.response.AuthResponse;
import com.swd.exe.teammanagement.entity.Notification;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.entity.WhitelistEmail;
import com.swd.exe.teammanagement.enums.notification.NotificationStatus;
import com.swd.exe.teammanagement.enums.notification.NotificationType;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.NotificationRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.AuthService;
import com.swd.exe.teammanagement.service.ExcelImportService;
import com.swd.exe.teammanagement.service.FirebaseAuthService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    FirebaseAuthService firebaseAuthService;
    UserRepository userRepository;
    JwtService jwtService;
    NotificationRepository notificationRepository;
    ExcelImportService excelImportService;

    @Transactional
    @Override
    public AuthResponse loginWithGoogle(String idToken) {
        try {
            FirebaseAuthService.FirebaseUserInfo info = firebaseAuthService.verify(idToken);
            String email = info.email();

            validateEmail(email);

            // Check if user exists and is active
            User user = userRepository.findByEmail(email)
                    .map(existingUser -> {
                        // If user is active, allow login directly without checking whitelist
                        if (Boolean.TRUE.equals(existingUser.getIsActive())) {
                            log.info("Active user logging in: {}", email);
                            return existingUser;
                        }
                        
                        // If user is inactive, must check whitelist to reactivate
                        log.info("Inactive user attempting login, checking whitelist: {}", email);
                        WhitelistEmail whitelistEmail = excelImportService.getWhitelistEmail(email);
                        if (whitelistEmail == null) {
                            log.warn("Inactive user not in whitelist: {}", email);
                            throw new AppException(ErrorCode.EMAIL_NOT_WHITELISTED);
                        }
                        
                        // Reactivate user and add new semester if needed
                        if (whitelistEmail.getSemester() != null 
                            && !existingUser.getSemesters().contains(whitelistEmail.getSemester())) {
                            existingUser.getSemesters().add(whitelistEmail.getSemester());
                        }
                        existingUser.setIsActive(true);
                        userRepository.save(existingUser);
                        log.info("User reactivated: {}", email);
                        return existingUser;
                    })
                    .orElseGet(() -> {
                        // New user must be in whitelist
                        log.info("New user attempting login, checking whitelist: {}", email);
                        WhitelistEmail whitelistEmail = excelImportService.getWhitelistEmail(email);
                        if (whitelistEmail == null) {
                            log.warn("New user not in whitelist: {}", email);
                            throw new AppException(ErrorCode.EMAIL_NOT_WHITELISTED);
                        }
                        return createNewUser(info, whitelistEmail);
                    });

            // Generate token
            String jwt = generateJwtToken(info, user);

            log.info("User authenticated successfully: {} with role: {}", email, user.getRole());

            return AuthResponse.builder()
                    .email(email)
                    .token(jwt)
                    .build();

        } catch (FirebaseAuthException e) {
            log.error("Firebase authentication failed", e);
            throw new AppException(ErrorCode.INVALID_GG_TOKEN);
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new AppException(ErrorCode.EMAIL_INVALID_FORMAT);
        }
    }

    private User createNewUser(FirebaseAuthService.FirebaseUserInfo firebaseInfo, WhitelistEmail whitelistEmail) {
        String email = firebaseInfo.email();
        
        // Use information from WhitelistEmail (from Excel import)
        String fullName = whitelistEmail.getFullName() != null && !whitelistEmail.getFullName().isEmpty() 
                ? whitelistEmail.getFullName() 
                : firebaseInfo.name();
        
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setAvatarUrl(firebaseInfo.pictureUrl());
        newUser.setRole(whitelistEmail.getRole());
        newUser.setStudentCode(whitelistEmail.getStudentCode());
        newUser.setIsActive(true);

        // Set student code from Excel if available
        if (whitelistEmail.getRole() == UserRole.STUDENT && whitelistEmail.getStudentCode() != null) {
            newUser.setStudentCode(whitelistEmail.getStudentCode().toUpperCase());
        }
        
        // Add user to the semester list from whitelist
        if (whitelistEmail.getSemester() != null) {
            newUser.getSemesters().add(whitelistEmail.getSemester());
        }
        
        sendNotification(newUser, "Update your major", NotificationType.SYSTEM);
        return userRepository.save(newUser);
    }

    private String generateJwtToken(FirebaseAuthService.FirebaseUserInfo firebaseInfo, User user) {
        Map<String, Object> claims = Map.of(
                "role", user.getRole().name(),
                "uid", firebaseInfo.uid()
        );
        return jwtService.generateToken(
            firebaseInfo.uid(),
            user.getId(),
            firebaseInfo.email(),
            user.getRole().name(),
            claims
        );
    }

    private void sendNotification(User user, String content, NotificationType type) {
        notificationRepository.save(Notification.builder()
                .receiver(user)
                .content(content)
                .type(type)
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build());
    }
}