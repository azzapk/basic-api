package com.teravin.training.springboot.rest.service.impl;

import com.teravin.base.RecordAlreadyExistException;
import com.teravin.base.RecordNotFoundException;
import com.teravin.base.context.UserContext;
import com.teravin.training.springboot.rest.domain.Company;
import com.teravin.training.springboot.rest.repository.CompanyRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompanyServiceTest {

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Captor
    private ArgumentCaptor<Company> companyCaptor;

    @After
    public void after() {
        UserContext.remove();
    }

    @Test
    public void testFind() {

        when(companyRepository.findById("TRN")).then(invocationOnMock -> {

            Company company = new Company();
            company.setCode("TRN");
            company.setName("Teravin");
            company.setDescription("Teravin");
            company.setInactiveCompany(false);
            company.setDeleteCompany(false);
            company.setIndex(999);
            company.setCreatedBy("system");
            company.setDateCreated(OffsetDateTime.now());

            return Optional.of(company);
        });

        Optional<Company> companyOptional = companyService.find("TRN");
        assertTrue(companyOptional.isPresent());

        Company company = companyOptional.get();
        assertEquals("TRN", company.getCode());
        assertEquals("Teravin",company.getName());
        assertEquals("Teravin", company.getDescription());
        assertFalse(company.isInactiveCompany());
        assertFalse(company.isDeleteCompany());
        assertEquals("system", company.getCreatedBy());
        assertNotNull(company.getDateCreated());
    }

    @Test
    public void testFindDroplist() {

        when(companyRepository.findByInactiveCompanyFalseAndDeleteCompanyFalseOrderByIndexAsc()).then(invocationOnMock -> {

            List<Company> companyList = new ArrayList<>();

            Company company = new Company();
            company.setCode("TRN");
            company.setName("Teravin");
            companyList.add(company);

            company = new Company();
            company.setCode("KAI");
            company.setName("Kereta Api Indonesia");
            companyList.add(company);

            return companyList;
        });

        List<Company> companyList = companyService.findDroplist();
        assertEquals(2, companyList.size());

        Company company = companyList.get(0);
        assertEquals("TRN", company.getCode());
        assertEquals("Teravin", company.getName());

        company = companyList.get(1);
        assertEquals("KAI", company.getCode());
        assertEquals("Kereta Api Indonesia", company.getName());
    }

    @Test
    public void testFindSearch() {

        when(companyRepository.findByCodeContainingAndNameContainingAndDeleteCompanyFalseOrderByCode("", "ter", PageRequest.of(1, 20))).then(invocationOnMock -> {

            Company company = new Company();
            company.setCode("TRN");
            company.setName("Teravin");
            company.setDescription("Teravin");

            return new PageImpl<>(Collections.singletonList(company), PageRequest.of(1, 20), 1);
        });

        Page<Company> companyPage = companyService.findSearch(null, "ter", PageRequest.of(1, 20));

        List<Company> companyList = companyPage.getContent();
        assertEquals(1, companyList.size());
        Company company = companyList.get(0);
        assertEquals("TRN", company.getCode());
        assertEquals("Teravin", company.getName());
        assertEquals("Teravin", company.getDescription());
    }

    @Test
    public void testSave() {

        UserContext.setUserId("admin5");

        when(companyRepository.findById("TRN")).thenReturn(Optional.empty());

        Company company = new Company();
        company.setCode("TRN");
        company.setName("Teravin");
        company.setDescription("Teravin");
        company.setInactiveCompany(false);

        companyService.save(company);

        verify(companyRepository, times(1)).save(companyCaptor.capture());
        company = companyCaptor.getValue();
        assertEquals("TRN", company.getCode());
        assertEquals("Teravin", company.getName());
        assertEquals("Teravin", company.getDescription());
        assertFalse(company.isInactiveCompany());
        assertFalse(company.isDeleteCompany());
        assertEquals("admin5", company.getCreatedBy());
        assertNotNull(company.getDateCreated());
        assertNull(company.getUpdatedBy());
        assertNull(company.getLastUpdated());
    }

    @Test(expected = RecordAlreadyExistException.class)
    public void testSaveWithRecordAlreadyExist() {

        when((companyRepository.findById("TRN"))).then(invocationOnMock -> {

            Company company = new Company();
            company.setCode("TRN");
            company.setName("Teravin");

            return Optional.of(company);
        });

        Company company = new Company();
        company.setCode("TRN");
        company.setName("Teravin");
        company.setDescription("Teravin Technovations");
        company.setInactiveCompany(false);

        companyService.save(company);
    }

    @Test
    public void testUpdate() {

        UserContext.setUserId("admin3");

        when(companyRepository.findByCodeAndDeleteCompanyFalse("TRN")).then(invocationOnMock -> {

            Company company = new Company();
            company.setCode("TRN");
            company.setName("Teravin");
            company.setDescription("Teravin");
            company.setInactiveCompany(true);
            company.setDeleteCompany(false);
            company.setCreatedBy("admin1");
            company.setDateCreated(OffsetDateTime.of(2021, 11,4, 8, 30, 0, 0, ZoneOffset.of("+7")));

            return Optional.of(company);
        });

        Company company = new Company();
        company.setCode("TRN");
        company.setName("Teravin");
        company.setDescription("Teravin Technovations");
        company.setInactiveCompany(false);

        companyService.update(company);

        verify(companyRepository, times(1)).save(companyCaptor.capture());
        company = companyCaptor.getValue();
        assertEquals("TRN", company.getCode());
        assertEquals("Teravin", company.getName());
        assertEquals("Teravin Technovations", company.getDescription());
        assertFalse(company.isInactiveCompany());
        assertFalse(company.isDeleteCompany());
        assertEquals("admin1", company.getCreatedBy());
        assertEquals(OffsetDateTime.of(2021, 11,4, 8, 30, 0, 0, ZoneOffset.of("+7")), company.getDateCreated());
        assertEquals("admin3", company.getUpdatedBy());
        assertNotNull(company.getLastUpdated());
    }

    @Test(expected = RecordNotFoundException.class)
    public void testUpdateWithRecordNotFound() {

        when(companyRepository.findByCodeAndDeleteCompanyFalse("TRN")).thenReturn(Optional.empty());

        Company company = new Company();
        company.setCode("TRN");
        company.setName("Teravin");
        company.setDescription("Teravin Technovations");
        company.setInactiveCompany(true);

        companyService.update(company);
    }

    @Test
    public void testDelete() {

        UserContext.setUserId("admin2");

        when(companyRepository.findByCodeAndDeleteCompanyFalse("TRN")).then(invocationOnMock -> {

            Company company = new Company();
            company.setCode("TRN");
            company.setName("Teravin");
            company.setDescription("Teravin Technovations");
            company.setInactiveCompany(false);
            company.setDeleteCompany(false);
            company.setCreatedBy("admin1");
            company.setDateCreated(OffsetDateTime.of(2021, 11,4, 8, 30, 0, 0, ZoneOffset.of("+7")));

            return Optional.of(company);
        });

        companyService.delete("TRN");

        verify(companyRepository, times(1)).save(companyCaptor.capture());
        Company company = companyCaptor.getValue();
        assertEquals("TRN", company.getCode());
        assertEquals("Teravin", company.getName());
        assertEquals("Teravin Technovations", company.getDescription());
        assertFalse(company.isInactiveCompany());
        assertTrue(company.isDeleteCompany());
        assertEquals("admin1", company.getCreatedBy());
        assertEquals(OffsetDateTime.of(2021, 11,4, 8, 30, 0, 0, ZoneOffset.of("+7")), company.getDateCreated());
        assertEquals("admin2", company.getUpdatedBy());
        assertNotNull(company.getLastUpdated());
    }

    @Test(expected = RecordNotFoundException.class)
    public void testDeleteWithRecordNotFound() {

        when(companyRepository.findByCodeAndDeleteCompanyFalse("TRN")).thenReturn(Optional.empty());

        companyService.delete("TRN");
    }
}