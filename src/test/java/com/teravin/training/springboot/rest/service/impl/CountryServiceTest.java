package com.teravin.training.springboot.rest.service.impl;

import com.teravin.base.RecordAlreadyExistException;
import com.teravin.base.RecordNotFoundException;
import com.teravin.base.context.UserContext;
import com.teravin.training.springboot.rest.domain.Country;
import com.teravin.training.springboot.rest.repository.CountryRepository;
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
public class CountryServiceTest {

    @InjectMocks
    private CountryServiceImpl countryService;

    @Mock
    private CountryRepository countryRepository;

    @Captor
    private ArgumentCaptor<Country> countryCaptor;

    @After
    public void after() {
        UserContext.remove();
    }

    @Test
    public void testFind() {

        when(countryRepository.findById("ID")).then(invocationOnMock -> {

            Country country = new Country();
            country.setCode("ID");
            country.setName("Indonesia");
            country.setDescription("Indonesia");
            country.setDeleteFlag(false);
            country.setInactiveFlag(false);
            country.setIndex(999);
            country.setCreatedBy("system");
            country.setDateCreated(OffsetDateTime.now());

            return Optional.of(country);
        });

        Optional<Country> countryOptional = countryService.find("ID");
        assertTrue(countryOptional.isPresent());

        Country country = countryOptional.get();
        assertEquals("ID", country.getCode());
        assertEquals("Indonesia", country.getName());
        assertEquals("Indonesia", country.getDescription());
        assertFalse(country.isDeleteFlag());
        assertFalse(country.isInactiveFlag());
        assertEquals("system", country.getCreatedBy());
        assertNotNull(country.getDateCreated());
    }

    @Test
    public void testFindDroplist() {

        when(countryRepository.findByInactiveFlagFalseAndDeleteFlagFalseOrderByIndexAsc()).then(invocationOnMock -> {

            List<Country> countryList = new ArrayList<>();

            Country country = new Country();
            country.setCode("SG");
            country.setName("Singapore");
            countryList.add(country);

            country = new Country();
            country.setCode("MY");
            country.setName("Malaysia");
            countryList.add(country);

            return countryList;
        });

        List<Country> countryList = countryService.findDroplist();
        assertEquals(2, countryList.size());

        Country country = countryList.get(0);
        assertEquals("SG", country.getCode());
        assertEquals("Singapore", country.getName());

        country = countryList.get(1);
        assertEquals("MY", country.getCode());
        assertEquals("Malaysia", country.getName());
    }

    @Test
    public void testFindSearch() {

        when(countryRepository.findByCodeContainingAndNameContainingAndDeleteFlagFalseOrderByCode("", "indo", PageRequest.of(1, 20))).then(invocationOnMock -> {

            Country country = new Country();
            country.setCode("ID");
            country.setName("Indonesia");
            country.setDescription("Indonesia");

            return new PageImpl<>(Collections.singletonList(country), PageRequest.of(1, 20), 1);
        });

        Page<Country> countryPage = countryService.findSearch(null, "indo", PageRequest.of(1, 20));

        List<Country> countryList = countryPage.getContent();
        assertEquals(1, countryList.size());
        Country country = countryList.get(0);
        assertEquals("ID", country.getCode());
        assertEquals("Indonesia", country.getName());
        assertEquals("Indonesia", country.getDescription());
    }

    @Test
    public void testSave() {

        UserContext.setUserId("admin5");

        when(countryRepository.findById("MY")).thenReturn(Optional.empty());

        Country country = new Country();
        country.setCode("MY");
        country.setName("Malaysia");
        country.setDescription("Malaysia");
        country.setInactiveFlag(false);

        countryService.save(country);

        verify(countryRepository, times(1)).save(countryCaptor.capture());
        country = countryCaptor.getValue();
        assertEquals("MY", country.getCode());
        assertEquals("Malaysia", country.getName());
        assertEquals("Malaysia", country.getDescription());
        assertFalse(country.isInactiveFlag());
        assertFalse(country.isDeleteFlag());
        assertEquals("admin5", country.getCreatedBy());
        assertNotNull(country.getDateCreated());
        assertNull(country.getUpdatedBy());
        assertNull(country.getLastUpdated());
    }

