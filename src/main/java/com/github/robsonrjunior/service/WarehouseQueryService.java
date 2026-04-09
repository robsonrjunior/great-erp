package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.Warehouse;
import com.github.robsonrjunior.repository.WarehouseRepository;
import com.github.robsonrjunior.repository.search.WarehouseSearchRepository;
import com.github.robsonrjunior.service.criteria.WarehouseCriteria;
import com.github.robsonrjunior.service.dto.WarehouseDTO;
import com.github.robsonrjunior.service.mapper.WarehouseMapper;
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
 * Service for executing complex queries for {@link Warehouse} entities in the database.
 * The main input is a {@link WarehouseCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link WarehouseDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WarehouseQueryService extends QueryService<Warehouse> {

    private static final Logger LOG = LoggerFactory.getLogger(WarehouseQueryService.class);

    private final WarehouseRepository warehouseRepository;

    private final WarehouseMapper warehouseMapper;

    private final WarehouseSearchRepository warehouseSearchRepository;

    public WarehouseQueryService(
        WarehouseRepository warehouseRepository,
        WarehouseMapper warehouseMapper,
        WarehouseSearchRepository warehouseSearchRepository
    ) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
        this.warehouseSearchRepository = warehouseSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link WarehouseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WarehouseDTO> findByCriteria(WarehouseCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Warehouse> specification = createSpecification(criteria);
        return warehouseRepository.findAll(specification, page).map(warehouseMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WarehouseCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Warehouse> specification = createSpecification(criteria);
        return warehouseRepository.count(specification);
    }

    /**
     * Function to convert {@link WarehouseCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Warehouse> createSpecification(WarehouseCriteria criteria) {
        Specification<Warehouse> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Warehouse_.id),
                buildStringSpecification(criteria.getName(), Warehouse_.name),
                buildStringSpecification(criteria.getCode(), Warehouse_.code),
                buildSpecification(criteria.getActive(), Warehouse_.active),
                buildRangeSpecification(criteria.getDeletedAt(), Warehouse_.deletedAt),
                buildSpecification(criteria.getStockMovementsId(), root ->
                    root.join(Warehouse_.stockMovements, JoinType.LEFT).get(StockMovement_.id)
                ),
                buildSpecification(criteria.getSalesId(), root -> root.join(Warehouse_.sales, JoinType.LEFT).get(Sale_.id)),
                buildSpecification(criteria.getTenantId(), root -> root.join(Warehouse_.tenants, JoinType.LEFT).get(Tenant_.id)),
                buildSpecification(criteria.getCityId(), root -> root.join(Warehouse_.cities, JoinType.LEFT).get(City_.id))
            );
        }
        return specification;
    }
}
