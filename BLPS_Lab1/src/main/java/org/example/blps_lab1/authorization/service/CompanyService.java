package org.example.blps_lab1.authorization.service;


import java.util.List;


import org.example.blps_lab1.authorization.dto.CompanyDto;
import org.example.blps_lab1.authorization.models.Company;

public interface CompanyService {
    
    Boolean isExist(String companyName);
    Company getByName(String companyName);
    Company getOrCreate(CompanyDto companyDto);
    Company save(CompanyDto companyDto);
    Company save(Company company);

    List<Company> saveAll(List<Company> companies);


}
