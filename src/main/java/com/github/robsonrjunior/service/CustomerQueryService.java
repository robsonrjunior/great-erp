package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.Customer;
import com.github.robsonrjunior.repository.CustomerRepository;
import com.github.robsonrjunior.repository.search.CustomerSearchRepository;
import com.github.robsonrjunior.service.criteria.CustomerCriteria;
import com.github.robsonrjunior.service.dto.CustomerDTO;
import com.github.robsonrjunior.service.mapper.CustomerMapper;
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
 * Service for executing complex queries for {@link Customer} entities in the database.
 * The main input is a {@link CustomerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CustomerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CustomerQueryService extends QueryService<Customer> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerQueryService.class);

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final CustomerSearchRepository customerSearchRepository;

    public CustomerQueryService(
        CustomerRepository customerRepository,
        CustomerMapper customerMapper,
        CustomerSearchRepository customerSearchRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.customerSearchRepository = customerSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link CustomerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findByCriteria(CustomerCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.findAll(specification, page).map(customerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CustomerCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.count(specification);
    }

    /**
     * Function to convert {@link CustomerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Customer> createSpecification(CustomerCriteria criteria) {
        Specification<Customer> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Customer_.id),
                buildStringSpecification(criteria.getLegalName(), Customer_.legalName),
                buildStringSpecification(criteria.getTradeName(), Customer_.tradeName),
                buildStringSpecification(criteria.getTaxId(), Customer_.taxId),
                buildSpecification(criteria.getPartyType(), Customer_.partyType),
                buildStringSpecification(criteria.getEmail(), Customer_.email),
                buildStringSpecification(criteria.getPhone(), Customer_.phone),
                buildSpecification(criteria.getActive(), Customer_.active),
                buildRangeSpecification(criteria.getDeletedAt(), Customer_.deletedAt),
                buildSpecification(criteria.getPersonId(), root -> root.join(Customer_.person, JoinType.LEFT).get(Person_.id)),
                buildSpecification(criteria.getCompanyId(), root -> root.join(Customer_.company, JoinType.LEFT).get(Company_.id)),
                buildSpecification(criteria.getSalesId(), root -> root.join(Customer_.sales, JoinType.LEFT).get(Sale_.id)),
                buildSpecification(criteria.getTenantId(), root -> root.join(Customer_.tenants, JoinType.LEFT).get(Tenant_.id)),
                buildSpecification(criteria.getCityId(), root -> root.join(Customer_.cities, JoinType.LEFT).get(City_.id))
            );
        }
        return specification;
    }
}
