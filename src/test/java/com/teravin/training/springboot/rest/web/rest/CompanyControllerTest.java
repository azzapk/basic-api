package com.teravin.training.springboot.rest.web.rest;

import com.teravin.training.springboot.rest.domain.Company;
import com.teravin.training.springboot.rest.domain.Country;
import com.teravin.training.springboot.rest.service.CompanyService;
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
public class CompanyControllerTest {

    private static final String BASE_URL = "/api/v1/company";

    private MockMvc mockMvc;

    @InjectMocks
    private CompanyController companyController;

    @Mock
    private CompanyService companyService;

    @Captor
    private ArgumentCaptor<Company> companyCaptor;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.standaloneSetup(companyController).build();
    }

    @Test
    public void testFind() throws Exception {

        when(companyService.find("TRN")).then(invocationOnMock -> {

            Company company = new Company();
            company.setCode("TRN");
            company.setName("Teravin");
            company.setDescription("Teravin");
            company.setInactiveCompany(false);
            company.setCreatedBy("admin1");
            company.setDateCreated(OffsetDateTime.of(2021, 11, 4, 8, 30, 0, 0, ZoneOffset.of("+7")));
            company.setUpdatedBy("admin2");
            company.setLastUpdated(OffsetDateTime.of(2021, 11, 5, 8, 30, 0, 0, ZoneOffset.of("+7")));

            return Optional.of(company);
        });

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/TRN")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value("TRN"))
                .andExpect(jsonPath("$.name").value("Teravin"))
                .andExpect(jsonPath("$.description").value("Teravin"))
                .andExpect(jsonPath("$.inactiveCompany").value(false))
                .andExpect(jsonPath("$.createdBy").value("admin1"))
                .andExpect(jsonPath("$.dateCreated").value("04-Nov-2021 08:30:00"))
                .andExpect(jsonPath("$.updatedBy").value("admin2"))
                .andExpect(jsonPath("$.lastUpdated").value("05-Nov-2021 08:30:00"));
    }

    @Test
    public void testFindSearch() throws Exception {

        when(companyService.findSearch(isNull(), eq("Kereta"), any())).then(invocationOnMock -> {

            Company company = new Company();
            company.setCode("KAI");
            company.setName("Kereta Api Indonesia");
            company.setDescription("Kereta Api Indonesia");
            company.setInactiveCompany(false);
            company.setCreatedBy("system");
            company.setDateCreated(OffsetDateTime.now());

            return new PageImpl<>(Collections.singletonList(company), PageRequest.of(1, 10), 1);
        });

        JSONObject request = new JSONObject();
        request.put("name", "Kereta");
        request.put("page", 1);
        request.put("size", 10);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .content(request.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].code").value("KAI"))
                .andExpect(jsonPath("$.content[0].name").value("Kereta Api Indonesia"))
                .andExpect(jsonPath("$.content[0].description").value("Kereta Api Indonesia"))
                .andExpect(jsonPath("$.content[0].inactiveCompany").value(false))
                .andExpect(jsonPath("$.content[0].createdBy").value("system"))
                .andExpect(jsonPath("$.content[0].dateCreated").exists());
    }

    @Test
    public void testFindDroplist() throws Exception {

        when(companyService.findDroplist()).then(invocationOnMock -> {

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

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/droplist")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("TRN"))
                .andExpect(jsonPath("$[0].value").value("Teravin"))
                .andExpect(jsonPath("$[1].id").value("KAI"))
                .andExpect(jsonPath("$[1].value").value("Kereta Api Indonesia"));
    }

    @Test
    public void testSave() throws Exception {

        JSONObject request = new JSONObject();
        request.put("code", "TRN");
        request.put("name", "Teravin");
        request.put("description", "Teravin Technovations");
        request.put("inactiveCompany", true);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .content(request.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(companyService, times(1)).save(companyCaptor.capture());
        Company company = companyCaptor.getValue();
        assertEquals("TRN", company.getCode());
        assertEquals("Teravin", company.getName());
        assertEquals("Teravin Technovations", company.getDescription());
        assertTrue(company.isInactiveCompany());
    }

    @Test
    public void testUpdate() throws Exception {

        JSONObject request = new JSONObject();
        request.put("code", "TRN");
        request.put("name", "Teravin");
        request.put("description", "Teravin Technovations");
        request.put("inactiveCompany", false);

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL)
                        .content(request.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(companyService, times(1)).update(companyCaptor.capture());
        Company company = companyCaptor.getValue();
        assertEquals("TRN", company.getCode());
        assertEquals("Teravin", company.getName());
        assertEquals("Teravin Technovations", company.getDescription());
    }

    @Test
    public void testDelete() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/TRN")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(companyService, times(1)).delete(eq("TRN"));
    }
}