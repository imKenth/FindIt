package com.findit.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            // Using ClassPathResource is more robust than a relative file path.
            // It will look in src/main/resources/ when running locally and inside the jar when deployed.
            ClassPathResource resource = new ClassPathResource("findit-41095-firebase-adminsdk-fbsvc-5f23ac96e5.json");
            
            if (!resource.exists()) {
                System.err.println("Firebase service account key file not found in classpath.");
                return;
            }

            InputStream serviceAccount = resource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            System.err.println("Firebase Initialization Error: " + e.getMessage());
        }
    }

    @Bean
    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
}
