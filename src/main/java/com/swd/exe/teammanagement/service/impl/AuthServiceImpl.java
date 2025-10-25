package com.swd.exe.teammanagement.service.impl;

import com.google.firebase.auth.FirebaseAuthException;
import com.swd.exe.teammanagement.config.JwtService;
import com.swd.exe.teammanagement.dto.response.AuthResponse;
import com.swd.exe.teammanagement.entity.Notification;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.notification.NotificationStatus;
import com.swd.exe.teammanagement.enums.notification.NotificationType;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.NotificationRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.AuthService;
import com.swd.exe.teammanagement.service.FirebaseAuthService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    FirebaseAuthService firebaseAuthService;
    UserRepository userRepository;
    JwtService jwtService;
    NotificationRepository notificationRepository;

    @Transactional
    @Override
    public AuthResponse loginWithGoogle(String idToken) {
        try {
            FirebaseAuthService.FirebaseUserInfo info = firebaseAuthService.verify(idToken);
            String email = info.email();

            validateEmail(email);

            // Get existing user or create new one
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> createNewUser(info));

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

    private User createNewUser(FirebaseAuthService.FirebaseUserInfo firebaseInfo) {
        String email = firebaseInfo.email();
        EmailParts emailParts = parseEmail(email);
        UserRole role = determineRole(emailParts.domain(), email);
        String fullName = getDisplayName(firebaseInfo.name(), emailParts.localPart());
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setAvatarUrl(firebaseInfo.pictureUrl());
        newUser.setRole(role);
        newUser.setIsActive(true);

        // Set student code only for students from FPT or FE domain
        if (role == UserRole.STUDENT && isEducationalDomain(emailParts.domain())) {
            String studentCode = extractStudentCode(emailParts.localPart());
            newUser.setStudentCode(studentCode.toUpperCase());
        }
        sendNotification(newUser,"Update your major",NotificationType.SYSTEM);
        return userRepository.save(newUser);

    }

    private EmailParts parseEmail(String email) {
        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1).toLowerCase();
        return new EmailParts(localPart, domain);
    }

    private UserRole determineRole(String domain, String email) {
        if ("quanlydaotaofpt@gmail.com".equalsIgnoreCase(email)) {
            return UserRole.ADMIN;
        }

        if ("fe.edu.vn".equals(domain)) {
            return UserRole.LECTURER;
        }

        return UserRole.STUDENT;
    }

    private boolean isEducationalDomain(String domain) {
        return "fpt.edu.vn".equals(domain) || "fe.edu.vn".equals(domain);
    }

    private String getDisplayName(String firebaseName, String localPart) {
        return (firebaseName != null && !firebaseName.isBlank())
                ? firebaseName
                : localPart;
    }

    private String extractStudentCode(String localPart) {
        return localPart.length() <= 8
                ? localPart
                : localPart.substring(localPart.length() - 8);
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

    /**
     * Record to hold email components
     */
    private record EmailParts(String localPart, String domain) {}
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