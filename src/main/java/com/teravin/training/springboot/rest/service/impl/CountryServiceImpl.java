package com.teravin.training.springboot.rest.service.impl;

import com.teravin.base.RecordAlreadyExistException;
import com.teravin.base.RecordNotFoundException;
import com.teravin.base.context.UserContext;
import com.teravin.base.util.StringUtils;
import com.teravin.training.springboot.rest.domain.Country;
import com.teravin.training.springboot.rest.repository.CountryRepository;
import com.teravin.training.springboot.rest.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CountryServiceImpl implements CountryService {

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public Optional<Country> find(String code) {
        return countryRepository.findById(code);
    }

    @Override
    public List<Country> findDroplist() {
        return countryRepository.findByInactiveFlagFalseAndDeleteFlagFalseOrderByIndexAsc();
    }

    @Override
    public Page<Country> findSearch(String code, String name, Pageable pageable) {

        return countryRepository.findByCodeContainingAndNameContainingAndDeleteFlagFalseOrderByCode(
                StringUtils.nullToEmpty(code),
                StringUtils.nullToEmpty(name),
                pageable);
    }

    @Override
    public void save(Country country) {

        if (countryRepository.findById(country.getCode()).isPresent()) {
            throw new RecordAlreadyExistException(country.getCode());
        }

        country.setDeleteFlag(false);
        country.setDateCreated(OffsetDateTime.now());
        country.setCreatedBy(UserContext.getUserId());

        countryRepository.save(country);
    }

    @Override
    public void update(Country country) {

        Country entity = countryRepository.findByCodeAndDeleteFlagFalse(country.getCode()).orElseThrow(() -> new RecordNotFoundException(country.getCode()));

        entity.setName(country.getName());
        entity.setDescription(country.getDescription());
        entity.setInactiveFlag(country.isInactiveFlag());
        entity.setLastUpdated(OffsetDateTime.now());
        entity.setUpdatedBy(UserContext.getUserId());

        countryRepository.save(entity);
    }

    @Override
    public void delete(String code) {

        Country country = countryRepository.findByCodeAndDeleteFlagFalse(code).orElseThrow(() -> new RecordNotFoundException(code));

        country.setDeleteFlag(true);
        country.setLastUpdated(OffsetDateTime.now());
        country.setUpdatedBy(UserContext.getUserId());

        countryRepository.save(country);
    }
}