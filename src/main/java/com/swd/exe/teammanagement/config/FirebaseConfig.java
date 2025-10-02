package com.swd.exe.teammanagement.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {
    
    @Value("${fcm.project-id}")
    private String projectId;
    
    @Value("${fcm.private-key}")
    private String privateKey;
    
    @Value("${fcm.client-email}")
    private String clientEmail;
    
    @Value("${fcm.private-key-id}")
    private String privateKeyId;
    
    @Value("${fcm.client-id}")
    private String clientId;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        // Tạo JSON credentials từ các biến môi trường
        String credentialsJson = String.format(
            "{\n" +
            "  \"type\": \"service_account\",\n" +
            "  \"project_id\": \"%s\",\n" +
            "  \"private_key_id\": \"%s\",\n" +
            "  \"private_key\": \"%s\",\n" +
            "  \"client_email\": \"%s\",\n" +
            "  \"client_id\": \"%s\",\n" +
            "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
            "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
            "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
            "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/%s\"\n" +
            "}",
            projectId,
            privateKeyId,
            privateKey.replace("\\n", "\n"), // Xử lý newlines trong private key
            clientEmail,
            clientId,
            clientEmail.replace("@", "%40") // URL encode @ symbol
        );

        ByteArrayInputStream credentialsStream = new ByteArrayInputStream(
            credentialsJson.getBytes(StandardCharsets.UTF_8)
        );

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .setProjectId(projectId)
                .build();

        return FirebaseApp.initializeApp(options);
    }
}