package org.example.unifyx.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for verifying Firebase ID tokens.
 *
 * <p>Uses Firebase Admin SDK's {@link FirebaseAuth#verifyIdToken(String)} which:
 * <ul>
 *   <li>Validates the token signature against Google's public keys</li>
 *   <li>Verifies the token has not expired</li>
 *   <li>Confirms the token was issued for the correct Firebase project</li>
 * </ul>
 *
 * <p><b>Token revocation checking</b> is NOT enabled (default behavior).
 * Revocation checking adds an extra round-trip to Firebase on every request,
 * which significantly impacts latency. For Phase 1, the standard token expiry
 * (1 hour) provides sufficient security. Revocation checking can be enabled
 * in a future phase if session invalidation requirements arise.</p>
 */
@Service
public class FirebaseTokenService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseTokenService.class);

    private final FirebaseConfig firebaseConfig;

    public FirebaseTokenService(FirebaseConfig firebaseConfig) {
        this.firebaseConfig = firebaseConfig;
    }

    /**
     * Verifies a Firebase ID token and returns the decoded token.
     *
     * @param idToken the raw Firebase ID token string
     * @return the verified {@link FirebaseToken} containing the user's UID and claims
     * @throws FirebaseAuthException if the token is invalid, expired, or malformed
     * @throws IllegalStateException if Firebase Admin SDK is not initialized
     */
    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        if (!firebaseConfig.isInitialized()) {
            throw new IllegalStateException(
                    "Firebase Admin SDK is not initialized. "
                    + "Set GOOGLE_APPLICATION_CREDENTIALS environment variable.");
        }

        // verifyIdToken(idToken) does NOT check revocation (default).
        // Use verifyIdToken(idToken, true) to enable revocation checking if needed.
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        log.debug("Firebase token verified for UID: {}", decodedToken.getUid());
        return decodedToken;
    }
}
