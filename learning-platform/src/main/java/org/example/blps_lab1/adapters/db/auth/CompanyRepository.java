package org.example.blps_lab1.adapters.db.auth;

import org.example.blps_lab1.core.domain.auth.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String>{
    
}
