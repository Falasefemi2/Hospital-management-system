package com.femi.hospitalmanagementsystem.dto;

import com.femi.hospitalmanagementsystem.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;
}
