package com.github.robsonrjunior.service.criteria;

import com.github.robsonrjunior.domain.enumeration.SaleStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.github.robsonrjunior.domain.Sale} entity. This class is used
 * in {@link com.github.robsonrjunior.web.rest.SaleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sales?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleCriteria implements Serializable, Criteria {

    /**
     * Class for filtering SaleStatus
     */
    public static class SaleStatusFilter extends Filter<SaleStatus> {

        public SaleStatusFilter() {}

        public SaleStatusFilter(SaleStatusFilter filter) {
            super(filter);
        }

        @Override
        public SaleStatusFilter copy() {
            return new SaleStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter saleDate;

    private StringFilter saleNumber;

    private SaleStatusFilter status;

    private BigDecimalFilter grossAmount;

    private BigDecimalFilter discountAmount;

    private BigDecimalFilter netAmount;

    private StringFilter notes;

    private InstantFilter deletedAt;

    private LongFilter itemsId;

    private LongFilter tenantId;

    private LongFilter warehouseId;

    private LongFilter customerId;

    private Boolean distinct;

    public SaleCriteria() {}

    public SaleCriteria(SaleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.saleDate = other.optionalSaleDate().map(InstantFilter::copy).orElse(null);
        this.saleNumber = other.optionalSaleNumber().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(SaleStatusFilter::copy).orElse(null);
        this.grossAmount = other.optionalGrossAmount().map(BigDecimalFilter::copy).orElse(null);
        this.discountAmount = other.optionalDiscountAmount().map(BigDecimalFilter::copy).orElse(null);
        this.netAmount = other.optionalNetAmount().map(BigDecimalFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.itemsId = other.optionalItemsId().map(LongFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.warehouseId = other.optionalWarehouseId().map(LongFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SaleCriteria copy() {
        return new SaleCriteria(this);
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

    public InstantFilter getSaleDate() {
        return saleDate;
    }

    public Optional<InstantFilter> optionalSaleDate() {
        return Optional.ofNullable(saleDate);
    }

    public InstantFilter saleDate() {
        if (saleDate == null) {
            setSaleDate(new InstantFilter());
        }
        return saleDate;
    }

    public void setSaleDate(InstantFilter saleDate) {
        this.saleDate = saleDate;
    }

    public StringFilter getSaleNumber() {
        return saleNumber;
    }

    public Optional<StringFilter> optionalSaleNumber() {
        return Optional.ofNullable(saleNumber);
    }

    public StringFilter saleNumber() {
        if (saleNumber == null) {
            setSaleNumber(new StringFilter());
        }
        return saleNumber;
    }

    public void setSaleNumber(StringFilter saleNumber) {
        this.saleNumber = saleNumber;
    }

    public SaleStatusFilter getStatus() {
        return status;
    }

    public Optional<SaleStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public SaleStatusFilter status() {
        if (status == null) {
            setStatus(new SaleStatusFilter());
        }
        return status;
    }

    public void setStatus(SaleStatusFilter status) {
        this.status = status;
    }

    public BigDecimalFilter getGrossAmount() {
        return grossAmount;
    }

    public Optional<BigDecimalFilter> optionalGrossAmount() {
        return Optional.ofNullable(grossAmount);
    }

    public BigDecimalFilter grossAmount() {
        if (grossAmount == null) {
            setGrossAmount(new BigDecimalFilter());
        }
        return grossAmount;
    }

    public void setGrossAmount(BigDecimalFilter grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimalFilter getDiscountAmount() {
        return discountAmount;
    }

    public Optional<BigDecimalFilter> optionalDiscountAmount() {
        return Optional.ofNullable(discountAmount);
    }

    public BigDecimalFilter discountAmount() {
        if (discountAmount == null) {
            setDiscountAmount(new BigDecimalFilter());
        }
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimalFilter discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimalFilter getNetAmount() {
        return netAmount;
    }

    public Optional<BigDecimalFilter> optionalNetAmount() {
        return Optional.ofNullable(netAmount);
    }

    public BigDecimalFilter netAmount() {
        if (netAmount == null) {
            setNetAmount(new BigDecimalFilter());
        }
        return netAmount;
    }

    public void setNetAmount(BigDecimalFilter netAmount) {
        this.netAmount = netAmount;
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

    public LongFilter getItemsId() {
        return itemsId;
    }

    public Optional<LongFilter> optionalItemsId() {
        return Optional.ofNullable(itemsId);
    }

    public LongFilter itemsId() {
        if (itemsId == null) {
            setItemsId(new LongFilter());
        }
        return itemsId;
    }

    public void setItemsId(LongFilter itemsId) {
        this.itemsId = itemsId;
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

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
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
        final SaleCriteria that = (SaleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(saleDate, that.saleDate) &&
            Objects.equals(saleNumber, that.saleNumber) &&
            Objects.equals(status, that.status) &&
            Objects.equals(grossAmount, that.grossAmount) &&
            Objects.equals(discountAmount, that.discountAmount) &&
            Objects.equals(netAmount, that.netAmount) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(itemsId, that.itemsId) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(warehouseId, that.warehouseId) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            saleDate,
            saleNumber,
            status,
            grossAmount,
            discountAmount,
            netAmount,
            notes,
            deletedAt,
            itemsId,
            tenantId,
            warehouseId,
            customerId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSaleDate().map(f -> "saleDate=" + f + ", ").orElse("") +
            optionalSaleNumber().map(f -> "saleNumber=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalGrossAmount().map(f -> "grossAmount=" + f + ", ").orElse("") +
            optionalDiscountAmount().map(f -> "discountAmount=" + f + ", ").orElse("") +
            optionalNetAmount().map(f -> "netAmount=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalItemsId().map(f -> "itemsId=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalWarehouseId().map(f -> "warehouseId=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
