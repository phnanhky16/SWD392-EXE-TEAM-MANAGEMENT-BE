package com.swd.exe.teammanagement.service.impl;

import com.google.firebase.auth.FirebaseAuthException;
import com.swd.exe.teammanagement.config.JwtService;
import com.swd.exe.teammanagement.dto.response.AuthResponse;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.AuthService;
import com.swd.exe.teammanagement.service.FirebaseAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final FirebaseAuthService firebaseAuthService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public AuthResponse loginWithGoogle(String idToken) {
        try {

            var info  = firebaseAuthService.verify(idToken); // uid, email, name
            String email = info.email();

            // Lấy phần trước @
            String localPart = email.substring(0, email.indexOf('@'));
            // Lấy 8 ký tự cuối của localPart (ngay trước @). Nếu < 8 thì lấy hết
            String studentCode = localPart.length() <= 8
                    ? localPart
                    : localPart.substring(localPart.length() - 8);

            // Tên hiển thị: ưu tiên name từ token, fallback localPart
            String fullName = (info.name() != null && !info.name().isBlank())
                    ? info.name()
                    : localPart;

            String pictureUrl = info.pictureUrl();
            // Tìm user theo email, nếu không có thì tạo mới
            if (!userRepository.existsByEmail(email)) {
                        User u = new User();
                        u.setEmail(email);
                        u.setFullName(fullName);
                        u.setStudentCode(studentCode);
                        u.setAvatarUrl(pictureUrl);
                        u.setRole(UserRole.STUDENT);
                        u.setIsActive(true);
                        userRepository.save(u);
                    }

            // 3) Payload tuỳ bạn muốn nhét gì thêm
            Map<String, Object> claims = Map.of(
                    "role", /* user.getRole() != null ? user.getRole().name() : */ "USER",
                    "uid", info.uid()
            );

            // 4) Sinh JWT và trả về
            String jwt = jwtService.generateToken(info.uid(), info.email(), claims);

            return AuthResponse.builder()
                    .email(info.email())
                    .token(jwt)
                    .build();

        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google token", e);
        }
    }
}
