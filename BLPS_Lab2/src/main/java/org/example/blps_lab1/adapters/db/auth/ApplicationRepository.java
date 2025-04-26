package org.example.blps_lab1.adapters.db.auth;

import org.example.blps_lab1.core.domain.auth.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
} 