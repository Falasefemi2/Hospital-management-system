package com.femi.hospitalmanagementsystem.service;

import com.femi.hospitalmanagementsystem.dto.AppointmentRequestDTO;
import com.femi.hospitalmanagementsystem.dto.AppointmentResponseDTO;
import com.femi.hospitalmanagementsystem.model.Appointment;
import com.femi.hospitalmanagementsystem.model.Role;
import com.femi.hospitalmanagementsystem.model.Status;
import com.femi.hospitalmanagementsystem.model.User;
import com.femi.hospitalmanagementsystem.repository.AppointmentRepository;
import com.femi.hospitalmanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO dto, String patientEmail) {
        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        User doctor = userRepository.findById(dto.doctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        if (!doctor.getRole().equals(Role.DOCTOR)) {
            throw new IllegalArgumentException("Invalid doctor");
        }
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentTime(dto.appointmentTime())
                .status(Status.SCHEDULED)
                .build();
        appointment = appointmentRepository.save(appointment);
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .patientId(patient.getId())
                .doctorId(doctor.getId())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus().name())
                .build();
    }

}
