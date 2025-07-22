package com.femi.hospitalmanagementsystem.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    PATIENT,
    DOCTOR,
    RECEPTIONIST,
    ADMIN;

    public GrantedAuthority getAuthority() {
        return new SimpleGrantedAuthority("ROLE_" + this.name());
    }
}
