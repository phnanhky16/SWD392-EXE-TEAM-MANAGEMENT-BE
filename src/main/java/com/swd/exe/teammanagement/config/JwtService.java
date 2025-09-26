package com.swd.exe.teammanagement.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private static final String SECRET_KEY = "YOUR_SECRET_KEY_MIN_32_CHARACTERS_LONG";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Tạo JWT
    public String generateToken(String uid, String email, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .setSubject(uid)
                .addClaims(extraClaims)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Lấy email từ JWT
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    // Kiểm tra token còn hạn hay không
    public boolean isTokenValid(String token) {
        return !extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
