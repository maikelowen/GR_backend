package com.projects.casinApp_v3.repository;

import com.projects.casinApp_v3.model.ERole;
import com.projects.casinApp_v3.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}