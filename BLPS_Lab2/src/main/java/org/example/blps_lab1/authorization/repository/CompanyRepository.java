package org.example.blps_lab1.authorization.repository;

import org.example.blps_lab1.authorization.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String>{
    
}
