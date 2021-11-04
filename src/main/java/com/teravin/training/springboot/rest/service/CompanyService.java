package com.teravin.training.springboot.rest.service;

import com.teravin.training.springboot.rest.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CompanyService {

    Optional<Company> find(String code);

    List<Company> findDroplist();

    Page<Company> findSearch(String code, String name, Pageable pageable);

    void save(Company company);

    void update(Company company);

    void delete(String code);
}
