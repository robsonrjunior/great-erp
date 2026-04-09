package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.StockMovement;
import com.github.robsonrjunior.repository.StockMovementRepository;
import com.github.robsonrjunior.repository.search.StockMovementSearchRepository;
import com.github.robsonrjunior.service.criteria.StockMovementCriteria;
import com.github.robsonrjunior.service.dto.StockMovementDTO;
import com.github.robsonrjunior.service.mapper.StockMovementMapper;
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
 * Service for executing complex queries for {@link StockMovement} entities in the database.
 * The main input is a {@link StockMovementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link StockMovementDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StockMovementQueryService extends QueryService<StockMovement> {

    private static final Logger LOG = LoggerFactory.getLogger(StockMovementQueryService.class);

    private final StockMovementRepository stockMovementRepository;

    private final StockMovementMapper stockMovementMapper;

    private final StockMovementSearchRepository stockMovementSearchRepository;

    public StockMovementQueryService(
        StockMovementRepository stockMovementRepository,
        StockMovementMapper stockMovementMapper,
        StockMovementSearchRepository stockMovementSearchRepository
    ) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockMovementMapper = stockMovementMapper;
        this.stockMovementSearchRepository = stockMovementSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link StockMovementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StockMovementDTO> findByCriteria(StockMovementCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StockMovement> specification = createSpecification(criteria);
        return stockMovementRepository.findAll(specification, page).map(stockMovementMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StockMovementCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<StockMovement> specification = createSpecification(criteria);
        return stockMovementRepository.count(specification);
    }

    /**
     * Function to convert {@link StockMovementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StockMovement> createSpecification(StockMovementCriteria criteria) {
        Specification<StockMovement> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), StockMovement_.id),
                buildRangeSpecification(criteria.getMovementDate(), StockMovement_.movementDate),
                buildSpecification(criteria.getMovementType(), StockMovement_.movementType),
                buildRangeSpecification(criteria.getQuantity(), StockMovement_.quantity),
                buildRangeSpecification(criteria.getUnitCost(), StockMovement_.unitCost),
                buildStringSpecification(criteria.getReferenceNumber(), StockMovement_.referenceNumber),
                buildStringSpecification(criteria.getNotes(), StockMovement_.notes),
                buildRangeSpecification(criteria.getDeletedAt(), StockMovement_.deletedAt),
                buildSpecification(criteria.getTenantId(), root -> root.join(StockMovement_.tenants, JoinType.LEFT).get(Tenant_.id)),
                buildSpecification(criteria.getWarehouseId(), root ->
                    root.join(StockMovement_.warehouses, JoinType.LEFT).get(Warehouse_.id)
                ),
                buildSpecification(criteria.getProductId(), root -> root.join(StockMovement_.products, JoinType.LEFT).get(Product_.id)),
                buildSpecification(criteria.getRawMaterialId(), root ->
                    root.join(StockMovement_.rawMaterials, JoinType.LEFT).get(RawMaterial_.id)
                )
            );
        }
        return specification;
    }
}
