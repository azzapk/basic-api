package com.teravin.training.springboot.rest.service.impl;

import com.teravin.base.RecordNotFoundException;
import com.teravin.training.springboot.rest.domain.CompanyCity;
import com.teravin.training.springboot.rest.repository.CompanyCityRepository;
import com.teravin.training.springboot.rest.service.CompanyCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompanyCityServiceImpl implements CompanyCityService {

    @Autowired
    private CompanyCityRepository companyCityRepository;

    @Override
    public void save(CompanyCity companyCity) {

        companyCityRepository.save(companyCity);
    }

    @Override
    public void update(CompanyCity companyCity) {

        CompanyCity entity = companyCityRepository.findById(companyCity.getId()).orElseThrow(() -> new RecordNotFoundException(companyCity.getId().toString()));

        entity.setCompany(companyCity.getCompany());
        entity.setCity(companyCity.getCity());

        companyCityRepository.save(entity);
    }

    @Override
    public void delete(Long id) {

        companyCityRepository.deleteById(id);
    }
}
