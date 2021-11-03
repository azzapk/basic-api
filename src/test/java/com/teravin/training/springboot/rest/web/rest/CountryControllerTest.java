package com.teravin.training.springboot.rest.web.rest;

import com.teravin.training.springboot.rest.domain.Country;
import com.teravin.training.springboot.rest.service.CountryService;
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
public class CountryControllerTest {

    private static final String BASE_URL = "/api/v1/country";

    private MockMvc mockMvc;

    @InjectMocks
    private CountryController countryController;

    @Mock
    private CountryService countryService;

    @Captor
    private ArgumentCaptor<Country> countryCaptor;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.standaloneSetup(countryController).build();
    }

    @Test
    public void testFind() throws Exception {

        when(countryService.find("TH")).then(invocationOnMock -> {

            Country country = new Country();
            country.setCode("TH");
            country.setName("Thailand");
            country.setDescription("Thailand");
            country.setInactiveFlag(false);
            country.setCreatedBy("admin1");
            country.setDateCreated(OffsetDateTime.of(2019, 10, 14, 9, 20, 0, 0, ZoneOffset.of("+7")));
            country.setUpdatedBy("admin2");
            country.setLastUpdated(OffsetDateTime.of(2019, 10, 14, 16, 30, 0, 0, ZoneOffset.of("+7")));

            return Optional.of(country);
        });

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/TH")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value("TH"))
                .andExpect(jsonPath("$.name").value("Thailand"))
                .andExpect(jsonPath("$.description").value("Thailand"))
                .andExpect(jsonPath("$.inactiveFlag").value(false))
                .andExpect(jsonPath("$.createdBy").value("admin1"))
                .andExpect(jsonPath("$.dateCreated").value("14-Oct-2019 09:20:00"))
                .andExpect(jsonPath("$.updatedBy").value("admin2"))
                .andExpect(jsonPath("$.lastUpdated").value("14-Oct-2019 16:30:00"));
    }

    @Test
    public void testFindSearch() throws Exception {

        when(countryService.findSearch(isNull(), eq("South"), any())).then(invocationOnMock -> {

            Country country = new Country();
            country.setCode("ZA");
            country.setName("South Africa");
            country.setDescription("South Africa");
            country.setInactiveFlag(false);
            country.setCreatedBy("system");
            country.setDateCreated(OffsetDateTime.now());

            return new PageImpl<>(Collections.singletonList(country), PageRequest.of(1, 10), 1);
        });

        JSONObject request = new JSONObject();
        request.put("name", "South");
        request.put("page", 1);
        request.put("size", 10);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].code").value("ZA"))
                .andExpect(jsonPath("$.content[0].name").value("South Africa"))
                .andExpect(jsonPath("$.content[0].description").value("South Africa"))
                .andExpect(jsonPath("$.content[0].inactiveFlag").value(false))
                .andExpect(jsonPath("$.content[0].createdBy").value("system"))
                .andExpect(jsonPath("$.content[0].dateCreated").exists());
    }

    @Test
    public void testFindDroplist() throws Exception {

        when(countryService.findDroplist()).then(invocationOnMock -> {

            List<Country> countryList = new ArrayList<>();

            Country country = new Country();
            country.setCode("VN");
            country.setName("Vietnam");
            countryList.add(country);

            country = new Country();
            country.setCode("AU");
            country.setName("Australia");
            countryList.add(country);

            return countryList;
        });

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/droplist")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("VN"))
                .andExpect(jsonPath("$[0].value").value("Vietnam"))
                .andExpect(jsonPath("$[1].id").value("AU"))
                .andExpect(jsonPath("$[1].value").value("Australia"));
    }

    @Test
    public void testSave() throws Exception {

        JSONObject request = new JSONObject();
        request.put("code", "PH");
        request.put("name", "Philippines");
        request.put("description", "Republic of Philippines");
        request.put("inactiveFlag", true);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(countryService, times(1)).save(countryCaptor.capture());
        Country country = countryCaptor.getValue();
        assertEquals("PH", country.getCode());
        assertEquals("Philippines", country.getName());
        assertEquals("Republic of Philippines", country.getDescription());
        assertTrue(country.isInactiveFlag());
    }

    @Test
    public void testUpdate() throws Exception {

        JSONObject request = new JSONObject();
        request.put("code", "TH");
        request.put("name", "Thailand");
        request.put("description", "Kingdom of Thailand");
        request.put("inactiveFlag", false);

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(countryService, times(1)).update(countryCaptor.capture());
        Country country = countryCaptor.getValue();
        assertEquals("TH", country.getCode());
        assertEquals("Thailand", country.getName());
        assertEquals("Kingdom of Thailand", country.getDescription());
    }

    @Test
    public void testDelete() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/SG")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(countryService, times(1)).delete(eq("SG"));
    }
}