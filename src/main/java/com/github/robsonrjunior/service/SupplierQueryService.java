package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.Supplier;
import com.github.robsonrjunior.repository.SupplierRepository;
import com.github.robsonrjunior.repository.search.SupplierSearchRepository;
import com.github.robsonrjunior.service.criteria.SupplierCriteria;
import com.github.robsonrjunior.service.dto.SupplierDTO;
import com.github.robsonrjunior.service.mapper.SupplierMapper;
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
 * Service for executing complex queries for {@link Supplier} entities in the database.
 * The main input is a {@link SupplierCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SupplierDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SupplierQueryService extends QueryService<Supplier> {

    private static final Logger LOG = LoggerFactory.getLogger(SupplierQueryService.class);

    private final SupplierRepository supplierRepository;

    private final SupplierMapper supplierMapper;

    private final SupplierSearchRepository supplierSearchRepository;

    public SupplierQueryService(
        SupplierRepository supplierRepository,
        SupplierMapper supplierMapper,
        SupplierSearchRepository supplierSearchRepository
    ) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
        this.supplierSearchRepository = supplierSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link SupplierDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SupplierDTO> findByCriteria(SupplierCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Supplier> specification = createSpecification(criteria);
        return supplierRepository.findAll(specification, page).map(supplierMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SupplierCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Supplier> specification = createSpecification(criteria);
        return supplierRepository.count(specification);
    }

    /**
     * Function to convert {@link SupplierCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Supplier> createSpecification(SupplierCriteria criteria) {
        Specification<Supplier> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Supplier_.id),
                buildStringSpecification(criteria.getLegalName(), Supplier_.legalName),
                buildStringSpecification(criteria.getTradeName(), Supplier_.tradeName),
                buildStringSpecification(criteria.getTaxId(), Supplier_.taxId),
                buildSpecification(criteria.getPartyType(), Supplier_.partyType),
                buildStringSpecification(criteria.getEmail(), Supplier_.email),
                buildStringSpecification(criteria.getPhone(), Supplier_.phone),
                buildSpecification(criteria.getActive(), Supplier_.active),
                buildRangeSpecification(criteria.getDeletedAt(), Supplier_.deletedAt),
                buildSpecification(criteria.getPersonId(), root -> root.join(Supplier_.person, JoinType.LEFT).get(Person_.id)),
                buildSpecification(criteria.getCompanyId(), root -> root.join(Supplier_.company, JoinType.LEFT).get(Company_.id)),
                buildSpecification(criteria.getRawMaterialsId(), root ->
                    root.join(Supplier_.rawMaterials, JoinType.LEFT).get(RawMaterial_.id)
                ),
                buildSpecification(criteria.getTenantId(), root -> root.join(Supplier_.tenants, JoinType.LEFT).get(Tenant_.id)),
                buildSpecification(criteria.getCityId(), root -> root.join(Supplier_.cities, JoinType.LEFT).get(City_.id))
            );
        }
        return specification;
    }
}
