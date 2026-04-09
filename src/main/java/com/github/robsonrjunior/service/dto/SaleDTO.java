package com.github.robsonrjunior.service.dto;

import com.github.robsonrjunior.domain.enumeration.SaleStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.github.robsonrjunior.domain.Sale} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant saleDate;

    @NotNull
    @Size(min = 2, max = 40)
    private String saleNumber;

    @NotNull
    private SaleStatus status;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal grossAmount;

    @DecimalMin(value = "0")
    private BigDecimal discountAmount;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal netAmount;

    @Size(max = 500)
    private String notes;

    private Instant deletedAt;

    private SaleItemDTO items;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Instant saleDate) {
        this.saleDate = saleDate;
    }

    public String getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }

    public SaleStatus getStatus() {
        return status;
    }

    public void setStatus(SaleStatus status) {
        this.status = status;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public SaleItemDTO getItems() {
        return items;
    }

    public void setItems(SaleItemDTO items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SaleDTO)) {
            return false;
        }

        SaleDTO saleDTO = (SaleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, saleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleDTO{" +
            "id=" + getId() +
            ", saleDate='" + getSaleDate() + "'" +
            ", saleNumber='" + getSaleNumber() + "'" +
            ", status='" + getStatus() + "'" +
            ", grossAmount=" + getGrossAmount() +
            ", discountAmount=" + getDiscountAmount() +
            ", netAmount=" + getNetAmount() +
            ", notes='" + getNotes() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", items=" + getItems() +
            "}";
    }
}
