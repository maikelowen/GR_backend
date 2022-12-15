package com.projects.casinApp_v3.repository;

import com.projects.casinApp_v3.model.GRUser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GRUserRepository extends JpaRepository<GRUser, Long> {
    boolean existsByGrId(Long grWalletId);

    Optional<GRUser> findByUsername(String username);

}
