package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.TokenRequest;
import com.swd.exe.teammanagement.dto.response.AuthResponse;
import com.swd.exe.teammanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Auth {
    AuthService authService;
    @PostMapping("/google-login")
    public ApiResponse<AuthResponse> googleLogin(@Valid @RequestBody TokenRequest request) {
        var data = authService.loginWithGoogle(request.getIdToken());
        return ApiResponse.<AuthResponse>builder()
                .message("Google login success")
                .result(data)
                .success(true)
                .build();
    }
}
