package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse loginWithGoogle(String idToken);
}
