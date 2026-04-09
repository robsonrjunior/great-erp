package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.Company;
import com.github.robsonrjunior.repository.CompanyRepository;
import com.github.robsonrjunior.repository.search.CompanySearchRepository;
import com.github.robsonrjunior.service.criteria.CompanyCriteria;
import com.github.robsonrjunior.service.dto.CompanyDTO;
import com.github.robsonrjunior.service.mapper.CompanyMapper;
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
 * Service for executing complex queries for {@link Company} entities in the database.
 * The main input is a {@link CompanyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CompanyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CompanyQueryService extends QueryService<Company> {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyQueryService.class);

    private final CompanyRepository companyRepository;

    private final CompanyMapper companyMapper;

    private final CompanySearchRepository companySearchRepository;

    public CompanyQueryService(
        CompanyRepository companyRepository,
        CompanyMapper companyMapper,
        CompanySearchRepository companySearchRepository
    ) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.companySearchRepository = companySearchRepository;
    }

    /**
     * Return a {@link Page} of {@link CompanyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CompanyDTO> findByCriteria(CompanyCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Company> specification = createSpecification(criteria);
        return companyRepository.findAll(specification, page).map(companyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CompanyCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Company> specification = createSpecification(criteria);
        return companyRepository.count(specification);
    }

    /**
     * Function to convert {@link CompanyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Company> createSpecification(CompanyCriteria criteria) {
        Specification<Company> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Company_.id),
                buildStringSpecification(criteria.getLegalName(), Company_.legalName),
                buildStringSpecification(criteria.getTradeName(), Company_.tradeName),
                buildStringSpecification(criteria.getCnpj(), Company_.cnpj),
                buildStringSpecification(criteria.getStateRegistration(), Company_.stateRegistration),
                buildStringSpecification(criteria.getEmail(), Company_.email),
                buildStringSpecification(criteria.getPhone(), Company_.phone),
                buildSpecification(criteria.getActive(), Company_.active),
                buildRangeSpecification(criteria.getDeletedAt(), Company_.deletedAt),
                buildSpecification(criteria.getCustomerId(), root -> root.join(Company_.customer, JoinType.LEFT).get(Customer_.id)),
                buildSpecification(criteria.getSupplierId(), root -> root.join(Company_.supplier, JoinType.LEFT).get(Supplier_.id)),
                buildSpecification(criteria.getTenantId(), root -> root.join(Company_.tenants, JoinType.LEFT).get(Tenant_.id)),
                buildSpecification(criteria.getCityId(), root -> root.join(Company_.cities, JoinType.LEFT).get(City_.id))
            );
        }
        return specification;
    }
}
