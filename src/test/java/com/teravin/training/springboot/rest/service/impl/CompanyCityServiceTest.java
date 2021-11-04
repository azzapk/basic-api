package com.teravin.training.springboot.rest.service.impl;

import com.teravin.training.springboot.rest.domain.City;
import com.teravin.training.springboot.rest.domain.Company;
import com.teravin.training.springboot.rest.domain.CompanyCity;
import com.teravin.training.springboot.rest.repository.CompanyCityRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompanyCityServiceTest {

    @InjectMocks
    private CompanyCityServiceImpl companyCityService;

    @Mock
    private CompanyCityRepository companyCityRepository;

    @Captor
    private ArgumentCaptor<CompanyCity> companyCityCaptor;

    @Test
    public void testSave(){

        City city = new City();
        city.setCode("JKT");
        city.setName("Jakarta");

        Company company = new Company();
        company.setCode("TRN");
        company.setName("Teravin");

//        when(companyCityRepository.findById(1L)).thenReturn(Optional.empty());

        CompanyCity companyCity = new CompanyCity();
//        companyCity.setId(1L);
        companyCity.setCompany(company);
        companyCity.setCity(city);

        companyCityService.save(companyCity);

        verify(companyCityRepository, times(1)).save(companyCityCaptor.capture());
        companyCity = companyCityCaptor.getValue();
        assertEquals("TRN", companyCity.getCompany().getCode());
        assertEquals("Teravin", companyCity.getCompany().getName());
        assertEquals("JKT", companyCity.getCity().getCode());
        assertEquals("Jakarta", companyCity.getCity().getName());
    }

    @Test
    public void testUpdate() {

        City city1 = new City();
        city1.setCode("JKT");
        city1.setName("Jakarta");

        City city2 = new City();
        city2.setCode("DIY");
        city2.setName("Yogyakarta");

        Company company = new Company();
        company.setCode("TRN");
        company.setName("Teravin");

        when(companyCityRepository.findById(1L)).then(invocationOnMock -> {

            CompanyCity companyCity = new CompanyCity();
            companyCity.setCompany(company);
            companyCity.setCity(city1);

            return Optional.of(companyCity);
        });

        CompanyCity companyCity = new CompanyCity();
        companyCity.setId(1L);
        companyCity.setCompany(company);
        companyCity.setCity(city2);

        companyCityService.update(companyCity);

        verify(companyCityRepository, times(1)).save(companyCityCaptor.capture());
        companyCity = companyCityCaptor.getValue();
        assertEquals("TRN", companyCity.getCompany().getCode());
        assertEquals("Teravin", companyCity.getCompany().getName());
        assertEquals("DIY", companyCity.getCity().getCode());
        assertEquals("Yogyakarta", companyCity.getCity().getName());
    }

    @Test
    public void testDelete() {

        City city = new City();
        city.setCode("JKT");
        city.setName("Jakarta");

        Company company = new Company();
        company.setCode("TRN");
        company.setName("Teravin");

        CompanyCity companyCity = new CompanyCity();
        companyCity.setId(1L);
        companyCity.setCompany(company);
        companyCity.setCity(city);

        companyCityRepository.save(companyCity);

        companyCityRepository.deleteById(1L);

        Optional<CompanyCity> companyCityDelete = companyCityRepository.findById(1L);
        assertEquals(Optional.empty(), companyCityDelete);
    }

}
