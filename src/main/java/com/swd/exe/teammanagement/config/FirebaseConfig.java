package com.swd.exe.teammanagement.config;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

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
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .setProjectId(projectId)
                .build();

        return FirebaseApp.initializeApp(options);
    }
}