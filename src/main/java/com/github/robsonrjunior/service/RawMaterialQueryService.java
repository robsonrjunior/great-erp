package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.RawMaterial;
import com.github.robsonrjunior.repository.RawMaterialRepository;
import com.github.robsonrjunior.repository.search.RawMaterialSearchRepository;
import com.github.robsonrjunior.service.criteria.RawMaterialCriteria;
import com.github.robsonrjunior.service.dto.RawMaterialDTO;
import com.github.robsonrjunior.service.mapper.RawMaterialMapper;
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
 * Service for executing complex queries for {@link RawMaterial} entities in the database.
 * The main input is a {@link RawMaterialCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RawMaterialDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RawMaterialQueryService extends QueryService<RawMaterial> {

    private static final Logger LOG = LoggerFactory.getLogger(RawMaterialQueryService.class);

    private final RawMaterialRepository rawMaterialRepository;

    private final RawMaterialMapper rawMaterialMapper;

    private final RawMaterialSearchRepository rawMaterialSearchRepository;

    public RawMaterialQueryService(
        RawMaterialRepository rawMaterialRepository,
        RawMaterialMapper rawMaterialMapper,
        RawMaterialSearchRepository rawMaterialSearchRepository
    ) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.rawMaterialMapper = rawMaterialMapper;
        this.rawMaterialSearchRepository = rawMaterialSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link RawMaterialDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RawMaterialDTO> findByCriteria(RawMaterialCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RawMaterial> specification = createSpecification(criteria);
        return rawMaterialRepository.findAll(specification, page).map(rawMaterialMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RawMaterialCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<RawMaterial> specification = createSpecification(criteria);
        return rawMaterialRepository.count(specification);
    }

    /**
     * Function to convert {@link RawMaterialCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RawMaterial> createSpecification(RawMaterialCriteria criteria) {
        Specification<RawMaterial> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), RawMaterial_.id),
                buildStringSpecification(criteria.getName(), RawMaterial_.name),
                buildStringSpecification(criteria.getSku(), RawMaterial_.sku),
                buildSpecification(criteria.getUnitOfMeasure(), RawMaterial_.unitOfMeasure),
                buildRangeSpecification(criteria.getUnitDecimalPlaces(), RawMaterial_.unitDecimalPlaces),
                buildRangeSpecification(criteria.getUnitCost(), RawMaterial_.unitCost),
                buildRangeSpecification(criteria.getMinStock(), RawMaterial_.minStock),
                buildSpecification(criteria.getActive(), RawMaterial_.active),
                buildRangeSpecification(criteria.getDeletedAt(), RawMaterial_.deletedAt),
                buildSpecification(criteria.getStockMovementsId(), root ->
                    root.join(RawMaterial_.stockMovements, JoinType.LEFT).get(StockMovement_.id)
                ),
                buildSpecification(criteria.getTenantId(), root -> root.join(RawMaterial_.tenants, JoinType.LEFT).get(Tenant_.id)),
                buildSpecification(criteria.getPrimarySupplierId(), root ->
                    root.join(RawMaterial_.primarySuppliers, JoinType.LEFT).get(Supplier_.id)
                )
            );
        }
        return specification;
    }
}
