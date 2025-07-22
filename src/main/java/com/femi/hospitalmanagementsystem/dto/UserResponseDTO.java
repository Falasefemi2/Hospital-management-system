package com.femi.hospitalmanagementsystem.dto;

import com.femi.hospitalmanagementsystem.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String token;
    private Long id;
    private String name;
    private String email;
    private Role role;
}
