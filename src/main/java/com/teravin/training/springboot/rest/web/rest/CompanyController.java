package com.teravin.training.springboot.rest.web.rest;

import com.teravin.base.RecordNotFoundException;
import com.teravin.base.dto.DroplistDto;
import com.teravin.base.util.SearchUtils;
import com.teravin.training.springboot.rest.domain.Company;
import com.teravin.training.springboot.rest.dto.CompanyDto;
import com.teravin.training.springboot.rest.dto.CompanySearchDto;
import com.teravin.training.springboot.rest.service.CompanyService;
import com.teravin.training.springboot.rest.util.DateTimeFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/{code}")
    public CompanyDto find(@PathVariable String code){

        return companyService.find(code)
                .map(this::toCompanyDto)
                .orElseThrow(() -> new RecordNotFoundException(code));
    }

    @GetMapping
    public Page<CompanyDto> findSearch(@RequestBody CompanySearchDto companySearchDto){

        return companyService.findSearch(
                companySearchDto.getCode(),
                companySearchDto.getName(),
                SearchUtils.toPageable(companySearchDto))
                .map(this::toCompanyDto);
    }

    @GetMapping("/droplist")
    public List<DroplistDto> findDroplist(){

        return companyService.findDroplist()
                .stream()
                .map(this::toDroplistDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public void save(@Valid @RequestBody CompanyDto companyDto){

        companyService.save(toCompany(companyDto));
    }

    @PatchMapping
    public void update(@Valid @RequestBody CompanyDto companyDto){

        companyService.update(toCompany(companyDto));
    }

    @DeleteMapping("/{code}")
    public void delete(@PathVariable String code){

        companyService.delete(code);
    }

    private CompanyDto toCompanyDto(Company company){

        CompanyDto companyDto = new CompanyDto();
        companyDto.setCode(company.getCode());
        companyDto.setName(company.getName());
        companyDto.setDescription(company.getDescription());
        companyDto.setInactiveCompany(company.isInactiveCompany());
        companyDto.setCreatedBy(company.getCreatedBy());
        companyDto.setDateCreated(DateTimeFormatUtil.format(company.getDateCreated()));
        companyDto.setUpdatedBy(company.getUpdatedBy());
        companyDto.setLastUpdated(DateTimeFormatUtil.format(company.getLastUpdated()));

        return companyDto;

    }

    private DroplistDto toDroplistDto(Company company) {

        DroplistDto droplistDto = new DroplistDto();
        droplistDto.setId(company.getCode());
        droplistDto.setValue(company.getName());

        return droplistDto;
    }

    private Company toCompany(CompanyDto companyDto){

        Company company = new Company();
        company.setCode(companyDto.getCode());
        company.setName(companyDto.getName());
        company.setDescription(companyDto.getDescription());
        company.setInactiveCompany(companyDto.isInactiveCompany());

        return company;
    }
}
