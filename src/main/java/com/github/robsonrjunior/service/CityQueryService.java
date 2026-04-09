package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.City;
import com.github.robsonrjunior.repository.CityRepository;
import com.github.robsonrjunior.repository.search.CitySearchRepository;
import com.github.robsonrjunior.service.criteria.CityCriteria;
import com.github.robsonrjunior.service.dto.CityDTO;
import com.github.robsonrjunior.service.mapper.CityMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link City} entities in the database.
 * The main input is a {@link CityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CityQueryService extends QueryService<City> {

    private static final Logger LOG = LoggerFactory.getLogger(CityQueryService.class);

    private final CityRepository cityRepository;

    private final CityMapper cityMapper;

    private final CitySearchRepository citySearchRepository;

    public CityQueryService(CityRepository cityRepository, CityMapper cityMapper, CitySearchRepository citySearchRepository) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
        this.citySearchRepository = citySearchRepository;
    }

    /**
     * Return a {@link Page} of {@link CityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CityDTO> findByCriteria(CityCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<City> specification = createSpecification(criteria);
        return cityRepository.findAll(specification, page).map(cityMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CityCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<City> specification = createSpecification(criteria);
        return cityRepository.count(specification);
    }

    /**
     * Function to convert {@link CityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<City> createSpecification(CityCriteria criteria) {
        Specification<City> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), City_.id),
                buildStringSpecification(criteria.getName(), City_.name),
                buildSpecification(criteria.getSuppliersId(), root -> root.join(City_.suppliers, JoinType.LEFT).get(Supplier_.id)),
                buildSpecification(criteria.getCustomersId(), root -> root.join(City_.customers, JoinType.LEFT).get(Customer_.id)),
                buildSpecification(criteria.getPeopleId(), root -> root.join(City_.people, JoinType.LEFT).get(Person_.id)),
                buildSpecification(criteria.getCompaniesId(), root -> root.join(City_.companies, JoinType.LEFT).get(Company_.id)),
                buildSpecification(criteria.getWarehousesId(), root -> root.join(City_.warehouses, JoinType.LEFT).get(Warehouse_.id)),
                buildSpecification(criteria.getStateId(), root -> root.join(City_.state, JoinType.LEFT).get(State_.id))
            );
        }
        return specification;
    }
}
