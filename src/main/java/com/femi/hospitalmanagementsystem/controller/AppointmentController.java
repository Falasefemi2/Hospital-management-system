package com.femi.hospitalmanagementsystem.controller;

import com.femi.hospitalmanagementsystem.dto.AppointmentRequestDTO;
import com.femi.hospitalmanagementsystem.dto.AppointmentResponseDTO;
import com.femi.hospitalmanagementsystem.dto.ErrorResponseDTO;
import com.femi.hospitalmanagementsystem.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> bookAppointment(@Valid @RequestBody AppointmentRequestDTO dto) {
        try {
            String patientEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            AppointmentResponseDTO appointment = appointmentService.bookAppointment(dto, patientEmail);
            return ResponseEntity.ok(appointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(e.getMessage()));
        }
    }
}