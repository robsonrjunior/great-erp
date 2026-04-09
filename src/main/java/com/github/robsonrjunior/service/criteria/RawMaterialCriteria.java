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
 * Criteria class for the {@link com.github.robsonrjunior.domain.RawMaterial} entity. This class is used
 * in {@link com.github.robsonrjunior.web.rest.RawMaterialResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /raw-materials?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RawMaterialCriteria implements Serializable, Criteria {

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

    private BigDecimalFilter unitCost;

    private BigDecimalFilter minStock;

    private BooleanFilter active;

    private InstantFilter deletedAt;

    private LongFilter stockMovementsId;

    private LongFilter tenantId;

    private LongFilter primarySupplierId;

    private Boolean distinct;

    public RawMaterialCriteria() {}

    public RawMaterialCriteria(RawMaterialCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.sku = other.optionalSku().map(StringFilter::copy).orElse(null);
        this.unitOfMeasure = other.optionalUnitOfMeasure().map(UnitOfMeasureFilter::copy).orElse(null);
        this.unitDecimalPlaces = other.optionalUnitDecimalPlaces().map(IntegerFilter::copy).orElse(null);
        this.unitCost = other.optionalUnitCost().map(BigDecimalFilter::copy).orElse(null);
        this.minStock = other.optionalMinStock().map(BigDecimalFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.stockMovementsId = other.optionalStockMovementsId().map(LongFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.primarySupplierId = other.optionalPrimarySupplierId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RawMaterialCriteria copy() {
        return new RawMaterialCriteria(this);
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

    public BigDecimalFilter getUnitCost() {
        return unitCost;
    }

    public Optional<BigDecimalFilter> optionalUnitCost() {
        return Optional.ofNullable(unitCost);
    }

    public BigDecimalFilter unitCost() {
        if (unitCost == null) {
            setUnitCost(new BigDecimalFilter());
        }
        return unitCost;
    }

    public void setUnitCost(BigDecimalFilter unitCost) {
        this.unitCost = unitCost;
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

    public LongFilter getPrimarySupplierId() {
        return primarySupplierId;
    }

    public Optional<LongFilter> optionalPrimarySupplierId() {
        return Optional.ofNullable(primarySupplierId);
    }

    public LongFilter primarySupplierId() {
        if (primarySupplierId == null) {
            setPrimarySupplierId(new LongFilter());
        }
        return primarySupplierId;
    }

    public void setPrimarySupplierId(LongFilter primarySupplierId) {
        this.primarySupplierId = primarySupplierId;
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
        final RawMaterialCriteria that = (RawMaterialCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(sku, that.sku) &&
            Objects.equals(unitOfMeasure, that.unitOfMeasure) &&
            Objects.equals(unitDecimalPlaces, that.unitDecimalPlaces) &&
            Objects.equals(unitCost, that.unitCost) &&
            Objects.equals(minStock, that.minStock) &&
            Objects.equals(active, that.active) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(stockMovementsId, that.stockMovementsId) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(primarySupplierId, that.primarySupplierId) &&
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
            unitCost,
            minStock,
            active,
            deletedAt,
            stockMovementsId,
            tenantId,
            primarySupplierId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RawMaterialCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalSku().map(f -> "sku=" + f + ", ").orElse("") +
            optionalUnitOfMeasure().map(f -> "unitOfMeasure=" + f + ", ").orElse("") +
            optionalUnitDecimalPlaces().map(f -> "unitDecimalPlaces=" + f + ", ").orElse("") +
            optionalUnitCost().map(f -> "unitCost=" + f + ", ").orElse("") +
            optionalMinStock().map(f -> "minStock=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalStockMovementsId().map(f -> "stockMovementsId=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalPrimarySupplierId().map(f -> "primarySupplierId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
