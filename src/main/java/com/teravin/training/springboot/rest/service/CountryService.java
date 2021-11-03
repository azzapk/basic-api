package com.teravin.training.springboot.rest.service;

import com.teravin.training.springboot.rest.domain.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CountryService {

    Optional<Country> find(String code);

    List<Country> findDroplist();

    Page<Country> findSearch(String code, String name, Pageable pageable);

    void save(Country country);

    void update(Country country);

    void delete(String code);
}