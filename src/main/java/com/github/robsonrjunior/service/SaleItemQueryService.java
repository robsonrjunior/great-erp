package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.SaleItem;
import com.github.robsonrjunior.repository.SaleItemRepository;
import com.github.robsonrjunior.repository.search.SaleItemSearchRepository;
import com.github.robsonrjunior.service.criteria.SaleItemCriteria;
import com.github.robsonrjunior.service.dto.SaleItemDTO;
import com.github.robsonrjunior.service.mapper.SaleItemMapper;
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
 * Service for executing complex queries for {@link SaleItem} entities in the database.
 * The main input is a {@link SaleItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SaleItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SaleItemQueryService extends QueryService<SaleItem> {

    private static final Logger LOG = LoggerFactory.getLogger(SaleItemQueryService.class);

    private final SaleItemRepository saleItemRepository;

    private final SaleItemMapper saleItemMapper;

    private final SaleItemSearchRepository saleItemSearchRepository;

    public SaleItemQueryService(
        SaleItemRepository saleItemRepository,
        SaleItemMapper saleItemMapper,
        SaleItemSearchRepository saleItemSearchRepository
    ) {
        this.saleItemRepository = saleItemRepository;
        this.saleItemMapper = saleItemMapper;
        this.saleItemSearchRepository = saleItemSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link SaleItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SaleItemDTO> findByCriteria(SaleItemCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SaleItem> specification = createSpecification(criteria);
        return saleItemRepository.findAll(specification, page).map(saleItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SaleItemCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SaleItem> specification = createSpecification(criteria);
        return saleItemRepository.count(specification);
    }

    /**
     * Function to convert {@link SaleItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SaleItem> createSpecification(SaleItemCriteria criteria) {
        Specification<SaleItem> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), SaleItem_.id),
                buildRangeSpecification(criteria.getQuantity(), SaleItem_.quantity),
                buildRangeSpecification(criteria.getUnitPrice(), SaleItem_.unitPrice),
                buildRangeSpecification(criteria.getDiscountAmount(), SaleItem_.discountAmount),
                buildRangeSpecification(criteria.getLineTotal(), SaleItem_.lineTotal),
                buildRangeSpecification(criteria.getDeletedAt(), SaleItem_.deletedAt),
                buildSpecification(criteria.getTenantId(), root -> root.join(SaleItem_.tenants, JoinType.LEFT).get(Tenant_.id)),
                buildSpecification(criteria.getSaleId(), root -> root.join(SaleItem_.sales, JoinType.LEFT).get(Sale_.id)),
                buildSpecification(criteria.getProductId(), root -> root.join(SaleItem_.products, JoinType.LEFT).get(Product_.id))
            );
        }
        return specification;
    }
}
