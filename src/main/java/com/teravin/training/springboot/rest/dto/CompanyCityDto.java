package com.teravin.training.springboot.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CompanyCityDto {

    @NotBlank
    private String companyId;

    @NotBlank
    private String companyName;

    @NotBlank
    private String cityId;

    @NotBlank
    private String cityName;
}
