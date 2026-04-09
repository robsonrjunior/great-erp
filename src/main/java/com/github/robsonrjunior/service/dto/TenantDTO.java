package com.github.robsonrjunior.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.github.robsonrjunior.domain.Tenant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    @NotNull
    @Size(min = 2, max = 30)
    private String code;

    @NotNull
    private Boolean active;

    private Instant deletedAt;

    private CustomerDTO customers;

    private SupplierDTO suppliers;

    private PersonDTO people;

    private CompanyDTO companies;

    private ProductDTO products;

    private RawMaterialDTO rawMaterials;

    private WarehouseDTO warehouses;

    private SaleDTO sales;

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

    public CustomerDTO getCustomers() {
        return customers;
    }

    public void setCustomers(CustomerDTO customers) {
        this.customers = customers;
    }

    public SupplierDTO getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(SupplierDTO suppliers) {
        this.suppliers = suppliers;
    }

    public PersonDTO getPeople() {
        return people;
    }

    public void setPeople(PersonDTO people) {
        this.people = people;
    }

    public CompanyDTO getCompanies() {
        return companies;
    }

    public void setCompanies(CompanyDTO companies) {
        this.companies = companies;
    }

    public ProductDTO getProducts() {
        return products;
    }

    public void setProducts(ProductDTO products) {
        this.products = products;
    }

    public RawMaterialDTO getRawMaterials() {
        return rawMaterials;
    }

    public void setRawMaterials(RawMaterialDTO rawMaterials) {
        this.rawMaterials = rawMaterials;
    }

    public WarehouseDTO getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(WarehouseDTO warehouses) {
        this.warehouses = warehouses;
    }

    public SaleDTO getSales() {
        return sales;
    }

    public void setSales(SaleDTO sales) {
        this.sales = sales;
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
        if (!(o instanceof TenantDTO)) {
            return false;
        }

        TenantDTO tenantDTO = (TenantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tenantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", active='" + getActive() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", customers=" + getCustomers() +
            ", suppliers=" + getSuppliers() +
            ", people=" + getPeople() +
            ", companies=" + getCompanies() +
            ", products=" + getProducts() +
            ", rawMaterials=" + getRawMaterials() +
            ", warehouses=" + getWarehouses() +
            ", sales=" + getSales() +
            ", saleItems=" + getSaleItems() +
            ", stockMovements=" + getStockMovements() +
            "}";
    }
}
