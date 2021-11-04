package com.teravin.training.springboot.rest.web.rest;

import com.teravin.training.springboot.rest.domain.City;
import com.teravin.training.springboot.rest.domain.CompanyCity;
import com.teravin.training.springboot.rest.service.CityService;
import com.teravin.training.springboot.rest.service.CompanyCityService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CompanyCityControllerTest {

    private static final String BASE_URL = "/api/v1/company_city";

    private MockMvc mockMvc;

    @InjectMocks
    private CompanyCityController companyCityController;

    @Mock
    private CompanyCityService companyCityService;

    @Captor
    private ArgumentCaptor<CompanyCity> companyCityCaptor;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.standaloneSetup(companyCityController).build();
    }

    @Test
    public void testSave() throws Exception {

        JSONObject request = new JSONObject();
        request.put("companyId", "TRN");
        request.put("companyName", "Teravin");
        request.put("cityId", "JKT");
        request.put("cityName", "Jakarta");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .content(request.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(companyCityService, times(1)).save(companyCityCaptor.capture());
        CompanyCity companyCity = companyCityCaptor.getValue();
        assertEquals("TRN", companyCity.getCompany().getCode());
        assertEquals("Teravin", companyCity.getCompany().getName());
        assertEquals("JKT", companyCity.getCity().getCode());
        assertEquals("Jakarta", companyCity.getCity().getName());
    }

    @Test
    public void testUpdate() throws Exception {

        JSONObject request = new JSONObject();
        request.put("companyId", "TRN");
        request.put("companyName", "Teravin");
        request.put("cityId", "JKT");
        request.put("cityName", "Jakarta");

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL)
                        .content(request.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(companyCityService, times(1)).update(companyCityCaptor.capture());
        CompanyCity companyCity = companyCityCaptor.getValue();
        assertEquals("TRN", companyCity.getCompany().getCode());
        assertEquals("Teravin", companyCity.getCompany().getName());
        assertEquals("JKT", companyCity.getCity().getCode());
        assertEquals("Jakarta", companyCity.getCity().getName());
    }

    @Test
    public void testDelete() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(companyCityService, times(1)).delete(eq(1L));
    }
}
