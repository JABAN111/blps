package org.example.blps_lab1.adapters.auth.service;

import java.util.List;

import org.example.blps_lab1.adapters.auth.dto.CompanyDto;
import org.example.blps_lab1.adapters.auth.mapper.CompanyMapper;
import org.example.blps_lab1.core.domain.auth.Company;
import org.example.blps_lab1.adapters.db.auth.CompanyRepository;
import org.example.blps_lab1.core.ports.auth.CompanyService;
import org.example.blps_lab1.core.exception.common.ObjectNotExistException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private CompanyRepository companyRepository;

    @Override
    public Boolean isExist(String companyName) {
        return companyRepository.findById(companyName).isPresent();
    }

    @Override
    public List<Company> saveAll(List<Company> companies) {
        return companyRepository.saveAll(companies);
    }

    @Override
    public Company getByName(String companyName) {
        return companyRepository.findById(companyName)
                .orElseThrow(() -> new ObjectNotExistException("Компания с именем: " + companyName + " не найдена"));
    }

    @Override
    public Company getOrCreate(CompanyDto companyDto) {
        Company result;
        if (isExist(companyDto.getCompanyName())) {
            result = getByName(companyDto.getCompanyName());
        } else {
            result = save(companyDto);
        }

        return result;
    }

    @Override
    public Company save(CompanyDto companyDto) {
        return save(CompanyMapper.toEntity(companyDto));
    }

    @Override
    public Company save(Company company) {
        return companyRepository.save(company);
    }

}
