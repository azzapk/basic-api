package com.teravin.training.springboot.rest.repository;

import com.teravin.training.springboot.rest.domain.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends CrudRepository<Country, String> {

    Optional<Country> findByCodeAndDeleteFlagFalse(String code);

    List<Country> findByInactiveFlagFalseAndDeleteFlagFalseOrderByIndexAsc();

    Page<Country> findByCodeContainingAndNameContainingAndDeleteFlagFalseOrderByCode(String code, String name, Pageable pageable);
}