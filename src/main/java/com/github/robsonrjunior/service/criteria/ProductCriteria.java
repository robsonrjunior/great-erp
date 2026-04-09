package com.github.robsonrjunior.service.criteria;

import com.github.robsonrjunior.domain.enumeration.UnitOfMeasure;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.github.robsonrjunior.domain.Product} entity. This class is used
 * in {@link com.github.robsonrjunior.web.rest.ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductCriteria implements Serializable, Criteria {

    /**
     * Class for filtering UnitOfMeasure
     */
    public static class UnitOfMeasureFilter extends Filter<UnitOfMeasure> {

        public UnitOfMeasureFilter() {}

        public UnitOfMeasureFilter(UnitOfMeasureFilter filter) {
            super(filter);
        }

        @Override
        public UnitOfMeasureFilter copy() {
            return new UnitOfMeasureFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter sku;

    private UnitOfMeasureFilter unitOfMeasure;

    private IntegerFilter unitDecimalPlaces;

    private BigDecimalFilter salePrice;

    private BigDecimalFilter costPrice;

    private BigDecimalFilter minStock;

    private BooleanFilter active;

    private InstantFilter deletedAt;

    private LongFilter saleItemsId;

    private LongFilter stockMovementsId;

    private LongFilter tenantId;

    private Boolean distinct;

    public ProductCriteria() {}

    public ProductCriteria(ProductCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.sku = other.optionalSku().map(StringFilter::copy).orElse(null);
        this.unitOfMeasure = other.optionalUnitOfMeasure().map(UnitOfMeasureFilter::copy).orElse(null);
        this.unitDecimalPlaces = other.optionalUnitDecimalPlaces().map(IntegerFilter::copy).orElse(null);
        this.salePrice = other.optionalSalePrice().map(BigDecimalFilter::copy).orElse(null);
        this.costPrice = other.optionalCostPrice().map(BigDecimalFilter::copy).orElse(null);
        this.minStock = other.optionalMinStock().map(BigDecimalFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.saleItemsId = other.optionalSaleItemsId().map(LongFilter::copy).orElse(null);
        this.stockMovementsId = other.optionalStockMovementsId().map(LongFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProductCriteria copy() {
        return new ProductCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getSku() {
        return sku;
    }

    public Optional<StringFilter> optionalSku() {
        return Optional.ofNullable(sku);
    }

    public StringFilter sku() {
        if (sku == null) {
            setSku(new StringFilter());
        }
        return sku;
    }

    public void setSku(StringFilter sku) {
        this.sku = sku;
    }

    public UnitOfMeasureFilter getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Optional<UnitOfMeasureFilter> optionalUnitOfMeasure() {
        return Optional.ofNullable(unitOfMeasure);
    }

    public UnitOfMeasureFilter unitOfMeasure() {
        if (unitOfMeasure == null) {
            setUnitOfMeasure(new UnitOfMeasureFilter());
        }
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasureFilter unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public IntegerFilter getUnitDecimalPlaces() {
        return unitDecimalPlaces;
    }

    public Optional<IntegerFilter> optionalUnitDecimalPlaces() {
        return Optional.ofNullable(unitDecimalPlaces);
    }

    public IntegerFilter unitDecimalPlaces() {
        if (unitDecimalPlaces == null) {
            setUnitDecimalPlaces(new IntegerFilter());
        }
        return unitDecimalPlaces;
    }

    public void setUnitDecimalPlaces(IntegerFilter unitDecimalPlaces) {
        this.unitDecimalPlaces = unitDecimalPlaces;
    }

    public BigDecimalFilter getSalePrice() {
        return salePrice;
    }

    public Optional<BigDecimalFilter> optionalSalePrice() {
        return Optional.ofNullable(salePrice);
    }

    public BigDecimalFilter salePrice() {
        if (salePrice == null) {
            setSalePrice(new BigDecimalFilter());
        }
        return salePrice;
    }

    public void setSalePrice(BigDecimalFilter salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimalFilter getCostPrice() {
        return costPrice;
    }

    public Optional<BigDecimalFilter> optionalCostPrice() {
        return Optional.ofNullable(costPrice);
    }

    public BigDecimalFilter costPrice() {
        if (costPrice == null) {
            setCostPrice(new BigDecimalFilter());
        }
        return costPrice;
    }

    public void setCostPrice(BigDecimalFilter costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimalFilter getMinStock() {
        return minStock;
    }

    public Optional<BigDecimalFilter> optionalMinStock() {
        return Optional.ofNullable(minStock);
    }

    public BigDecimalFilter minStock() {
        if (minStock == null) {
            setMinStock(new BigDecimalFilter());
        }
        return minStock;
    }

    public void setMinStock(BigDecimalFilter minStock) {
        this.minStock = minStock;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public Optional<BooleanFilter> optionalActive() {
        return Optional.ofNullable(active);
    }

    public BooleanFilter active() {
        if (active == null) {
            setActive(new BooleanFilter());
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public InstantFilter getDeletedAt() {
        return deletedAt;
    }

    public Optional<InstantFilter> optionalDeletedAt() {
        return Optional.ofNullable(deletedAt);
    }

    public InstantFilter deletedAt() {
        if (deletedAt == null) {
            setDeletedAt(new InstantFilter());
        }
        return deletedAt;
    }

    public void setDeletedAt(InstantFilter deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LongFilter getSaleItemsId() {
        return saleItemsId;
    }

    public Optional<LongFilter> optionalSaleItemsId() {
        return Optional.ofNullable(saleItemsId);
    }

    public LongFilter saleItemsId() {
        if (saleItemsId == null) {
            setSaleItemsId(new LongFilter());
        }
        return saleItemsId;
    }

    public void setSaleItemsId(LongFilter saleItemsId) {
        this.saleItemsId = saleItemsId;
    }

    public LongFilter getStockMovementsId() {
        return stockMovementsId;
    }

    public Optional<LongFilter> optionalStockMovementsId() {
        return Optional.ofNullable(stockMovementsId);
    }

    public LongFilter stockMovementsId() {
        if (stockMovementsId == null) {
            setStockMovementsId(new LongFilter());
        }
        return stockMovementsId;
    }

    public void setStockMovementsId(LongFilter stockMovementsId) {
        this.stockMovementsId = stockMovementsId;
    }

    public LongFilter getTenantId() {
        return tenantId;
    }

    public Optional<LongFilter> optionalTenantId() {
        return Optional.ofNullable(tenantId);
    }

    public LongFilter tenantId() {
        if (tenantId == null) {
            setTenantId(new LongFilter());
        }
        return tenantId;
    }

    public void setTenantId(LongFilter tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductCriteria that = (ProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(sku, that.sku) &&
            Objects.equals(unitOfMeasure, that.unitOfMeasure) &&
            Objects.equals(unitDecimalPlaces, that.unitDecimalPlaces) &&
            Objects.equals(salePrice, that.salePrice) &&
            Objects.equals(costPrice, that.costPrice) &&
            Objects.equals(minStock, that.minStock) &&
            Objects.equals(active, that.active) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(saleItemsId, that.saleItemsId) &&
            Objects.equals(stockMovementsId, that.stockMovementsId) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            sku,
            unitOfMeasure,
            unitDecimalPlaces,
            salePrice,
            costPrice,
            minStock,
            active,
            deletedAt,
            saleItemsId,
            stockMovementsId,
            tenantId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalSku().map(f -> "sku=" + f + ", ").orElse("") +
            optionalUnitOfMeasure().map(f -> "unitOfMeasure=" + f + ", ").orElse("") +
            optionalUnitDecimalPlaces().map(f -> "unitDecimalPlaces=" + f + ", ").orElse("") +
            optionalSalePrice().map(f -> "salePrice=" + f + ", ").orElse("") +
            optionalCostPrice().map(f -> "costPrice=" + f + ", ").orElse("") +
            optionalMinStock().map(f -> "minStock=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalSaleItemsId().map(f -> "saleItemsId=" + f + ", ").orElse("") +
            optionalStockMovementsId().map(f -> "stockMovementsId=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
