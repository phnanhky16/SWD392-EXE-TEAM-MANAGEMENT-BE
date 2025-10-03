package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.TokenRequest;
import com.swd.exe.teammanagement.dto.response.AuthResponse;
import com.swd.exe.teammanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication")
public class AuthController {
    AuthService authService;

    @Operation(
            summary = "Login with Google",
            description = "Get ID token from Google and return JWT token"
    )
    @PostMapping("/google-login")
    public ApiResponse<AuthResponse> googleLogin(@Valid @RequestBody TokenRequest request) {
        var data = authService.loginWithGoogle(request.getIdToken());
        return ApiResponse.success("Google login success", data);
    }

}
