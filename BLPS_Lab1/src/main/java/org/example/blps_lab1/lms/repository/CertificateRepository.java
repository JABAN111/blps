package org.example.blps_lab1.lms.repository;

import org.example.blps_lab1.lms.models.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
}