    @Test(expected = RecordAlreadyExistException.class)
    public void testSaveWithRecordAlreadyExist() {

        when((countryRepository.findById("SG"))).then(invocationOnMock -> {

            Country country = new Country();
            country.setCode("SG");
            country.setName("Singapore");

            return Optional.of(country);
        });

        Country country = new Country();
        country.setCode("SG");
        country.setName("Singapore");
        country.setDescription("Republic of Singapore");
        country.setInactiveFlag(false);

        countryService.save(country);
    }

    @Test
    public void testUpdate() {

        UserContext.setUserId("admin3");

        when(countryRepository.findByCodeAndDeleteFlagFalse("MM")).then(invocationOnMock -> {

            Country country = new Country();
            country.setCode("MM");
            country.setName("Myanmar");
            country.setDescription("Myanmar");
            country.setInactiveFlag(true);
            country.setDeleteFlag(false);
            country.setCreatedBy("admin1");
            country.setDateCreated(OffsetDateTime.of(2019, 10,14, 22, 50, 0, 0, ZoneOffset.of("+7")));

            return Optional.of(country);
        });

        Country country = new Country();
        country.setCode("MM");
        country.setName("Myanmar");
        country.setDescription("The Republic of the Union of Myanmar");
        country.setInactiveFlag(false);

        countryService.update(country);

        verify(countryRepository, times(1)).save(countryCaptor.capture());
        country = countryCaptor.getValue();
        assertEquals("MM", country.getCode());
        assertEquals("Myanmar", country.getName());
        assertEquals("The Republic of the Union of Myanmar", country.getDescription());
        assertFalse(country.isDeleteFlag());
        assertFalse(country.isInactiveFlag());
        assertEquals("admin1", country.getCreatedBy());
        assertEquals(OffsetDateTime.of(2019, 10,14, 22, 50, 0, 0, ZoneOffset.of("+7")), country.getDateCreated());
        assertEquals("admin3", country.getUpdatedBy());
        assertNotNull(country.getLastUpdated());
    }

    @Test(expected = RecordNotFoundException.class)
    public void testUpdateWithRecordNotFound() {

        when(countryRepository.findByCodeAndDeleteFlagFalse("NL")).thenReturn(Optional.empty());

        Country country = new Country();
        country.setCode("NL");
        country.setName("Netherlands");
        country.setDescription("Netherlands");
        country.setInactiveFlag(true);

        countryService.update(country);
    }

    @Test
    public void testDelete() {

        UserContext.setUserId("admin2");

        when(countryRepository.findByCodeAndDeleteFlagFalse("DE")).then(invocationOnMock -> {

            Country country = new Country();
            country.setCode("DE");
            country.setName("German");
            country.setDescription("German");
            country.setInactiveFlag(false);
            country.setDeleteFlag(false);
            country.setCreatedBy("admin1");
            country.setDateCreated(OffsetDateTime.of(2019, 10,14, 22, 40, 0, 0, ZoneOffset.of("+7")));

            return Optional.of(country);
        });

        countryService.delete("DE");

        verify(countryRepository, times(1)).save(countryCaptor.capture());
        Country country = countryCaptor.getValue();
        assertEquals("DE", country.getCode());
        assertEquals("German", country.getName());
        assertEquals("German", country.getDescription());
        assertTrue(country.isDeleteFlag());
        assertFalse(country.isInactiveFlag());
        assertEquals("admin1", country.getCreatedBy());
        assertEquals(OffsetDateTime.of(2019, 10,14, 22, 40, 0, 0, ZoneOffset.of("+7")), country.getDateCreated());
        assertEquals("admin2", country.getUpdatedBy());
        assertNotNull(country.getLastUpdated());
    }

    @Test(expected = RecordNotFoundException.class)
    public void testDeleteWithRecordNotFound() {

        when(countryRepository.findByCodeAndDeleteFlagFalse("ZA")).thenReturn(Optional.empty());

        countryService.delete("ZA");
    }
}