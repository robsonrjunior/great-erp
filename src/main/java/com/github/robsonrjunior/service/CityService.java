package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.City;
import com.github.robsonrjunior.repository.CityRepository;
import com.github.robsonrjunior.repository.search.CitySearchRepository;
import com.github.robsonrjunior.service.dto.CityDTO;
import com.github.robsonrjunior.service.mapper.CityMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.github.robsonrjunior.domain.City}.
 */
@Service
@Transactional
public class CityService {

    private static final Logger LOG = LoggerFactory.getLogger(CityService.class);

    private final CityRepository cityRepository;

    private final CityMapper cityMapper;

    private final CitySearchRepository citySearchRepository;

    public CityService(CityRepository cityRepository, CityMapper cityMapper, CitySearchRepository citySearchRepository) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
        this.citySearchRepository = citySearchRepository;
    }

    /**
     * Save a city.
     *
     * @param cityDTO the entity to save.
     * @return the persisted entity.
     */
    public CityDTO save(CityDTO cityDTO) {
        LOG.debug("Request to save City : {}", cityDTO);
        City city = cityMapper.toEntity(cityDTO);
        city = cityRepository.save(city);
        citySearchRepository.index(city);
        return cityMapper.toDto(city);
    }

    /**
     * Update a city.
     *
     * @param cityDTO the entity to save.
     * @return the persisted entity.
     */
    public CityDTO update(CityDTO cityDTO) {
        LOG.debug("Request to update City : {}", cityDTO);
        City city = cityMapper.toEntity(cityDTO);
        city = cityRepository.save(city);
        citySearchRepository.index(city);
        return cityMapper.toDto(city);
    }

    /**
     * Partially update a city.
     *
     * @param cityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CityDTO> partialUpdate(CityDTO cityDTO) {
        LOG.debug("Request to partially update City : {}", cityDTO);

        return cityRepository
            .findById(cityDTO.getId())
            .map(existingCity -> {
                cityMapper.partialUpdate(existingCity, cityDTO);

                return existingCity;
            })
            .map(cityRepository::save)
            .map(savedCity -> {
                citySearchRepository.index(savedCity);
                return savedCity;
            })
            .map(cityMapper::toDto);
    }

    /**
     * Get one city by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CityDTO> findOne(Long id) {
        LOG.debug("Request to get City : {}", id);
        return cityRepository.findById(id).map(cityMapper::toDto);
    }

    /**
     * Delete the city by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete City : {}", id);
        cityRepository.deleteById(id);
        citySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the city corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CityDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Cities for query {}", query);
        return citySearchRepository.search(query, pageable).map(cityMapper::toDto);
    }
}
