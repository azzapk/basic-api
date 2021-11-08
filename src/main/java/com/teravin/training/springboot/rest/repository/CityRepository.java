package com.teravin.training.springboot.rest.repository;

import com.teravin.training.springboot.rest.domain.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends CrudRepository<City, String> {

    Optional<City> findByCodeAndDeleteCityFalse(String code);

    List<City> findByInactiveCityFalseAndDeleteCityFalseOrderByIndexAsc();

    Page<City> findByCodeContainingAndNameContainingAndDeleteCityFalseOrderByCode(String code, String name, Pageable pageable);
}
