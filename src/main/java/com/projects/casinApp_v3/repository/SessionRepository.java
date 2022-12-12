package com.projects.casinApp_v3.repository;

import com.projects.casinApp_v3.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
}
