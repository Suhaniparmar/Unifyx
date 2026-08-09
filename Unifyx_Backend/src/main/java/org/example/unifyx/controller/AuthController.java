package org.example.unifyx.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication verification endpoint.
 *
 * <p>Returns the authenticated user's identity derived ONLY from the verified
 * Firebase ID token. This endpoint does NOT accept a UID parameter and ignores
 * any client-supplied UID values.</p>
 *
 * <p>Protected by Spring Security — requires a valid {@code Authorization: Bearer <token>} header.</p>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * Returns the authenticated user's identity.
     *
     * <p>The UID is extracted from the SecurityContext, which was populated by
     * {@link org.example.unifyx.auth.FirebaseAuthFilter} after verifying the
     * Firebase ID token. No client-supplied identity is trusted.</p>
     *
     * @return JSON with {@code uid} and {@code authenticated} fields
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // The principal was set to the verified Firebase UID by FirebaseAuthFilter
        String uid = authentication.getName();

        Map<String, Object> response = new HashMap<>();
        response.put("uid", uid);
        response.put("authenticated", true);

        return ResponseEntity.ok(response);
    }
}
