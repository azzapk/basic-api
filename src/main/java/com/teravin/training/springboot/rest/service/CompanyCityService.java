package com.teravin.training.springboot.rest.service;

import com.teravin.training.springboot.rest.domain.CompanyCity;

public interface CompanyCityService {

    void save(CompanyCity companyCity);

    void update(CompanyCity companyCity);

    void delete(Long id);
}
