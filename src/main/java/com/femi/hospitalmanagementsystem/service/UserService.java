package com.femi.hospitalmanagementsystem.service;

import com.femi.hospitalmanagementsystem.auth.JwtUtil;
import com.femi.hospitalmanagementsystem.dto.*;
import com.femi.hospitalmanagementsystem.model.PasswordResetToken;
import com.femi.hospitalmanagementsystem.model.Role;
import com.femi.hospitalmanagementsystem.model.User;
import com.femi.hospitalmanagementsystem.repository.PasswordResetTokenRepository;
import com.femi.hospitalmanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserDetailsDTO registerPatient(UserRegistrationDTO dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if(userRepository.existsByName(dto.getName())) {
            throw new IllegalStateException("Username already in use");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.PATIENT)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with email: {}", user.getEmail());

        return UserDetailsDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public UserDetailsDTO createUser(AdminUserCreationDTO dto) {
        System.err.println("=== USERSERVICE ENTRY ===");
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        if(userRepository.existsByName(dto.getName())) {
            throw new IllegalStateException("Username already in use");
        }

        if (dto.getRole() == Role.PATIENT) {
            throw new IllegalArgumentException("Patients must register via public endpoint");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with email: {}", user.getEmail());

        return UserDetailsDTO.builder()
                .id(user.getId())
                .username(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public UserResponseDTO loginUser(UserLoginDTO userLoginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(userLoginDTO.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String token = jwtUtil.generateToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);
            log.info("User logged in successfully: {}", user.getEmail());

            return UserResponseDTO.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();
        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for email: {}", userLoginDTO.getEmail());
            throw new IllegalArgumentException("Invalid email or password", e);
        }
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(
                token,
                user,
                LocalDateTime.now().plusHours(1)
        );
        passwordResetTokenRepository.save(passwordResetToken);
        // TODO: Send email with reset link (e.g., http://localhost:8080/api/users/reset-password?token=token)
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));
        if(resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken); // invalid token
    }
}