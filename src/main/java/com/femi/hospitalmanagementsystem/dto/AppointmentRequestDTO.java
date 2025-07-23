package com.femi.hospitalmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppointmentRequestDTO(
        @NotNull Long doctorId,
        @NotNull LocalDateTime appointmentTime
) {}