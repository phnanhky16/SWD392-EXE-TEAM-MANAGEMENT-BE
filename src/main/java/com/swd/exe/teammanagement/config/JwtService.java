package com.swd.exe.teammanagement.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key key;

    public JwtService(
            @Value("${SECRET_KEY}") String secret,
            // nếu bạn lưu secret dạng Base64, đặt app.jwt.secret-base64=true
            @Value("${BASE_64:false}") boolean isBase64
    ) {
        byte[] bytes = isBase64 ? Decoders.BASE64.decode(secret) : secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(String uid, Long id, String email, String role, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + 24 * 60 * 60 * 1000L); // 24h
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .addClaims(extraClaims == null ? Map.of() : extraClaims)
                .claim("uid", uid)
                .claim("email", email)
                .claim("role", role) // ví dụ: ADMIN/TEACHER/MODERATOR
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public boolean isTokenValid(String token) { return parse(token).getExpiration().after(new Date()); }
    public Long extractUserId(String token) {
        String sub = parse(token).getSubject(); // "2"
        try {
            return Long.parseLong(sub);
        } catch (NumberFormatException e) {
            throw new JwtException("Subject is not a numeric user id: " + sub);
        }
    }
}
