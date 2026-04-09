package com.github.robsonrjunior.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.robsonrjunior.domain.enumeration.SaleStatus;
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
 * A Sale.
 */
@Entity
@Table(name = "sale")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "sale")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Sale implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Long)
    private Long id;

    @NotNull
    @Column(name = "sale_date", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(
        type = org.springframework.data.elasticsearch.annotations.FieldType.Date,
        format = org.springframework.data.elasticsearch.annotations.DateFormat.date_time
    )
    private Instant saleDate;

    @NotNull
    @Size(min = 2, max = 40)
    @Column(name = "sale_number", length = 40, nullable = false)
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
    private String saleNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private SaleStatus status;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "gross_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal grossAmount;

    @DecimalMin(value = "0")
    @Column(name = "discount_amount", precision = 21, scale = 2)
    private BigDecimal discountAmount;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "net_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal netAmount;

    @Size(max = 500)
    @Column(name = "notes", length = 500)
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
    private String notes;

    @Column(name = "deleted_at")
    @org.springframework.data.elasticsearch.annotations.Field(
        type = org.springframework.data.elasticsearch.annotations.FieldType.Date,
        format = org.springframework.data.elasticsearch.annotations.DateFormat.date_time
    )
    private Instant deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "tenants", "sales", "products" }, allowSetters = true)
    private SaleItem items;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sales")
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sales")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "stockMovements", "sales", "tenants", "cities" }, allowSetters = true)
    private Set<Warehouse> warehouses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sales")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "person", "company", "sales", "tenants", "cities" }, allowSetters = true)
    private Set<Customer> customers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sale id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getSaleDate() {
        return this.saleDate;
    }

    public Sale saleDate(Instant saleDate) {
        this.setSaleDate(saleDate);
        return this;
    }

    public void setSaleDate(Instant saleDate) {
        this.saleDate = saleDate;
    }

    public String getSaleNumber() {
        return this.saleNumber;
    }

    public Sale saleNumber(String saleNumber) {
        this.setSaleNumber(saleNumber);
        return this;
    }

    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }

    public SaleStatus getStatus() {
        return this.status;
    }

    public Sale status(SaleStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SaleStatus status) {
        this.status = status;
    }

    public BigDecimal getGrossAmount() {
        return this.grossAmount;
    }

    public Sale grossAmount(BigDecimal grossAmount) {
        this.setGrossAmount(grossAmount);
        return this;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getDiscountAmount() {
        return this.discountAmount;
    }

    public Sale discountAmount(BigDecimal discountAmount) {
        this.setDiscountAmount(discountAmount);
        return this;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getNetAmount() {
        return this.netAmount;
    }

    public Sale netAmount(BigDecimal netAmount) {
        this.setNetAmount(netAmount);
        return this;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getNotes() {
        return this.notes;
    }

    public Sale notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public Sale deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public SaleItem getItems() {
        return this.items;
    }

    public void setItems(SaleItem saleItem) {
        this.items = saleItem;
    }

    public Sale items(SaleItem saleItem) {
        this.setItems(saleItem);
        return this;
    }

    public Set<Tenant> getTenants() {
        return this.tenants;
    }

    public void setTenants(Set<Tenant> tenants) {
        if (this.tenants != null) {
            this.tenants.forEach(i -> i.setSales(null));
        }
        if (tenants != null) {
            tenants.forEach(i -> i.setSales(this));
        }
        this.tenants = tenants;
    }

    public Sale tenants(Set<Tenant> tenants) {
        this.setTenants(tenants);
        return this;
    }

    public Sale addTenant(Tenant tenant) {
        this.tenants.add(tenant);
        tenant.setSales(this);
        return this;
    }

    public Sale removeTenant(Tenant tenant) {
        this.tenants.remove(tenant);
        tenant.setSales(null);
        return this;
    }

    public Set<Warehouse> getWarehouses() {
        return this.warehouses;
    }

    public void setWarehouses(Set<Warehouse> warehouses) {
        if (this.warehouses != null) {
            this.warehouses.forEach(i -> i.setSales(null));
        }
        if (warehouses != null) {
            warehouses.forEach(i -> i.setSales(this));
        }
        this.warehouses = warehouses;
    }

    public Sale warehouses(Set<Warehouse> warehouses) {
        this.setWarehouses(warehouses);
        return this;
    }

    public Sale addWarehouse(Warehouse warehouse) {
        this.warehouses.add(warehouse);
        warehouse.setSales(this);
        return this;
    }

    public Sale removeWarehouse(Warehouse warehouse) {
        this.warehouses.remove(warehouse);
        warehouse.setSales(null);
        return this;
    }

    public Set<Customer> getCustomers() {
        return this.customers;
    }

    public void setCustomers(Set<Customer> customers) {
        if (this.customers != null) {
            this.customers.forEach(i -> i.setSales(null));
        }
        if (customers != null) {
            customers.forEach(i -> i.setSales(this));
        }
        this.customers = customers;
    }

    public Sale customers(Set<Customer> customers) {
        this.setCustomers(customers);
        return this;
    }

    public Sale addCustomer(Customer customer) {
        this.customers.add(customer);
        customer.setSales(this);
        return this;
    }

    public Sale removeCustomer(Customer customer) {
        this.customers.remove(customer);
        customer.setSales(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sale)) {
            return false;
        }
        return getId() != null && getId().equals(((Sale) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sale{" +
            "id=" + getId() +
            ", saleDate='" + getSaleDate() + "'" +
            ", saleNumber='" + getSaleNumber() + "'" +
            ", status='" + getStatus() + "'" +
            ", grossAmount=" + getGrossAmount() +
            ", discountAmount=" + getDiscountAmount() +
            ", netAmount=" + getNetAmount() +
            ", notes='" + getNotes() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            "}";
    }
}
