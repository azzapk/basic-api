package com.teravin.training.springboot.rest.dto;

import com.teravin.training.springboot.rest.domain.Country;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CityDto {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;

    private String countryCode;

    private String countryName;

    private boolean inactiveCity;

    private String createdBy;

    private String dateCreated;

    private String updatedBy;

    private String lastUpdated;
}
