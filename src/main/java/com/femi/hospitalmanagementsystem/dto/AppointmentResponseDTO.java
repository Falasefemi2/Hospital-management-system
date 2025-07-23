package com.femi.hospitalmanagementsystem.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppointmentResponseDTO(
        Long id,
        Long patientId,
        Long doctorId,
        LocalDateTime appointmentTime,
        String status
) {}