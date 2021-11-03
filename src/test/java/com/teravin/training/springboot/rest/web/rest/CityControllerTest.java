package com.teravin.training.springboot.rest.web.rest;

import com.teravin.training.springboot.rest.domain.City;
import com.teravin.training.springboot.rest.domain.Country;
import com.teravin.training.springboot.rest.service.CityService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(MockitoJUnitRunner.class)
public class CityControllerTest {

    private static final String BASE_URL = "/api/v1/city";

    private MockMvc mockMvc;

    @InjectMocks
    private CityController cityController;

    @Mock
    private CityService cityService;

    @Captor
    private ArgumentCaptor<City> cityCaptor;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.standaloneSetup(cityController).build();
    }

    @Test
    public void testFind() throws Exception {

        Country country = new Country();
        country.setCode("ID");
        country.setName("Indonesia");
        country.setDescription("Indonesia");

        when(cityService.find("JKT")).then(invocationOnMock -> {

            City city = new City();
            city.setCode("JKT");
            city.setName("Jakarta");
            city.setDescription("Jakarta");
            city.setCountry(country);
            city.setInactiveCity(false);
            city.setCreatedBy("user1");
            city.setDateCreated(OffsetDateTime.of(2021, 11, 3, 8, 30, 0, 0, ZoneOffset.of("+7")));
            city.setUpdatedBy("user2");
            city.setLastUpdated(OffsetDateTime.of(2021, 11, 4, 8, 30, 0, 0, ZoneOffset.of("+7")));

            return Optional.of(city);
        });

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/JKT")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value("JKT"))
                .andExpect(jsonPath("$.name").value("Jakarta"))
                .andExpect(jsonPath("$.description").value("Jakarta"))
                .andExpect(jsonPath("$.countryCode").value("ID"))
                .andExpect(jsonPath("$.countryName").value("Indonesia"))
                .andExpect(jsonPath("$.inactiveCity").value(false))
                .andExpect(jsonPath("$.createdBy").value("user1"))
                .andExpect(jsonPath("$.dateCreated").value("03-Nov-2021 08:30:00"))
                .andExpect(jsonPath("$.updatedBy").value("user2"))
                .andExpect(jsonPath("$.lastUpdated").value("04-Nov-2021 08:30:00"));
    }

    @Test
    public void testFindSearch() throws Exception {

        Country country = new Country();
        country.setCode("US");
        country.setName("United State");

        when(cityService.findSearch(isNull(), eq("Los"), any())).then(invocationOnMock -> {

            City city = new City();
            city.setCode("LA");
            city.setName("Los Angeles");
            city.setDescription("Los Angeles");
            city.setCountry(country);
            city.setInactiveCity(false);
            city.setCreatedBy("user1");
            city.setDateCreated(OffsetDateTime.now());

            return new PageImpl<>(Collections.singletonList(city), PageRequest.of(1, 10), 1);
        });

        JSONObject request = new JSONObject();
        request.put("name", "Los");
        request.put("page", 1);
        request.put("size", 10);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .content(request.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].code").value("LA"))
                .andExpect(jsonPath("$.content[0].name").value("Los Angeles"))
                .andExpect(jsonPath("$.content[0].description").value("Los Angeles"))
                .andExpect(jsonPath("$.content[0].countryCode").value("US"))
                .andExpect(jsonPath("$.content[0].countryName").value("United State"))
                .andExpect(jsonPath("$.content[0].inactiveCity").value(false))
                .andExpect(jsonPath("$.content[0].createdBy").value("user1"))
                .andExpect(jsonPath("$.content[0].dateCreated").exists());
    }

    @Test
    public void testFindDroplist() throws Exception {

        when(cityService.findDroplist()).then(invocationOnMock -> {

            List<City> cityList = new ArrayList<>();

            City city = new City();
            city.setCode("SBY");
            city.setName("Surabaya");
            cityList.add(city);

            city = new City();
            city.setCode("JBR");
            city.setName("Jember");
            cityList.add(city);

            return cityList;
        });

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/droplist")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("SBY"))
                .andExpect(jsonPath("$[0].value").value("Surabaya"))
                .andExpect(jsonPath("$[1].id").value("JBR"))
                .andExpect(jsonPath("$[1].value").value("Jember"));
    }

    @Test
    public void testSave() throws Exception {

        JSONObject request = new JSONObject();
        request.put("code", "JKT");
        request.put("name", "Jakarta");
        request.put("description", "Jakarta");
        request.put("countryCode", "ID");
        request.put("countryName", "Indonesia");
        request.put("inactiveCity", true);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .content(request.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(cityService, times(1)).save(cityCaptor.capture());
        City city = cityCaptor.getValue();
        assertEquals("JKT", city.getCode());
        assertEquals("Jakarta", city.getName());
        assertEquals("Jakarta", city.getDescription());
        assertEquals("ID", city.getCountry().getCode());
        assertEquals("Indonesia", city.getCountry().getName());
        assertTrue(city.isInactiveCity());
    }

    @Test
    public void testUpdate() throws Exception {

        JSONObject request = new JSONObject();
        request.put("code", "JKT");
        request.put("name", "Jakarta");
        request.put("description", "Jakarta");
        request.put("countryCode", "ID");
        request.put("countryName", "Indonesia");
        request.put("inactiveCity", true);

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL)
                        .content(request.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(cityService, times(1)).update(cityCaptor.capture());
        City city = cityCaptor.getValue();
        assertEquals("JKT", city.getCode());
        assertEquals("Jakarta", city.getName());
        assertEquals("Jakarta", city.getDescription());
        assertEquals("ID", city.getCountry().getCode());
        assertEquals("Indonesia", city.getCountry().getName());
    }

    @Test
    public void testDelete() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/JKT")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(cityService, times(1)).delete(eq("JKT"));
    }
}