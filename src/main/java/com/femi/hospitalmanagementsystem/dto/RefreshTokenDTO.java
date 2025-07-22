package com.femi.hospitalmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshTokenDTO(
        @NotBlank String accessToken,
        @NotBlank String refreshToken
) {}