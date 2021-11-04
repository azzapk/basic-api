package com.teravin.training.springboot.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CompanyDto {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;

    private boolean inactiveCompany;

    private String createdBy;

    private String dateCreated;

    private String updatedBy;

    private String lastUpdated;
}
