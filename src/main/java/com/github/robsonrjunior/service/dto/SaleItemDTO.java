package com.github.robsonrjunior.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.github.robsonrjunior.domain.SaleItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleItemDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0.000001")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0")
    private BigDecimal discountAmount;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal lineTotal;

    private Instant deletedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SaleItemDTO)) {
            return false;
        }

        SaleItemDTO saleItemDTO = (SaleItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, saleItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleItemDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", discountAmount=" + getDiscountAmount() +
            ", lineTotal=" + getLineTotal() +
            ", deletedAt='" + getDeletedAt() + "'" +
            "}";
    }
}
