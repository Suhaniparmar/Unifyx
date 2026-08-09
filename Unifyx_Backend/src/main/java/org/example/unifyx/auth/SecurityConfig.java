package org.example.unifyx.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for Phase 1 Firebase authentication.
 *
 * <p><b>Phase 1 access rules:</b></p>
 * <ul>
 *   <li>{@code /auth/**} — requires authentication (Firebase ID token)</li>
 *   <li>All other endpoints — permitted without authentication (existing behavior preserved)</li>
 * </ul>
 *
 * <p>This is intentional. Existing business APIs (/users, /posts, /quotes, /hires, etc.)
 * remain open so the working application is not broken. They will be migrated to
 * require authentication in a future phase.</p>
 *
 * <p>Configuration:</p>
 * <ul>
 *   <li>CSRF disabled — this is a stateless REST API with no browser sessions</li>
 *   <li>Session management — STATELESS (no HTTP sessions)</li>
 *   <li>Authentication entry point — returns 401 for unauthenticated requests to protected endpoints</li>
 *   <li>FirebaseAuthFilter runs before UsernamePasswordAuthenticationFilter</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final FirebaseAuthFilter firebaseAuthFilter;

    public SecurityConfig(FirebaseAuthFilter firebaseAuthFilter) {
        this.firebaseAuthFilter = firebaseAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — stateless REST API, no browser sessions
                .csrf(csrf -> csrf.disable())

                // Stateless session management — no HTTP sessions
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Access rules
                .authorizeHttpRequests(auth -> auth
                        // Phase 1: Only /auth/** requires authentication
                        .requestMatchers("/auth/**").authenticated()
                        // All existing endpoints remain open (unchanged behavior)
                        .anyRequest().permitAll()
                )

                // Return 401 (not redirect to login) for unauthenticated requests
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                // Register Firebase auth filter before Spring Security's default auth filter
                .addFilterBefore(firebaseAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
