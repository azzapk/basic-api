package com.teravin.training.springboot.rest.repository;

import com.teravin.training.springboot.rest.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends CrudRepository<Company, String> {

    Optional<Company> findByCodeAndDeleteCompanyFalse(String code);

    List<Company> findByInactiveCompanyFalseAndDeleteCompanyFalseOrderByIndexAsc();

    Page<Company> findByCodeContainingAndNameContainingAndDeleteCompanyFalseOrderByCode(String code, String name, Pageable pageable);
}
