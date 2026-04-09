package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.Country;
import com.github.robsonrjunior.repository.CountryRepository;
import com.github.robsonrjunior.repository.search.CountrySearchRepository;
import com.github.robsonrjunior.service.dto.CountryDTO;
import com.github.robsonrjunior.service.mapper.CountryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.github.robsonrjunior.domain.Country}.
 */
@Service
@Transactional
public class CountryService {

    private static final Logger LOG = LoggerFactory.getLogger(CountryService.class);

    private final CountryRepository countryRepository;

    private final CountryMapper countryMapper;

    private final CountrySearchRepository countrySearchRepository;

    public CountryService(
        CountryRepository countryRepository,
        CountryMapper countryMapper,
        CountrySearchRepository countrySearchRepository
    ) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        this.countrySearchRepository = countrySearchRepository;
    }

    /**
     * Save a country.
     *
     * @param countryDTO the entity to save.
     * @return the persisted entity.
     */
    public CountryDTO save(CountryDTO countryDTO) {
        LOG.debug("Request to save Country : {}", countryDTO);
        Country country = countryMapper.toEntity(countryDTO);
        country = countryRepository.save(country);
        countrySearchRepository.index(country);
        return countryMapper.toDto(country);
    }

    /**
     * Update a country.
     *
     * @param countryDTO the entity to save.
     * @return the persisted entity.
     */
    public CountryDTO update(CountryDTO countryDTO) {
        LOG.debug("Request to update Country : {}", countryDTO);
        Country country = countryMapper.toEntity(countryDTO);
        country = countryRepository.save(country);
        countrySearchRepository.index(country);
        return countryMapper.toDto(country);
    }

    /**
     * Partially update a country.
     *
     * @param countryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CountryDTO> partialUpdate(CountryDTO countryDTO) {
        LOG.debug("Request to partially update Country : {}", countryDTO);

        return countryRepository
            .findById(countryDTO.getId())
            .map(existingCountry -> {
                countryMapper.partialUpdate(existingCountry, countryDTO);

                return existingCountry;
            })
            .map(countryRepository::save)
            .map(savedCountry -> {
                countrySearchRepository.index(savedCountry);
                return savedCountry;
            })
            .map(countryMapper::toDto);
    }

    /**
     * Get one country by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CountryDTO> findOne(Long id) {
        LOG.debug("Request to get Country : {}", id);
        return countryRepository.findById(id).map(countryMapper::toDto);
    }

    /**
     * Delete the country by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Country : {}", id);
        countryRepository.deleteById(id);
        countrySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the country corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CountryDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Countries for query {}", query);
        return countrySearchRepository.search(query, pageable).map(countryMapper::toDto);
    }
}
