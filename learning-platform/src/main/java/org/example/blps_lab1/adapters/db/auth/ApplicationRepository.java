package org.example.blps_lab1.adapters.db.auth;

import org.example.blps_lab1.core.domain.auth.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    void deleteAllByNewCourse_Uuid(UUID courseUUID);

    List<Application> findByNewCourse_Uuid(UUID courseUUID);
}