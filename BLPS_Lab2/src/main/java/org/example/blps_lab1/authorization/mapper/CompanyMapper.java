package org.example.blps_lab1.authorization.mapper;

import org.example.blps_lab1.authorization.dto.CompanyDto;
import org.example.blps_lab1.authorization.models.Company;

public class CompanyMapper {
    public static CompanyDto toDto(Company company) {
        return CompanyDto.builder()
                .companyName(company.getCompanyName())
                .build();
    }

    public static Company toEntity(CompanyDto companyDto) {
        return Company.builder()
                .companyName(companyDto.getCompanyName())
                .build();
    }
}
