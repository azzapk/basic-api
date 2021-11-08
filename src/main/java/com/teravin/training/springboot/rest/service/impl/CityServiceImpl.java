package com.teravin.training.springboot.rest.service.impl;

import com.teravin.base.RecordAlreadyExistException;
import com.teravin.base.RecordNotFoundException;
import com.teravin.base.context.UserContext;
import com.teravin.base.util.StringUtils;
import com.teravin.training.springboot.rest.domain.City;
import com.teravin.training.springboot.rest.repository.CityRepository;
import com.teravin.training.springboot.rest.service.CityService;
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
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public Optional<City> find(String code) {
        return cityRepository.findById(code);
    }

    @Override
    public List<City> findDroplist() {
        return cityRepository.findByInactiveCityFalseAndDeleteCityFalseOrderByIndexAsc();
    }

    @Override
    public Page<City> findSearch(String code, String name, Pageable pageable) {

        return cityRepository.findByCodeContainingAndNameContainingAndDeleteCityFalseOrderByCode(
                StringUtils.nullToEmpty(code),
                StringUtils.nullToEmpty(name),
                pageable
        );
    }

    @Override
    public void save(City city) {

        if (cityRepository.findById(city.getCode()).isPresent()){
            throw new RecordAlreadyExistException(city.getCode());
        }

        city.setDeleteCity(false);
        city.setDateCreated(OffsetDateTime.now());
        city.setCreatedBy(UserContext.getUserId());

        cityRepository.save(city);
    }

    @Override
    public void update(City city) {

        City entity = cityRepository.findByCodeAndDeleteCityFalse(city.getCode()).orElseThrow(() -> new RecordNotFoundException(city.getCode()));

        entity.setName(city.getName());
        entity.setDescription(city.getDescription());
        entity.setCountry(city.getCountry());
        entity.setInactiveCity(city.isInactiveCity());
        entity.setLastUpdated(OffsetDateTime.now());
        entity.setUpdatedBy(UserContext.getUserId());

        cityRepository.save(entity);
    }

    @Override
    public void delete(String code) {

        City city = cityRepository.findByCodeAndDeleteCityFalse(code).orElseThrow(() -> new RecordNotFoundException(code));

        city.setDeleteCity(true);
        city.setLastUpdated(OffsetDateTime.now());
        city.setUpdatedBy(UserContext.getUserId());

        cityRepository.save(city);
    }
}
