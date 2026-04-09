package com.github.robsonrjunior.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.github.robsonrjunior.domain.Warehouse} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 120)
    private String name;

    @NotNull
    @Size(min = 2, max = 30)
    private String code;

    @NotNull
    private Boolean active;

    private Instant deletedAt;

    private StockMovementDTO stockMovements;

    private SaleDTO sales;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public SaleDTO getSales() {
        return sales;
    }

    public void setSales(SaleDTO sales) {
        this.sales = sales;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseDTO)) {
            return false;
        }

        WarehouseDTO warehouseDTO = (WarehouseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, warehouseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", active='" + getActive() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", stockMovements=" + getStockMovements() +
            ", sales=" + getSales() +
            "}";
    }
}
