package com.swd.exe.teammanagement.config;

import com.swd.exe.teammanagement.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // 1) Bỏ qua preflight CORS
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // 2) Bỏ qua các endpoint public (đúng path bạn đang dùng)
        final String path = request.getServletPath(); // KHÔNG gồm context-path
        if (path.startsWith("/api/auth/") || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) {
            chain.doFilter(request, response);
            return;
        }

        // 3) Nếu đã có authentication thì bỏ qua (tránh set lại)
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        // 4) Lấy Bearer token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);

        try {
            // 5) Validate token
            if (!jwtService.isTokenValid(token)) {
                unauthorized(response, "Invalid or expired token");
                return;
            }

            String email = jwtService.extractEmail(token);
            String role  = jwtService.extractRole(token);

            // (tuỳ chọn) kiểm tra user còn active không
            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + role) // ví dụ ROLE_ADMIN / ROLE_STUDENT
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            unauthorized(response, "Token expired");
        } catch (JwtException e) {
            unauthorized(response, "Invalid token");
        }
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        // trả 401 rõ ràng để FE biết refresh/đăng nhập lại
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
