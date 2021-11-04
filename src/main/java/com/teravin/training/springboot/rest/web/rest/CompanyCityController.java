package com.teravin.training.springboot.rest.web.rest;

import com.teravin.training.springboot.rest.domain.City;
import com.teravin.training.springboot.rest.domain.Company;
import com.teravin.training.springboot.rest.domain.CompanyCity;
import com.teravin.training.springboot.rest.dto.CompanyCityDto;
import com.teravin.training.springboot.rest.service.CompanyCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/company_city")
public class CompanyCityController {

    @Autowired
    private CompanyCityService companyCityService;

    @PostMapping
    public void save(@Valid @RequestBody CompanyCityDto companyCityDto){

        companyCityService.save(toCompanyCity(companyCityDto));
    }

    @PatchMapping
    public void update(@Valid @RequestBody CompanyCityDto companyCityDto){

        companyCityService.update(toCompanyCity(companyCityDto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){

        companyCityService.delete(id);
    }

    private CompanyCity toCompanyCity(CompanyCityDto companyCityDto){

        Company company = new Company();
        company.setCode(companyCityDto.getCompanyId());
        company.setName(companyCityDto.getCompanyName());

        City city = new City();
        city.setCode(companyCityDto.getCityId());
        city.setName(companyCityDto.getCityName());

        CompanyCity companyCity = new CompanyCity();
        companyCity.setCompany(company);
        companyCity.setCity(city);

        return companyCity;
    }
}
