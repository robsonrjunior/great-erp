package com.github.robsonrjunior.service.dto;

import com.github.robsonrjunior.domain.enumeration.UnitOfMeasure;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.github.robsonrjunior.domain.Product} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 120)
    private String name;

    @NotNull
    @Size(min = 2, max = 40)
    private String sku;

    @NotNull
    private UnitOfMeasure unitOfMeasure;

    @NotNull
    @Min(value = 0)
    @Max(value = 6)
    private Integer unitDecimalPlaces;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal salePrice;

    @DecimalMin(value = "0")
    private BigDecimal costPrice;

    @DecimalMin(value = "0")
    private BigDecimal minStock;

    @NotNull
    private Boolean active;

    private Instant deletedAt;

    private SaleItemDTO saleItems;

    private StockMovementDTO stockMovements;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Integer getUnitDecimalPlaces() {
        return unitDecimalPlaces;
    }

    public void setUnitDecimalPlaces(Integer unitDecimalPlaces) {
        this.unitDecimalPlaces = unitDecimalPlaces;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getMinStock() {
        return minStock;
    }

    public void setMinStock(BigDecimal minStock) {
        this.minStock = minStock;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public SaleItemDTO getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(SaleItemDTO saleItems) {
        this.saleItems = saleItems;
    }

    public StockMovementDTO getStockMovements() {
        return stockMovements;
    }

    public void setStockMovements(StockMovementDTO stockMovements) {
        this.stockMovements = stockMovements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductDTO)) {
            return false;
        }

        ProductDTO productDTO = (ProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", sku='" + getSku() + "'" +
            ", unitOfMeasure='" + getUnitOfMeasure() + "'" +
            ", unitDecimalPlaces=" + getUnitDecimalPlaces() +
            ", salePrice=" + getSalePrice() +
            ", costPrice=" + getCostPrice() +
            ", minStock=" + getMinStock() +
            ", active='" + getActive() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", saleItems=" + getSaleItems() +
            ", stockMovements=" + getStockMovements() +
            "}";
    }
}
