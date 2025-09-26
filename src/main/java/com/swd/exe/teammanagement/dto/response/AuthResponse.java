package com.swd.exe.teammanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String uid;
    private String email;
    private String token; // JWT backend
}

