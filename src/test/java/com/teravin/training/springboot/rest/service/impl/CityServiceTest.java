package com.teravin.training.springboot.rest.service.impl;

import com.teravin.base.RecordAlreadyExistException;
import com.teravin.base.RecordNotFoundException;
import com.teravin.base.context.UserContext;
import com.teravin.training.springboot.rest.domain.City;
import com.teravin.training.springboot.rest.domain.Country;
import com.teravin.training.springboot.rest.repository.CityRepository;
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
public class CityServiceTest {

    @InjectMocks
    private CityServiceImpl cityService;

    @Mock
    private CityRepository cityRepository;

    @Captor
    private ArgumentCaptor<City> cityCaptor;

    @After
    public void after() {
        UserContext.remove();
    }

    @Test
    public void testFind() {

        Country country = new Country();
        country.setCode("ID");
        country.setName("Indonesia");

        when(cityRepository.findById("JKT")).then(invocationOnMock -> {

            City city = new City();
            city.setCode("JKT");
            city.setName("Jakarta");
            city.setDescription("Jakarta");
            city.setCountry(country);
            city.setDeleteCity(false);
            city.setInactiveCity(false);
            city.setIndex(999);
            city.setCreatedBy("system");
            city.setDateCreated(OffsetDateTime.now());

            return Optional.of(city);
        });

        Optional<City> cityOptional = cityService.find("JKT");
        assertTrue(cityOptional.isPresent());

        City city = cityOptional.get();
        assertEquals("JKT", city.getCode());
        assertEquals("Jakarta", city.getName());
        assertEquals("Jakarta", city.getDescription());
        assertEquals(country, city.getCountry());
        assertFalse(city.isInactiveCity());
        assertFalse(city.isInactiveCity());
        assertEquals("system", city.getCreatedBy());
        assertNotNull(city.getDateCreated());
    }

    @Test
    public void testFindDroplist() {

        Country country = new Country();
        country.setCode("ID");
        country.setName("Indonesia");

        when(cityRepository.findByInactiveCityFalseAndDeleteCityFalseOrderByIndexAsc()).then(invocationOnMock -> {

            List<City> cityList = new ArrayList<>();

            City city = new City();
            city.setCode("JKT");
            city.setName("Jakarta");
            city.setCountry(country);
            cityList.add(city);

            city = new City();
            city.setCode("DIY");
            city.setName("Yogyakarta");
            city.setCountry(country);
            cityList.add(city);

            return cityList;
        });

        List<City> cityList = cityService.findDroplist();
        assertEquals(2, cityList.size());

        City city = cityList.get(0);
        assertEquals("JKT", city.getCode());
        assertEquals("Jakarta", city.getName());
        assertEquals(country, city.getCountry());

        city = cityList.get(1);
        assertEquals("DIY", city.getCode());
        assertEquals("Yogyakarta", city.getName());
        assertEquals(country, city.getCountry());
    }

    @Test
    public void testFindSearch() {

        Country country = new Country();
        country.setCode("ID");
        country.setName("Indonesia");

        when(cityRepository.findByCodeContainingAndNameContainingAndDeleteCityFalseOrderByCode("", "jak", PageRequest.of(1, 20))).then(invocationOnMock -> {

            City city = new City();
            city.setCode("JKT");
            city.setName("Jakarta");
            city.setDescription("Jakarta");
            city.setCountry(country);

            return new PageImpl<>(Collections.singletonList(city), PageRequest.of(1, 20), 1);
        });

        Page<City> cityPage = cityService.findSearch(null, "jak", PageRequest.of(1, 20));

        List<City> cityList = cityPage.getContent();
        assertEquals(1, cityList.size());
        City city = cityList.get(0);
        assertEquals("JKT", city.getCode());
        assertEquals("Jakarta", city.getName());
        assertEquals("Jakarta", city.getDescription());
        assertEquals(country, city.getCountry());
    }

    @Test
    public void testSave() {

        UserContext.setUserId("user1");

        Country country = new Country();
        country.setCode("ID");
        country.setName("Indonesia");
        country.setDescription("Indonesia");

        when(cityRepository.findById("JBR")).thenReturn(Optional.empty());

        City city = new City();
        city.setCode("JBR");
        city.setName("Jember");
        city.setDescription("Jember");
        city.setCountry(country);
        city.setInactiveCity(false);

        cityService.save(city);

        verify(cityRepository, times(1)).save(cityCaptor.capture());
        city = cityCaptor.getValue();
        assertEquals("JBR", city.getCode());
        assertEquals("Jember", city.getName());
        assertEquals("Jember", city.getDescription());
        assertEquals(country, city.getCountry());
        assertEquals(country.getCode(), city.getCountry().getCode());
        assertEquals(country.getName(), city.getCountry().getName());
        assertEquals(country.getDescription(), city.getCountry().getDescription());
        assertFalse(city.isInactiveCity());
        assertFalse(city.isDeleteCity());
        assertEquals("user1", city.getCreatedBy());
        assertNotNull(city.getDateCreated());
        assertNull(city.getUpdatedBy());
        assertNull(city.getLastUpdated());
    }

