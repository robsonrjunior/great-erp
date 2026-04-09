package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.*; // for static metamodels
import com.github.robsonrjunior.domain.Product;
import com.github.robsonrjunior.repository.ProductRepository;
import com.github.robsonrjunior.repository.search.ProductSearchRepository;
import com.github.robsonrjunior.service.criteria.ProductCriteria;
import com.github.robsonrjunior.service.dto.ProductDTO;
import com.github.robsonrjunior.service.mapper.ProductMapper;
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
 * Service for executing complex queries for {@link Product} entities in the database.
 * The main input is a {@link ProductCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ProductDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductQueryService extends QueryService<Product> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductQueryService.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final ProductSearchRepository productSearchRepository;

    public ProductQueryService(
        ProductRepository productRepository,
        ProductMapper productMapper,
        ProductSearchRepository productSearchRepository
    ) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productSearchRepository = productSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link ProductDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> findByCriteria(ProductCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Product> specification = createSpecification(criteria);
        return productRepository.findAll(specification, page).map(productMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Product> specification = createSpecification(criteria);
        return productRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Product> createSpecification(ProductCriteria criteria) {
        Specification<Product> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Product_.id),
                buildStringSpecification(criteria.getName(), Product_.name),
                buildStringSpecification(criteria.getSku(), Product_.sku),
                buildSpecification(criteria.getUnitOfMeasure(), Product_.unitOfMeasure),
                buildRangeSpecification(criteria.getUnitDecimalPlaces(), Product_.unitDecimalPlaces),
                buildRangeSpecification(criteria.getSalePrice(), Product_.salePrice),
                buildRangeSpecification(criteria.getCostPrice(), Product_.costPrice),
                buildRangeSpecification(criteria.getMinStock(), Product_.minStock),
                buildSpecification(criteria.getActive(), Product_.active),
                buildRangeSpecification(criteria.getDeletedAt(), Product_.deletedAt),
                buildSpecification(criteria.getSaleItemsId(), root -> root.join(Product_.saleItems, JoinType.LEFT).get(SaleItem_.id)),
                buildSpecification(criteria.getStockMovementsId(), root ->
                    root.join(Product_.stockMovements, JoinType.LEFT).get(StockMovement_.id)
                ),
                buildSpecification(criteria.getTenantId(), root -> root.join(Product_.tenants, JoinType.LEFT).get(Tenant_.id))
            );
        }
        return specification;
    }
}
