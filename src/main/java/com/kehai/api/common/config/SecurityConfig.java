package com.kehai.api.common.config;

import com.kehai.api.common.security.KehaiJwtConverter;
import com.kehai.api.common.security.TenantContextFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.secret}")
    private String jwtSecret;

    private final KehaiJwtConverter kehaiJwtConverter;
    private final TenantContextFilter tenantContextFilter;

    public SecurityConfig(KehaiJwtConverter kehaiJwtConverter,
                          TenantContextFilter tenantContextFilter) {
        this.kehaiJwtConverter = kehaiJwtConverter;
        this.tenantContextFilter = tenantContextFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disables CSRF - stateless JWT APIs don't need it
                .csrf(AbstractHttpConfigurer::disable)

                // No sessions - every request must carry its own JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Endpoint access rules
                .authorizeHttpRequests(auth -> auth
                        // Health check - public so load balancers can reach it
                        .requestMatchers("/actuator/health").permitAll()
                        // Everything else requires a valid JWT
                        .anyRequest().authenticated()
                )

                // JWT validation via OAuth2 Resource Server
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(kehaiJwtConverter))
                )

                // Populate TenantContext after JWT is validated
                .addFilterAfter(tenantContextFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // HMAC-SHA256 symmetric key decoder
        // In production this secret comes from an environment variable
        SecretKeySpec key = new SecretKeySpec(
                jwtSecret.getBytes(), "HmacSHA256"
        );
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
