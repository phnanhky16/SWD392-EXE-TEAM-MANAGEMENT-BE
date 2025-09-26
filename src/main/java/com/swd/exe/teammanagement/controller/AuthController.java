package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.config.JwtService;
import com.swd.exe.teammanagement.dto.request.TokenRequest;
import com.swd.exe.teammanagement.dto.response.AuthResponse;
import com.swd.exe.teammanagement.service.FirebaseAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final FirebaseAuthService firebaseAuthService;
    private final JwtService jwtService;

    @PostMapping("/google-login")
    public ResponseEntity<AuthResponse> firebaseLogin(@RequestBody TokenRequest tokenRequest) {
        try {
            // Verify Firebase ID token
            String uid = firebaseAuthService.verifyIdToken(tokenRequest.getIdToken());
            String email = firebaseAuthService.getEmailFromToken(tokenRequest.getIdToken());

            // Tạo JWT backend
            String jwt = jwtService.generateToken(uid, email, Map.of("role", "USER"));

            return ResponseEntity.ok(new AuthResponse(uid, email, jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

