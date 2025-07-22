package com.femi.hospitalmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PasswordResetDTO(
        @NotBlank String token,
        @NotBlank @Size(min = 8, max = 128) String newPassword
) {}