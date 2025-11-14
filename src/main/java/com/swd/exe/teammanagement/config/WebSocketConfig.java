package com.swd.exe.teammanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đây là endpoint mà client sẽ connect WebSocket vào
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://127.0.0.1:5500", "http://localhost:63342",
                        "https://swd392-exe-team-management-be.onrender.com", "http://localhost:5173", "https://exe-groups.pages.dev") // Cho phép Live Server
                .setAllowedOriginPatterns("http://127.0.0.1:*", "http://localhost:63342","http://localhost:5173", "https://exe-groups.pages.dev") // Cho phép localhost với mọi port
                .withSockJS(); // Hỗ trợ fallback cho browser cũ
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user");
    }
}