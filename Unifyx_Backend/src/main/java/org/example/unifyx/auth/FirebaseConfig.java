package org.example.unifyx.auth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Initializes Firebase Admin SDK using Google Application Default Credentials (ADC).
 *
 * <p>Credentials are loaded from the environment variable {@code GOOGLE_APPLICATION_CREDENTIALS},
 * which must point to a Firebase service-account JSON file stored OUTSIDE the repository.</p>
 *
 * <p>The service-account JSON is NEVER embedded in the classpath or committed to Git.</p>
 *
 * <p>If credentials are not available, the application will still start but Firebase-protected
 * endpoints (/auth/**) will return 401 Unauthorized.</p>
 */
@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    private boolean initialized = false;

    @PostConstruct
    public void initFirebase() {
        if (!FirebaseApp.getApps().isEmpty()) {
            log.info("FirebaseApp already initialized, skipping.");
            initialized = true;
            return;
        }

        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            FirebaseApp.initializeApp(options);
            initialized = true;
            log.info("Firebase Admin SDK initialized successfully using Application Default Credentials.");
        } catch (Exception e) {
            initialized = false;
            log.warn("==========================================================");
            log.warn("Firebase Admin SDK NOT initialized.");
            log.warn("Set GOOGLE_APPLICATION_CREDENTIALS environment variable");
            log.warn("to the path of your Firebase service-account JSON file.");
            log.warn("Protected endpoints (/auth/**) will return 401 until configured.");
            log.warn("Error: {}", e.getMessage());
            log.warn("==========================================================");
        }
    }

    /**
     * Returns whether Firebase Admin SDK was successfully initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }
}
