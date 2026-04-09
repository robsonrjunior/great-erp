package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.State;
import com.github.robsonrjunior.repository.StateRepository;
import com.github.robsonrjunior.repository.search.StateSearchRepository;
import com.github.robsonrjunior.service.criteria.StateCriteria;
import com.github.robsonrjunior.service.dto.StateDTO;
import com.github.robsonrjunior.service.mapper.StateMapper;
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
 * Service for executing complex queries for {@link State} entities in the database.
 * The main input is a {@link StateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link StateDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StateQueryService extends QueryService<State> {

    private static final Logger LOG = LoggerFactory.getLogger(StateQueryService.class);

    private final StateRepository stateRepository;

    private final StateMapper stateMapper;

    private final StateSearchRepository stateSearchRepository;

    public StateQueryService(StateRepository stateRepository, StateMapper stateMapper, StateSearchRepository stateSearchRepository) {
        this.stateRepository = stateRepository;
        this.stateMapper = stateMapper;
        this.stateSearchRepository = stateSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link StateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StateDTO> findByCriteria(StateCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<State> specification = createSpecification(criteria);
        return stateRepository.findAll(specification, page).map(stateMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StateCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<State> specification = createSpecification(criteria);
        return stateRepository.count(specification);
    }

    /**
     * Function to convert {@link StateCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<State> createSpecification(StateCriteria criteria) {
        Specification<State> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), State_.id),
                buildStringSpecification(criteria.getName(), State_.name),
                buildStringSpecification(criteria.getCode(), State_.code),
                buildSpecification(criteria.getCitiesId(), root -> root.join(State_.citieses, JoinType.LEFT).get(City_.id)),
                buildSpecification(criteria.getCountryId(), root -> root.join(State_.country, JoinType.LEFT).get(Country_.id))
            );
        }
        return specification;
    }
}
