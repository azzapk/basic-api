package com.teravin.training.springboot.rest.web.rest;

import com.teravin.base.RecordNotFoundException;
import com.teravin.base.dto.DroplistDto;
import com.teravin.base.util.SearchUtils;
import com.teravin.training.springboot.rest.domain.Country;
import com.teravin.training.springboot.rest.dto.CountryDto;
import com.teravin.training.springboot.rest.dto.CountrySearchDto;
import com.teravin.training.springboot.rest.service.CountryService;
import com.teravin.training.springboot.rest.util.DateTimeFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/country")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @GetMapping("/{code}")
    public CountryDto find(@PathVariable String code) {

        return countryService.find(code)
                .map(this::toCountryDto)
                .orElseThrow(() -> new RecordNotFoundException(code));
    }

    @GetMapping("")
    public Page<CountryDto> findSearch(@RequestBody CountrySearchDto countrySearchDto) {

        return countryService.findSearch(
                countrySearchDto.getCode(),
                countrySearchDto.getName(),
                SearchUtils.toPageable(countrySearchDto))
                .map(this::toCountryDto);
    }

    @GetMapping("/droplist")
    public List<DroplistDto> findDroplist() {

        return countryService.findDroplist()
                .stream()
                .map(this::toDroplistDto)
                .collect(Collectors.toList());
    }

    @PostMapping("")
    public void save(@Valid @RequestBody CountryDto countryDto) {
        countryService.save(toCountry(countryDto));
    }

    @PatchMapping("")
    public void update(@Valid @RequestBody CountryDto countryDto) {
        countryService.update(toCountry(countryDto));
    }

    @DeleteMapping("/{code}")
    public void delete(@PathVariable String code) {
        countryService.delete(code);
    }

    private CountryDto toCountryDto(Country country) {

        CountryDto countryDto = new CountryDto();
        countryDto.setCode(country.getCode());
        countryDto.setName(country.getName());
        countryDto.setDescription(country.getDescription());
        countryDto.setInactiveFlag(country.isInactiveFlag());
        countryDto.setCreatedBy(country.getCreatedBy());
        countryDto.setDateCreated(DateTimeFormatUtil.format(country.getDateCreated()));
        countryDto.setUpdatedBy(country.getUpdatedBy());
        countryDto.setLastUpdated(DateTimeFormatUtil.format(country.getLastUpdated()));

        return countryDto;
    }

    private DroplistDto toDroplistDto(Country country) {

        DroplistDto droplistDto = new DroplistDto();
        droplistDto.setId(country.getCode());
        droplistDto.setValue(country.getName());

        return droplistDto;
    }

    private Country toCountry(CountryDto countryDto) {

        Country country = new Country();
        country.setCode(countryDto.getCode());
        country.setName(countryDto.getName());
        country.setDescription(countryDto.getDescription());
        country.setInactiveFlag(countryDto.isInactiveFlag());

        return country;
    }
}