    @Test(expected = RecordAlreadyExistException.class)
    public void testSaveWithRecordAlreadyExist() {

        Country country = new Country();
        country.setCode("ID");
        country.setName("Indonesia");

        when((cityRepository.findById("DIY"))).then(invocationOnMock -> {

            City city = new City();
            city.setCode("DIY");
            city.setName("Yogyakarta");
            city.setDescription("Yogyakarta");
            city.setCountry(country);

            return Optional.of(city);
        });

        City city = new City();
        city.setCode("DIY");
        city.setName("Yogyakarta");
        city.setDescription("Daerah Istimewa Yogyakarta");
        city.setCountry(country);

        cityService.save(city);
    }

    @Test
    public void testUpdate() {

        UserContext.setUserId("user2");

        Country country1 = new Country();
        country1.setCode("SG");
        country1.setName("Singapore");

        Country country2 = new Country();
        country2.setCode("ID");
        country2.setName("Indonesia");

        when(cityRepository.findByCodeAndDeleteCityFalse("SBY")).then(invocationOnMock -> {

            City city = new City();
            city.setCode("SBY");
            city.setName("Surabaya");
            city.setDescription("Surabaya");
            city.setInactiveCity(true);
            city.setDeleteCity(false);
            city.setCreatedBy("user1");
            city.setCountry(country1);
            city.setDateCreated(OffsetDateTime.of(2021, 11,3, 8, 30, 0, 0, ZoneOffset.of("+7")));

            return Optional.of(city);
        });

        City city = new City();
        city.setCode("SBY");
        city.setName("Surabaya");
        city.setDescription("Surabaya City");
        city.setInactiveCity(false);
        city.setCountry(country2);

        cityService.update(city);

        verify(cityRepository, times(1)).save(cityCaptor.capture());
        city = cityCaptor.getValue();
        assertEquals("SBY", city.getCode());
        assertEquals("Surabaya", city.getName());
        assertEquals("Surabaya City", city.getDescription());
        assertEquals(country2, city.getCountry());
        assertEquals(country2.getCode(), city.getCountry().getCode());
        assertEquals(country2.getName(), city.getCountry().getName());
        assertFalse(city.isDeleteCity());
        assertFalse(city.isInactiveCity());
        assertEquals("user1", city.getCreatedBy());
        assertEquals(OffsetDateTime.of(2021, 11,3, 8, 30, 0, 0, ZoneOffset.of("+7")), city.getDateCreated());
        assertEquals("user2", city.getUpdatedBy());
        assertNotNull(city.getLastUpdated());
    }

    @Test(expected = RecordNotFoundException.class)
    public void testUpdateWithRecordNotFound() {

        Country country = new Country();
        country.setCode("ID");
        country.setName("Indonesia");

        when(cityRepository.findByCodeAndDeleteCityFalse("LMG")).thenReturn(Optional.empty());

        City city = new City();
        city.setCode("LMG");
        city.setName("Lumajang");
        city.setDescription("Lumajang");
        city.setCountry(country);
        city.setInactiveCity(true);

        cityService.update(city);
    }

    @Test
    public void testDelete() {

        UserContext.setUserId("user3");

        Country country = new Country();
        country.setCode("CA");
        country.setName("California");

        when(cityRepository.findByCodeAndDeleteCityFalse("LA")).then(invocationOnMock -> {

            City city = new City();
            city.setCode("LA");
            city.setName("Los Angeles");
            city.setDescription("Los Angeles");
            city.setCountry(country);
            city.setInactiveCity(false);
            city.setDeleteCity(false);
            city.setCreatedBy("user1");
            city.setDateCreated(OffsetDateTime.of(2021, 11,3, 8, 30, 0, 0, ZoneOffset.of("+7")));

            return Optional.of(city);
        });

        cityService.delete("LA");

        verify(cityRepository, times(1)).save(cityCaptor.capture());
        City city = cityCaptor.getValue();
        assertEquals("LA", city.getCode());
        assertEquals("Los Angeles", city.getName());
        assertEquals("Los Angeles", city.getDescription());
        assertTrue(city.isDeleteCity());
        assertFalse(city.isInactiveCity());
        assertEquals("user1", city.getCreatedBy());
        assertEquals(OffsetDateTime.of(2021, 11,3, 8, 30, 0, 0, ZoneOffset.of("+7")), city.getDateCreated());
        assertEquals("user3", city.getUpdatedBy());
        assertNotNull(city.getLastUpdated());
    }

    @Test(expected = RecordNotFoundException.class)
    public void testDeleteWithRecordNotFound() {

        when(cityRepository.findByCodeAndDeleteCityFalse("LA")).thenReturn(Optional.empty());

        cityService.delete("LA");
    }
}