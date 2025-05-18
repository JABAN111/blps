package org.example.blps_lab1.core.ports.auth;


import java.util.List;


import org.example.blps_lab1.adapters.auth.dto.CompanyDto;
import org.example.blps_lab1.core.domain.auth.Company;

@SuppressWarnings("wait for refactor")
@Deprecated
public interface CompanyService {
    
    Boolean isExist(String companyName);
    Company getByName(String companyName);
    Company getOrCreate(CompanyDto companyDto);
    Company save(CompanyDto companyDto);
    Company save(Company company);

    List<Company> saveAll(List<Company> companies);


}
