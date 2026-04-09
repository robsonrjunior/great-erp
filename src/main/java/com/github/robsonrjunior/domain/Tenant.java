package com.github.robsonrjunior.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tenant.
 */
@Entity
@Table(name = "tenant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "tenant")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tenant implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Long)
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    @Column(name = "name", length = 100, nullable = false)
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
    @Size(min = 2, max = 30)
    @Column(name = "code", length = 30, nullable = false, unique = true)
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
    private String code;

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
    @JsonIgnoreProperties(value = { "person", "company", "sales", "tenants", "cities" }, allowSetters = true)
    private Customer customers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "person", "company", "rawMaterials", "tenants", "cities" }, allowSetters = true)
    private Supplier suppliers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "customer", "supplier", "tenants", "cities" }, allowSetters = true)
    private Person people;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "customer", "supplier", "tenants", "cities" }, allowSetters = true)
    private Company companies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "saleItems", "stockMovements", "tenants" }, allowSetters = true)
    private Product products;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "stockMovements", "tenants", "primarySuppliers" }, allowSetters = true)
    private RawMaterial rawMaterials;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "stockMovements", "sales", "tenants", "cities" }, allowSetters = true)
    private Warehouse warehouses;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "items", "tenants", "warehouses", "customers" }, allowSetters = true)
    private Sale sales;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "tenants", "sales", "products" }, allowSetters = true)
    private SaleItem saleItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "tenants", "warehouses", "products", "rawMaterials" }, allowSetters = true)
    private StockMovement stockMovements;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tenant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Tenant name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public Tenant code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Tenant active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public Tenant deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Customer getCustomers() {
        return this.customers;
    }

    public void setCustomers(Customer customer) {
        this.customers = customer;
    }

    public Tenant customers(Customer customer) {
        this.setCustomers(customer);
        return this;
    }

    public Supplier getSuppliers() {
        return this.suppliers;
    }

    public void setSuppliers(Supplier supplier) {
        this.suppliers = supplier;
    }

    public Tenant suppliers(Supplier supplier) {
        this.setSuppliers(supplier);
        return this;
    }

    public Person getPeople() {
        return this.people;
    }

    public void setPeople(Person person) {
        this.people = person;
    }

    public Tenant people(Person person) {
        this.setPeople(person);
        return this;
    }

    public Company getCompanies() {
        return this.companies;
    }

    public void setCompanies(Company company) {
        this.companies = company;
    }

    public Tenant companies(Company company) {
        this.setCompanies(company);
        return this;
    }

    public Product getProducts() {
        return this.products;
    }

    public void setProducts(Product product) {
        this.products = product;
    }

    public Tenant products(Product product) {
        this.setProducts(product);
        return this;
    }

    public RawMaterial getRawMaterials() {
        return this.rawMaterials;
    }

    public void setRawMaterials(RawMaterial rawMaterial) {
        this.rawMaterials = rawMaterial;
    }

    public Tenant rawMaterials(RawMaterial rawMaterial) {
        this.setRawMaterials(rawMaterial);
        return this;
    }

    public Warehouse getWarehouses() {
        return this.warehouses;
    }

    public void setWarehouses(Warehouse warehouse) {
        this.warehouses = warehouse;
    }

    public Tenant warehouses(Warehouse warehouse) {
        this.setWarehouses(warehouse);
        return this;
    }

    public Sale getSales() {
        return this.sales;
    }

    public void setSales(Sale sale) {
        this.sales = sale;
    }

    public Tenant sales(Sale sale) {
        this.setSales(sale);
        return this;
    }

    public SaleItem getSaleItems() {
        return this.saleItems;
    }

    public void setSaleItems(SaleItem saleItem) {
        this.saleItems = saleItem;
    }

    public Tenant saleItems(SaleItem saleItem) {
        this.setSaleItems(saleItem);
        return this;
    }

    public StockMovement getStockMovements() {
        return this.stockMovements;
    }

    public void setStockMovements(StockMovement stockMovement) {
        this.stockMovements = stockMovement;
    }

    public Tenant stockMovements(StockMovement stockMovement) {
        this.setStockMovements(stockMovement);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tenant)) {
            return false;
        }
        return getId() != null && getId().equals(((Tenant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tenant{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", active='" + getActive() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            "}";
    }
}
