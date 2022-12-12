package com.projects.casinApp_v3.repository;

import com.projects.casinApp_v3.model.GRWallet;
import com.projects.casinApp_v3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GRWalletRepository extends JpaRepository<GRWallet, Long> {

    public boolean existsBygrWalletId(Long grWalletId);
}
