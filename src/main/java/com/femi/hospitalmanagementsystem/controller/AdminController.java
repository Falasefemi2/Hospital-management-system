package com.femi.hospitalmanagementsystem.controller;

import com.femi.hospitalmanagementsystem.dto.AdminUserCreationDTO;
import com.femi.hospitalmanagementsystem.dto.ApiResponse;
import com.femi.hospitalmanagementsystem.dto.UserDetailsDTO;
import com.femi.hospitalmanagementsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminController implements ApplicationListener<ContextRefreshedEvent> {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserDetailsDTO>> createUser(
            @Valid @RequestBody AdminUserCreationDTO adminUserCreationDTO
            ) {
               try {
            UserDetailsDTO userDetailsDTO = userService.createUser(adminUserCreationDTO);
            ApiResponse<UserDetailsDTO> response = ApiResponse.<UserDetailsDTO>builder()
                    .success(true)
                    .message("User registered successfully")
                    .data(userDetailsDTO)
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

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        RequestMappingHandlerMapping mapping = context.getBean(RequestMappingHandlerMapping.class);

        mapping.getHandlerMethods().forEach((key, value) -> {
            if (key.toString().contains("/api/admin/users")) {
                System.err.println("Found mapping: " + key + " -> " + value);
            }
        });
    }
}
