package org.example.unifyx.auth;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Authentication filter that extracts and verifies Firebase ID tokens from
 * the {@code Authorization: Bearer <token>} header.
 *
 * <p>On successful verification, the authenticated Firebase UID is placed into
 * the {@link SecurityContextHolder} as the principal, making it available to
 * downstream controllers via {@code SecurityContextHolder.getContext().getAuthentication().getName()}.</p>
 *
 * <p>On failure (missing, malformed, invalid, or expired tokens), the filter
 * does NOT set authentication. Spring Security's configured access rules then
 * decide whether to return 401 or allow the request through.</p>
 */
@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(FirebaseAuthFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final FirebaseTokenService firebaseTokenService;

    public FirebaseAuthFilter(FirebaseTokenService firebaseTokenService) {
        this.firebaseTokenService = firebaseTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        // If no Authorization header or not Bearer scheme, skip — let Security config decide access
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String idToken = authHeader.substring(BEARER_PREFIX.length()).trim();

        if (idToken.isEmpty()) {
            log.warn("Empty Bearer token received from {}", request.getRemoteAddr());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            FirebaseToken decodedToken = firebaseTokenService.verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            // Create authentication with the verified Firebase UID as principal
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(uid, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authenticated request for UID: {} on {}", uid, request.getRequestURI());

        } catch (FirebaseAuthException e) {
            // Token is invalid, expired, or malformed
            // Do NOT expose Firebase internal details to the client
            log.warn("Firebase token verification failed for {} {}: {}",
                    request.getMethod(), request.getRequestURI(), e.getMessage());
            // Clear any partial authentication
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            // Unexpected error during verification
            log.error("Unexpected error during Firebase token verification: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
