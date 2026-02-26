package com.ibm.fscc.registrationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        // allow Kubernetes health checks without auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/registration").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/registration/status/check").permitAll()
                        // everything else requires authentication
                        .anyRequest().authenticated())
                .addFilterBefore(new GatewayHeaderAuthFilter(),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Custom filter to read headers from Gateway
    static class GatewayHeaderAuthFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain filterChain)
                throws ServletException, IOException {

            String userId = request.getHeader("X-User-Id");
            String role = request.getHeader("X-User-Role");

            if (userId != null && role != null) {
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                // The 'userId' becomes authentication.getName()
                // Role becomes part of authentication.getAuthorities()
                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);
        }
    }
}
