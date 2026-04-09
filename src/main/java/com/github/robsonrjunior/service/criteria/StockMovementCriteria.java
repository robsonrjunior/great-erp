package com.github.robsonrjunior.service.criteria;

import com.github.robsonrjunior.domain.enumeration.MovementType;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.github.robsonrjunior.domain.StockMovement} entity. This class is used
 * in {@link com.github.robsonrjunior.web.rest.StockMovementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stock-movements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockMovementCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MovementType
     */
    public static class MovementTypeFilter extends Filter<MovementType> {

        public MovementTypeFilter() {}

        public MovementTypeFilter(MovementTypeFilter filter) {
            super(filter);
        }

        @Override
        public MovementTypeFilter copy() {
            return new MovementTypeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter movementDate;

    private MovementTypeFilter movementType;

    private BigDecimalFilter quantity;

    private BigDecimalFilter unitCost;

    private StringFilter referenceNumber;

    private StringFilter notes;

    private InstantFilter deletedAt;

    private LongFilter tenantId;

    private LongFilter warehouseId;

    private LongFilter productId;

    private LongFilter rawMaterialId;

    private Boolean distinct;

    public StockMovementCriteria() {}

    public StockMovementCriteria(StockMovementCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.movementDate = other.optionalMovementDate().map(InstantFilter::copy).orElse(null);
        this.movementType = other.optionalMovementType().map(MovementTypeFilter::copy).orElse(null);
        this.quantity = other.optionalQuantity().map(BigDecimalFilter::copy).orElse(null);
        this.unitCost = other.optionalUnitCost().map(BigDecimalFilter::copy).orElse(null);
        this.referenceNumber = other.optionalReferenceNumber().map(StringFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.warehouseId = other.optionalWarehouseId().map(LongFilter::copy).orElse(null);
        this.productId = other.optionalProductId().map(LongFilter::copy).orElse(null);
        this.rawMaterialId = other.optionalRawMaterialId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public StockMovementCriteria copy() {
        return new StockMovementCriteria(this);
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

    public InstantFilter getMovementDate() {
        return movementDate;
    }

    public Optional<InstantFilter> optionalMovementDate() {
        return Optional.ofNullable(movementDate);
    }

    public InstantFilter movementDate() {
        if (movementDate == null) {
            setMovementDate(new InstantFilter());
        }
        return movementDate;
    }

    public void setMovementDate(InstantFilter movementDate) {
        this.movementDate = movementDate;
    }

    public MovementTypeFilter getMovementType() {
        return movementType;
    }

    public Optional<MovementTypeFilter> optionalMovementType() {
        return Optional.ofNullable(movementType);
    }

    public MovementTypeFilter movementType() {
        if (movementType == null) {
            setMovementType(new MovementTypeFilter());
        }
        return movementType;
    }

    public void setMovementType(MovementTypeFilter movementType) {
        this.movementType = movementType;
    }

    public BigDecimalFilter getQuantity() {
        return quantity;
    }

    public Optional<BigDecimalFilter> optionalQuantity() {
        return Optional.ofNullable(quantity);
    }

    public BigDecimalFilter quantity() {
        if (quantity == null) {
            setQuantity(new BigDecimalFilter());
        }
        return quantity;
    }

    public void setQuantity(BigDecimalFilter quantity) {
        this.quantity = quantity;
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

    public StringFilter getReferenceNumber() {
        return referenceNumber;
    }

    public Optional<StringFilter> optionalReferenceNumber() {
        return Optional.ofNullable(referenceNumber);
    }

    public StringFilter referenceNumber() {
        if (referenceNumber == null) {
            setReferenceNumber(new StringFilter());
        }
        return referenceNumber;
    }

    public void setReferenceNumber(StringFilter referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public Optional<StringFilter> optionalNotes() {
        return Optional.ofNullable(notes);
    }

    public StringFilter notes() {
        if (notes == null) {
            setNotes(new StringFilter());
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
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

    public LongFilter getWarehouseId() {
        return warehouseId;
    }

    public Optional<LongFilter> optionalWarehouseId() {
        return Optional.ofNullable(warehouseId);
    }

    public LongFilter warehouseId() {
        if (warehouseId == null) {
            setWarehouseId(new LongFilter());
        }
        return warehouseId;
    }

    public void setWarehouseId(LongFilter warehouseId) {
        this.warehouseId = warehouseId;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public Optional<LongFilter> optionalProductId() {
        return Optional.ofNullable(productId);
    }

    public LongFilter productId() {
        if (productId == null) {
            setProductId(new LongFilter());
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    public LongFilter getRawMaterialId() {
        return rawMaterialId;
    }

    public Optional<LongFilter> optionalRawMaterialId() {
        return Optional.ofNullable(rawMaterialId);
    }

    public LongFilter rawMaterialId() {
        if (rawMaterialId == null) {
            setRawMaterialId(new LongFilter());
        }
        return rawMaterialId;
    }

    public void setRawMaterialId(LongFilter rawMaterialId) {
        this.rawMaterialId = rawMaterialId;
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
        final StockMovementCriteria that = (StockMovementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(movementDate, that.movementDate) &&
            Objects.equals(movementType, that.movementType) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(unitCost, that.unitCost) &&
            Objects.equals(referenceNumber, that.referenceNumber) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(warehouseId, that.warehouseId) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(rawMaterialId, that.rawMaterialId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            movementDate,
            movementType,
            quantity,
            unitCost,
            referenceNumber,
            notes,
            deletedAt,
            tenantId,
            warehouseId,
            productId,
            rawMaterialId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockMovementCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMovementDate().map(f -> "movementDate=" + f + ", ").orElse("") +
            optionalMovementType().map(f -> "movementType=" + f + ", ").orElse("") +
            optionalQuantity().map(f -> "quantity=" + f + ", ").orElse("") +
            optionalUnitCost().map(f -> "unitCost=" + f + ", ").orElse("") +
            optionalReferenceNumber().map(f -> "referenceNumber=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalWarehouseId().map(f -> "warehouseId=" + f + ", ").orElse("") +
            optionalProductId().map(f -> "productId=" + f + ", ").orElse("") +
            optionalRawMaterialId().map(f -> "rawMaterialId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
