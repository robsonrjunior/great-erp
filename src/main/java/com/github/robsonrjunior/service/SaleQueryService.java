package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.Sale;
import com.github.robsonrjunior.repository.SaleRepository;
import com.github.robsonrjunior.repository.search.SaleSearchRepository;
import com.github.robsonrjunior.service.criteria.SaleCriteria;
import com.github.robsonrjunior.service.dto.SaleDTO;
import com.github.robsonrjunior.service.mapper.SaleMapper;
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
 * Service for executing complex queries for {@link Sale} entities in the database.
 * The main input is a {@link SaleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SaleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SaleQueryService extends QueryService<Sale> {

    private static final Logger LOG = LoggerFactory.getLogger(SaleQueryService.class);

    private final SaleRepository saleRepository;

    private final SaleMapper saleMapper;

    private final SaleSearchRepository saleSearchRepository;

    public SaleQueryService(SaleRepository saleRepository, SaleMapper saleMapper, SaleSearchRepository saleSearchRepository) {
        this.saleRepository = saleRepository;
        this.saleMapper = saleMapper;
        this.saleSearchRepository = saleSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link SaleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SaleDTO> findByCriteria(SaleCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Sale> specification = createSpecification(criteria);
        return saleRepository.findAll(specification, page).map(saleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SaleCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Sale> specification = createSpecification(criteria);
        return saleRepository.count(specification);
    }

    /**
     * Function to convert {@link SaleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Sale> createSpecification(SaleCriteria criteria) {
        Specification<Sale> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Sale_.id),
                buildRangeSpecification(criteria.getSaleDate(), Sale_.saleDate),
                buildStringSpecification(criteria.getSaleNumber(), Sale_.saleNumber),
                buildSpecification(criteria.getStatus(), Sale_.status),
                buildRangeSpecification(criteria.getGrossAmount(), Sale_.grossAmount),
                buildRangeSpecification(criteria.getDiscountAmount(), Sale_.discountAmount),
                buildRangeSpecification(criteria.getNetAmount(), Sale_.netAmount),
                buildStringSpecification(criteria.getNotes(), Sale_.notes),
                buildRangeSpecification(criteria.getDeletedAt(), Sale_.deletedAt),
                buildSpecification(criteria.getItemsId(), root -> root.join(Sale_.items, JoinType.LEFT).get(SaleItem_.id)),
                buildSpecification(criteria.getTenantId(), root -> root.join(Sale_.tenants, JoinType.LEFT).get(Tenant_.id)),
                buildSpecification(criteria.getWarehouseId(), root -> root.join(Sale_.warehouses, JoinType.LEFT).get(Warehouse_.id)),
                buildSpecification(criteria.getCustomerId(), root -> root.join(Sale_.customers, JoinType.LEFT).get(Customer_.id))
            );
        }
        return specification;
    }
}
