package com.teravin.training.springboot.rest.service;

import com.teravin.training.springboot.rest.domain.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CityService {

    Optional<City> find(String code);

    List<City> findDroplist();

    Page<City> findSearch(String code, String name, Pageable pageable);

    void save(City city);

    void update(City city);

    void delete(String code);
}
