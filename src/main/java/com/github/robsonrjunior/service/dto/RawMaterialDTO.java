package com.github.robsonrjunior.service.dto;

import com.github.robsonrjunior.domain.enumeration.UnitOfMeasure;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.github.robsonrjunior.domain.RawMaterial} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RawMaterialDTO implements Serializable {

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

    @DecimalMin(value = "0")
    private BigDecimal unitCost;

    @DecimalMin(value = "0")
    private BigDecimal minStock;

    @NotNull
    private Boolean active;

    private Instant deletedAt;

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

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
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
        if (!(o instanceof RawMaterialDTO)) {
            return false;
        }

        RawMaterialDTO rawMaterialDTO = (RawMaterialDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, rawMaterialDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RawMaterialDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", sku='" + getSku() + "'" +
            ", unitOfMeasure='" + getUnitOfMeasure() + "'" +
            ", unitDecimalPlaces=" + getUnitDecimalPlaces() +
            ", unitCost=" + getUnitCost() +
            ", minStock=" + getMinStock() +
            ", active='" + getActive() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", stockMovements=" + getStockMovements() +
            "}";
    }
}
