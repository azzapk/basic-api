package com.teravin.training.springboot.rest.dto;

import com.teravin.base.dto.SearchDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CountrySearchDto extends SearchDto {

    private String code;

    private String name;
}