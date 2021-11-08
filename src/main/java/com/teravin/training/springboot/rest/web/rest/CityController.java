package com.teravin.training.springboot.rest.web.rest;

import com.teravin.base.RecordNotFoundException;
import com.teravin.base.dto.DroplistDto;
import com.teravin.base.util.SearchUtils;
import com.teravin.training.springboot.rest.domain.City;
import com.teravin.training.springboot.rest.domain.Country;
import com.teravin.training.springboot.rest.dto.CityDto;
import com.teravin.training.springboot.rest.dto.CitySearchDto;
import com.teravin.training.springboot.rest.service.CityService;
import com.teravin.training.springboot.rest.util.DateTimeFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/city")
public class CityController {

    @Autowired
    private CityService cityService;

    @GetMapping("/{code}")
    public CityDto find(@PathVariable String code) {

        return cityService.find(code)
                .map(this::toCityDto)
                .orElseThrow(() -> new RecordNotFoundException(code));
    }

    @GetMapping("")
    public Page<CityDto> findSearch(@RequestBody CitySearchDto citySearchDto) {

        return cityService.findSearch(
                        citySearchDto.getCode(),
                        citySearchDto.getName(),
                        SearchUtils.toPageable(citySearchDto))
                .map(this::toCityDto);
    }

    @GetMapping("/droplist")
    public List<DroplistDto> findDroplist() {

        return cityService.findDroplist()
                .stream()
                .map(this::toDroplistDto)
                .collect(Collectors.toList());
    }

    @PostMapping("")
    public void save(@Valid @RequestBody CityDto cityDto) {
        cityService.save(toCity(cityDto));
    }

    @PatchMapping("")
    public void update(@Valid @RequestBody CityDto cityDto) {
        cityService.update(toCity(cityDto));
    }

    @DeleteMapping("/{code}")
    public void delete(@PathVariable String code) {
        cityService.delete(code);
    }

    private CityDto toCityDto(City city) {

        CityDto cityDto = new CityDto();
        cityDto.setCode(city.getCode());
        cityDto.setName(city.getName());
        cityDto.setDescription(city.getDescription());
        cityDto.setCountryCode(city.getCountry().getCode());
        cityDto.setCountryName(city.getCountry().getName());
        cityDto.setInactiveCity(city.isInactiveCity());
        cityDto.setCreatedBy(city.getCreatedBy());
        cityDto.setDateCreated(DateTimeFormatUtil.format(city.getDateCreated()));
        cityDto.setUpdatedBy(city.getUpdatedBy());
        cityDto.setLastUpdated(DateTimeFormatUtil.format(city.getLastUpdated()));

        return cityDto;
    }

    private DroplistDto toDroplistDto(City city) {

        DroplistDto droplistDto = new DroplistDto();
        droplistDto.setId(city.getCode());
        droplistDto.setValue(city.getName());

        return droplistDto;
    }

    private City toCity(CityDto cityDto) {

        Country country = new Country();
        country.setCode(cityDto.getCountryCode());
        country.setName(cityDto.getCountryName());

        City city = new City();
        city.setCode(cityDto.getCode());
        city.setName(cityDto.getName());
        city.setDescription(cityDto.getDescription());
        city.setCountry(country);
        city.setInactiveCity(cityDto.isInactiveCity());

        return city;
    }
}
