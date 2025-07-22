package com.femi.hospitalmanagementsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PasswordResetRequestDTO(@NotBlank @Email String email) {}