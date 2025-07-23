package com.femi.hospitalmanagementsystem.controller;

import com.femi.hospitalmanagementsystem.auth.JwtUtil;
import com.femi.hospitalmanagementsystem.dto.*;
import com.femi.hospitalmanagementsystem.model.User;
import com.femi.hospitalmanagementsystem.repository.UserRepository;
import com.femi.hospitalmanagementsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDetailsDTO>> registerPatient(
            @Valid @RequestBody UserRegistrationDTO userRegistrationDTO
    ) {
        try {
            UserDetailsDTO userDetails = userService.registerPatient(userRegistrationDTO);
            ApiResponse<UserDetailsDTO> response = ApiResponse.<UserDetailsDTO>builder()
                    .success(true)
                    .message("User registered successfully")
                    .data(userDetails)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            ApiResponse<UserDetailsDTO> response = ApiResponse.<UserDetailsDTO>builder()
                    .success(false)
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            log.error("Error during user registration", e);
            ApiResponse<UserDetailsDTO> response = ApiResponse.<UserDetailsDTO>builder()
                    .success(false)
                    .message("Registration failed")
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDTO>> loginUser(
            @Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
            UserResponseDTO loginResponse = userService.loginUser(userLoginDTO);
            ApiResponse<UserResponseDTO> response = ApiResponse.<UserResponseDTO>builder()
                    .success(true)
                    .message("Login successful")
                    .data(loginResponse)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<UserResponseDTO> response = ApiResponse.<UserResponseDTO>builder()
                    .success(false)
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error during user login", e);
            ApiResponse<UserResponseDTO> response = ApiResponse.<UserResponseDTO>builder()
                    .success(false)
                    .message("Login failed")
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<?> resetPasswordRequest(
            @RequestBody @Valid PasswordResetRequestDTO dto
    ) {
        try {
            userService.requestPasswordReset(dto.email());
            return ResponseEntity.ok().body(new ErrorResponseDTO("Password reset link sent"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordResetDTO dto) {
        try {
            userService.resetPassword(dto.token(), dto.newPassword());
            return ResponseEntity.ok().body(new ErrorResponseDTO("Password reset successful"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenDTO dto) {
        try {
            User user = userRepository.findByEmail(jwtUtil.extractUsername(dto.accessToken()))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid access token"));
            if (jwtUtil.isRefreshTokenValid(dto.refreshToken(), user)) {
                String newAccessToken = jwtUtil.generateToken(user);
                return ResponseEntity.ok().body(UserResponseDTO.builder()
                        .token(newAccessToken)
                        .refreshToken(dto.refreshToken())
                        .id(user.getId())
                        .name(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build());
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponseDTO("Invalid or expired refresh token"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(e.getMessage()));
        }
    }
}