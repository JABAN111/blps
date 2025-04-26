package org.example.blps_lab1.adapters.auth.mapper;

import org.example.blps_lab1.adapters.auth.dto.CompanyDto;
import org.example.blps_lab1.core.domain.auth.Company;

@Deprecated
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
