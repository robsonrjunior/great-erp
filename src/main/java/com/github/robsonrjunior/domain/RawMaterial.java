package com.github.robsonrjunior.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.robsonrjunior.domain.enumeration.UnitOfMeasure;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RawMaterial.
 */
@Entity
@Table(name = "raw_material")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "rawmaterial")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RawMaterial implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Long)
    private Long id;

    @NotNull
    @Size(min = 2, max = 120)
    @Column(name = "name", length = 120, nullable = false)
    @org.springframework.data.elasticsearch.annotations.MultiField(
        mainField = @org.springframework.data.elasticsearch.annotations.Field(
            type = org.springframework.data.elasticsearch.annotations.FieldType.Text
        ),
        otherFields = {
            @org.springframework.data.elasticsearch.annotations.InnerField(
                suffix = "keyword",
                type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword,
                ignoreAbove = 256
            ),
        }
    )
    private String name;

    @NotNull
    @Size(min = 2, max = 40)
    @Column(name = "sku", length = 40, nullable = false)
    @org.springframework.data.elasticsearch.annotations.MultiField(
        mainField = @org.springframework.data.elasticsearch.annotations.Field(
            type = org.springframework.data.elasticsearch.annotations.FieldType.Text
        ),
        otherFields = {
            @org.springframework.data.elasticsearch.annotations.InnerField(
                suffix = "keyword",
                type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword,
                ignoreAbove = 256
            ),
        }
    )
    private String sku;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private UnitOfMeasure unitOfMeasure;

    @NotNull
    @Min(value = 0)
    @Max(value = 6)
    @Column(name = "unit_decimal_places", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer unitDecimalPlaces;

    @DecimalMin(value = "0")
    @Column(name = "unit_cost", precision = 21, scale = 2)
    private BigDecimal unitCost;

    @DecimalMin(value = "0")
    @Column(name = "min_stock", precision = 21, scale = 2)
    private BigDecimal minStock;

    @NotNull
    @Column(name = "active", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean active;

    @Column(name = "deleted_at")
    @org.springframework.data.elasticsearch.annotations.Field(
        type = org.springframework.data.elasticsearch.annotations.FieldType.Date,
        format = org.springframework.data.elasticsearch.annotations.DateFormat.date_time
    )
    private Instant deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "tenants", "warehouses", "products", "rawMaterials" }, allowSetters = true)
    private StockMovement stockMovements;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rawMaterials")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(
        value = {
            "customers",
            "suppliers",
            "people",
            "companies",
            "products",
            "rawMaterials",
            "warehouses",
            "sales",
            "saleItems",
            "stockMovements",
        },
        allowSetters = true
    )
    private Set<Tenant> tenants = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rawMaterials")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "person", "company", "rawMaterials", "tenants", "cities" }, allowSetters = true)
    private Set<Supplier> primarySuppliers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RawMaterial id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public RawMaterial name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return this.sku;
    }

    public RawMaterial sku(String sku) {
        this.setSku(sku);
        return this;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return this.unitOfMeasure;
    }

    public RawMaterial unitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.setUnitOfMeasure(unitOfMeasure);
        return this;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Integer getUnitDecimalPlaces() {
        return this.unitDecimalPlaces;
    }

    public RawMaterial unitDecimalPlaces(Integer unitDecimalPlaces) {
        this.setUnitDecimalPlaces(unitDecimalPlaces);
        return this;
    }

    public void setUnitDecimalPlaces(Integer unitDecimalPlaces) {
        this.unitDecimalPlaces = unitDecimalPlaces;
    }

    public BigDecimal getUnitCost() {
        return this.unitCost;
    }

    public RawMaterial unitCost(BigDecimal unitCost) {
        this.setUnitCost(unitCost);
        return this;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getMinStock() {
        return this.minStock;
    }

    public RawMaterial minStock(BigDecimal minStock) {
        this.setMinStock(minStock);
        return this;
    }

    public void setMinStock(BigDecimal minStock) {
        this.minStock = minStock;
    }

    public Boolean getActive() {
        return this.active;
    }

    public RawMaterial active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public RawMaterial deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public StockMovement getStockMovements() {
        return this.stockMovements;
    }

    public void setStockMovements(StockMovement stockMovement) {
        this.stockMovements = stockMovement;
    }

    public RawMaterial stockMovements(StockMovement stockMovement) {
        this.setStockMovements(stockMovement);
        return this;
    }

    public Set<Tenant> getTenants() {
        return this.tenants;
    }

    public void setTenants(Set<Tenant> tenants) {
        if (this.tenants != null) {
            this.tenants.forEach(i -> i.setRawMaterials(null));
        }
        if (tenants != null) {
            tenants.forEach(i -> i.setRawMaterials(this));
        }
        this.tenants = tenants;
    }

    public RawMaterial tenants(Set<Tenant> tenants) {
        this.setTenants(tenants);
        return this;
    }

    public RawMaterial addTenant(Tenant tenant) {
        this.tenants.add(tenant);
        tenant.setRawMaterials(this);
        return this;
    }

    public RawMaterial removeTenant(Tenant tenant) {
        this.tenants.remove(tenant);
        tenant.setRawMaterials(null);
        return this;
    }

    public Set<Supplier> getPrimarySuppliers() {
        return this.primarySuppliers;
    }

    public void setPrimarySuppliers(Set<Supplier> suppliers) {
        if (this.primarySuppliers != null) {
            this.primarySuppliers.forEach(i -> i.setRawMaterials(null));
        }
        if (suppliers != null) {
            suppliers.forEach(i -> i.setRawMaterials(this));
        }
        this.primarySuppliers = suppliers;
    }

    public RawMaterial primarySuppliers(Set<Supplier> suppliers) {
        this.setPrimarySuppliers(suppliers);
        return this;
    }

    public RawMaterial addPrimarySupplier(Supplier supplier) {
        this.primarySuppliers.add(supplier);
        supplier.setRawMaterials(this);
        return this;
    }

    public RawMaterial removePrimarySupplier(Supplier supplier) {
        this.primarySuppliers.remove(supplier);
        supplier.setRawMaterials(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RawMaterial)) {
            return false;
        }
        return getId() != null && getId().equals(((RawMaterial) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RawMaterial{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", sku='" + getSku() + "'" +
            ", unitOfMeasure='" + getUnitOfMeasure() + "'" +
            ", unitDecimalPlaces=" + getUnitDecimalPlaces() +
            ", unitCost=" + getUnitCost() +
            ", minStock=" + getMinStock() +
            ", active='" + getActive() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            "}";
    }
}
