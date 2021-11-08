package com.teravin.training.springboot.rest.repository;

import com.teravin.training.springboot.rest.domain.CompanyCity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyCityRepository extends CrudRepository<CompanyCity, Long> {
}
