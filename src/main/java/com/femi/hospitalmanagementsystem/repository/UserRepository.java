package com.femi.hospitalmanagementsystem.repository;

import com.femi.hospitalmanagementsystem.model.Role;
import com.femi.hospitalmanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByName(String name);
    List<User> findAllByRole(Role role);

}
