package org.example.blps_lab1.authorization.service.impl;



import java.util.List;


import org.example.blps_lab1.authorization.dto.CompanyDto;
import org.example.blps_lab1.authorization.mapper.CompanyMapper;
import org.example.blps_lab1.authorization.models.Company;
import org.example.blps_lab1.authorization.repository.CompanyRepository;
import org.example.blps_lab1.authorization.service.CompanyService;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
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
        if (companyRepository.findById(companyName).isPresent()){
            return true;
        }
        return false;
    }

    @Override

    public List<Company> saveAll(List<Company> companies) {
        return companyRepository.saveAll(companies);
    }

    @Override
    public Company getByName(String companyName) {
        var potentialCompany = companyRepository.findById(companyName);

        if(potentialCompany.isPresent()){
            return potentialCompany.get();
        }
        log.warn("Company with name: {} not found", companyName);
        throw new ObjectNotExistException("Компания с именем: " + companyName + " не найдена");
    }

    @Override
    public Company getOrCreate(CompanyDto companyDto) {
        Company result; 
        if(isExist(companyDto.getCompanyName())){
            result = getByName(companyDto.getCompanyName());
        } 
        result = save(companyDto);
        

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
