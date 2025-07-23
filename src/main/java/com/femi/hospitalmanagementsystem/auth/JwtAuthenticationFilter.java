package com.femi.hospitalmanagementsystem.auth;

import com.femi.hospitalmanagementsystem.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        System.err.println("JWT Filter processing: " + method + " " + path);

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.err.println("No Bearer token found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtUtil.extractUsername(jwt);
            System.err.println("Extracted email from JWT: " + userEmail);
        } catch (Exception e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid token\"}");
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.err.println("Looking up user by email: " + userEmail);

            userRepository.findByEmail(userEmail).ifPresentOrElse(user -> {
                System.err.println("User found: " + user.getEmail() + ", Role: " + user.getRole());

                if (jwtUtil.isTokenValid(jwt, user)) {
                    String role = jwtUtil.extractRole(jwt);
                    Collection<GrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );

                    System.err.println("Setting authorities: " + authorities);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.err.println("Authentication set successfully");
                } else {
                    System.err.println("Token validation failed");
                }
            }, () -> {
                System.err.println("User not found for email: " + userEmail);
            });
        }

        System.err.println("Continuing filter chain");
        filterChain.doFilter(request, response);
    }
}
