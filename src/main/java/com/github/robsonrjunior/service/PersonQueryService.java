package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.Person;
import com.github.robsonrjunior.repository.PersonRepository;
import com.github.robsonrjunior.repository.search.PersonSearchRepository;
import com.github.robsonrjunior.service.criteria.PersonCriteria;
import com.github.robsonrjunior.service.dto.PersonDTO;
import com.github.robsonrjunior.service.mapper.PersonMapper;
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
 * Service for executing complex queries for {@link Person} entities in the database.
 * The main input is a {@link PersonCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PersonDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PersonQueryService extends QueryService<Person> {

    private static final Logger LOG = LoggerFactory.getLogger(PersonQueryService.class);

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    private final PersonSearchRepository personSearchRepository;

    public PersonQueryService(PersonRepository personRepository, PersonMapper personMapper, PersonSearchRepository personSearchRepository) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.personSearchRepository = personSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link PersonDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PersonDTO> findByCriteria(PersonCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Person> specification = createSpecification(criteria);
        return personRepository.findAll(specification, page).map(personMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PersonCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Person> specification = createSpecification(criteria);
        return personRepository.count(specification);
    }

    /**
     * Function to convert {@link PersonCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Person> createSpecification(PersonCriteria criteria) {
        Specification<Person> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Person_.id),
                buildStringSpecification(criteria.getFullName(), Person_.fullName),
                buildStringSpecification(criteria.getCpf(), Person_.cpf),
                buildRangeSpecification(criteria.getBirthDate(), Person_.birthDate),
                buildStringSpecification(criteria.getEmail(), Person_.email),
                buildStringSpecification(criteria.getPhone(), Person_.phone),
                buildSpecification(criteria.getActive(), Person_.active),
                buildRangeSpecification(criteria.getDeletedAt(), Person_.deletedAt),
                buildSpecification(criteria.getCustomerId(), root -> root.join(Person_.customer, JoinType.LEFT).get(Customer_.id)),
                buildSpecification(criteria.getSupplierId(), root -> root.join(Person_.supplier, JoinType.LEFT).get(Supplier_.id)),
                buildSpecification(criteria.getTenantId(), root -> root.join(Person_.tenants, JoinType.LEFT).get(Tenant_.id)),
                buildSpecification(criteria.getCityId(), root -> root.join(Person_.cities, JoinType.LEFT).get(City_.id))
            );
        }
        return specification;
    }
}
