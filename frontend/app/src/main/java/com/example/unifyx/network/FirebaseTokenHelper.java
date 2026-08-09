package com.example.unifyx.network;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Helper utility for obtaining Firebase ID tokens for authenticated API calls.
 *
 * <p>Usage:</p>
 * <pre>
 * FirebaseTokenHelper.getIdToken(token -> {
 *     if (token != null) {
 *         // Use token in Authorization header: "Bearer " + token
 *     } else {
 *         // User not authenticated or token retrieval failed
 *     }
 * });
 * </pre>
 *
 * <p>This helper does NOT modify existing Retrofit API calls.
 * It provides the minimal mechanism to obtain a Firebase ID token
 * for proof-of-concept authenticated requests.</p>
 */
public class FirebaseTokenHelper {

    private static final String TAG = "FirebaseTokenHelper";

    /**
     * Callback interface for receiving the Firebase ID token.
     */
    public interface TokenCallback {
        /**
         * Called when the token is available or retrieval fails.
         *
         * @param token the Firebase ID token, or null if retrieval failed
         */
        void onTokenReceived(String token);
    }

    /**
     * Obtains a fresh Firebase ID token for the currently authenticated user.
     *
     * <p>Forces a token refresh to ensure the token is not expired.
     * The callback is invoked on the main thread.</p>
     *
     * @param callback receives the ID token or null on failure
     */
    public static void getIdToken(TokenCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.w(TAG, "No authenticated Firebase user");
            callback.onTokenReceived(null);
            return;
        }

        // forceRefresh = true ensures we get a fresh, valid token
        user.getIdToken(true)
                .addOnSuccessListener(result -> {
                    String token = result.getToken();
                    if (token != null) {
                        Log.d(TAG, "Firebase ID token obtained successfully");
                    } else {
                        Log.w(TAG, "Firebase ID token was null");
                    }
                    callback.onTokenReceived(token);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get Firebase ID token", e);
                    callback.onTokenReceived(null);
                });
    }
}
