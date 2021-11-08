package com.teravin.training.springboot.rest.service.impl;

import com.teravin.base.RecordAlreadyExistException;
import com.teravin.base.RecordNotFoundException;
import com.teravin.base.context.UserContext;
import com.teravin.base.util.StringUtils;
import com.teravin.training.springboot.rest.domain.Company;
import com.teravin.training.springboot.rest.repository.CompanyRepository;
import com.teravin.training.springboot.rest.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public Optional<Company> find(String code) {
        return companyRepository.findById(code);
    }

    @Override
    public List<Company> findDroplist() {
        return companyRepository.findByInactiveCompanyFalseAndDeleteCompanyFalseOrderByIndexAsc();
    }

    @Override
    public Page<Company> findSearch(String code, String name, Pageable pageable) {
        return companyRepository.findByCodeContainingAndNameContainingAndDeleteCompanyFalseOrderByCode(
                StringUtils.nullToEmpty(code),
                StringUtils.nullToEmpty(name),
                pageable
        );
    }

    @Override
    public void save(Company company) {

        if (companyRepository.findById(company.getCode()).isPresent()){
            throw new RecordAlreadyExistException(company.getCode());
        }

        company.setDeleteCompany(false);
        company.setCreatedBy(UserContext.getUserId());
        company.setDateCreated(OffsetDateTime.now());

        companyRepository.save(company);
    }

    @Override
    public void update(Company company) {

        Company entity = companyRepository.findByCodeAndDeleteCompanyFalse(company.getCode()).orElseThrow(() -> new RecordNotFoundException(company.getCode()));

        entity.setName(company.getName());
        entity.setDescription(company.getDescription());
        entity.setInactiveCompany(company.isInactiveCompany());
        entity.setUpdatedBy(UserContext.getUserId());
        entity.setLastUpdated(OffsetDateTime.now());

        companyRepository.save(entity);
    }

    @Override
    public void delete(String code) {

        Company company = companyRepository.findByCodeAndDeleteCompanyFalse(code).orElseThrow(() -> new RecordNotFoundException(code));

        company.setDeleteCompany(true);
        company.setUpdatedBy(UserContext.getUserId());
        company.setLastUpdated(OffsetDateTime.now());

        companyRepository.save(company);
    }
}
