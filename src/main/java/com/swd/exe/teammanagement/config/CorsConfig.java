// CorsConfig.java
package com.swd.exe.teammanagement.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // LIỆT KÊ ĐÚNG ORIGIN bạn gọi từ FE
        config.setAllowedOrigins(List.of(
                "http://127.0.0.1:5500",
                "http://localhost:5500",
                "http://localhost:5173",
                "https://swd392-exe-team-management-be.onrender.com",
                "https://exe-groups.pages.dev",
                "http://localhost:63342",
                "http://127.0.0.1:5501"

                 // nếu FE deploy ở domain khác, thêm domain đó
        ));

        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));

        // Dùng addAllowedHeader("*") thay vì List.of("*")
        config.addAllowedHeader("*");

        // Nếu bạn trả JWT qua header/Location muốn đọc từ FE
        config.setExposedHeaders(List.of("Authorization","Location"));

        // Nếu dùng cookie/session, bật dòng dưới và KHÔNG dùng "*" cho origins
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
