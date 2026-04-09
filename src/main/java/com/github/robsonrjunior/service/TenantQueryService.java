package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.Tenant;
import com.github.robsonrjunior.repository.TenantRepository;
import com.github.robsonrjunior.repository.search.TenantSearchRepository;
import com.github.robsonrjunior.service.criteria.TenantCriteria;
import com.github.robsonrjunior.service.dto.TenantDTO;
import com.github.robsonrjunior.service.mapper.TenantMapper;
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
 * Service for executing complex queries for {@link Tenant} entities in the database.
 * The main input is a {@link TenantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TenantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TenantQueryService extends QueryService<Tenant> {

    private static final Logger LOG = LoggerFactory.getLogger(TenantQueryService.class);

    private final TenantRepository tenantRepository;

    private final TenantMapper tenantMapper;

    private final TenantSearchRepository tenantSearchRepository;

    public TenantQueryService(TenantRepository tenantRepository, TenantMapper tenantMapper, TenantSearchRepository tenantSearchRepository) {
        this.tenantRepository = tenantRepository;
        this.tenantMapper = tenantMapper;
        this.tenantSearchRepository = tenantSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link TenantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TenantDTO> findByCriteria(TenantCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Tenant> specification = createSpecification(criteria);
        return tenantRepository.findAll(specification, page).map(tenantMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TenantCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Tenant> specification = createSpecification(criteria);
        return tenantRepository.count(specification);
    }

    /**
     * Function to convert {@link TenantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Tenant> createSpecification(TenantCriteria criteria) {
        Specification<Tenant> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Tenant_.id),
                buildStringSpecification(criteria.getName(), Tenant_.name),
                buildStringSpecification(criteria.getCode(), Tenant_.code),
                buildSpecification(criteria.getActive(), Tenant_.active),
                buildRangeSpecification(criteria.getDeletedAt(), Tenant_.deletedAt),
                buildSpecification(criteria.getCustomersId(), root -> root.join(Tenant_.customers, JoinType.LEFT).get(Customer_.id)),
                buildSpecification(criteria.getSuppliersId(), root -> root.join(Tenant_.suppliers, JoinType.LEFT).get(Supplier_.id)),
                buildSpecification(criteria.getPeopleId(), root -> root.join(Tenant_.people, JoinType.LEFT).get(Person_.id)),
                buildSpecification(criteria.getCompaniesId(), root -> root.join(Tenant_.companies, JoinType.LEFT).get(Company_.id)),
                buildSpecification(criteria.getProductsId(), root -> root.join(Tenant_.products, JoinType.LEFT).get(Product_.id)),
                buildSpecification(criteria.getRawMaterialsId(), root ->
                    root.join(Tenant_.rawMaterials, JoinType.LEFT).get(RawMaterial_.id)
                ),
                buildSpecification(criteria.getWarehousesId(), root -> root.join(Tenant_.warehouses, JoinType.LEFT).get(Warehouse_.id)),
                buildSpecification(criteria.getSalesId(), root -> root.join(Tenant_.sales, JoinType.LEFT).get(Sale_.id)),
                buildSpecification(criteria.getSaleItemsId(), root -> root.join(Tenant_.saleItems, JoinType.LEFT).get(SaleItem_.id)),
                buildSpecification(criteria.getStockMovementsId(), root ->
                    root.join(Tenant_.stockMovements, JoinType.LEFT).get(StockMovement_.id)
                )
            );
        }
        return specification;
    }
}
