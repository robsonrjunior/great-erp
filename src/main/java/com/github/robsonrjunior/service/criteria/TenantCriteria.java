package com.github.robsonrjunior.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.github.robsonrjunior.domain.Tenant} entity. This class is used
 * in {@link com.github.robsonrjunior.web.rest.TenantResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tenants?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter code;

    private BooleanFilter active;

    private InstantFilter deletedAt;

    private LongFilter customersId;

    private LongFilter suppliersId;

    private LongFilter peopleId;

    private LongFilter companiesId;

    private LongFilter productsId;

    private LongFilter rawMaterialsId;

    private LongFilter warehousesId;

    private LongFilter salesId;

    private LongFilter saleItemsId;

    private LongFilter stockMovementsId;

    private Boolean distinct;

    public TenantCriteria() {}

    public TenantCriteria(TenantCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.customersId = other.optionalCustomersId().map(LongFilter::copy).orElse(null);
        this.suppliersId = other.optionalSuppliersId().map(LongFilter::copy).orElse(null);
        this.peopleId = other.optionalPeopleId().map(LongFilter::copy).orElse(null);
        this.companiesId = other.optionalCompaniesId().map(LongFilter::copy).orElse(null);
        this.productsId = other.optionalProductsId().map(LongFilter::copy).orElse(null);
        this.rawMaterialsId = other.optionalRawMaterialsId().map(LongFilter::copy).orElse(null);
        this.warehousesId = other.optionalWarehousesId().map(LongFilter::copy).orElse(null);
        this.salesId = other.optionalSalesId().map(LongFilter::copy).orElse(null);
        this.saleItemsId = other.optionalSaleItemsId().map(LongFilter::copy).orElse(null);
        this.stockMovementsId = other.optionalStockMovementsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TenantCriteria copy() {
        return new TenantCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public Optional<BooleanFilter> optionalActive() {
        return Optional.ofNullable(active);
    }

    public BooleanFilter active() {
        if (active == null) {
            setActive(new BooleanFilter());
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
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

    public LongFilter getCustomersId() {
        return customersId;
    }

    public Optional<LongFilter> optionalCustomersId() {
        return Optional.ofNullable(customersId);
    }

    public LongFilter customersId() {
        if (customersId == null) {
            setCustomersId(new LongFilter());
        }
        return customersId;
    }

    public void setCustomersId(LongFilter customersId) {
        this.customersId = customersId;
    }

    public LongFilter getSuppliersId() {
        return suppliersId;
    }

    public Optional<LongFilter> optionalSuppliersId() {
        return Optional.ofNullable(suppliersId);
    }

    public LongFilter suppliersId() {
        if (suppliersId == null) {
            setSuppliersId(new LongFilter());
        }
        return suppliersId;
    }

    public void setSuppliersId(LongFilter suppliersId) {
        this.suppliersId = suppliersId;
    }

    public LongFilter getPeopleId() {
        return peopleId;
    }

    public Optional<LongFilter> optionalPeopleId() {
        return Optional.ofNullable(peopleId);
    }

    public LongFilter peopleId() {
        if (peopleId == null) {
            setPeopleId(new LongFilter());
        }
        return peopleId;
    }

    public void setPeopleId(LongFilter peopleId) {
        this.peopleId = peopleId;
    }

    public LongFilter getCompaniesId() {
        return companiesId;
    }

    public Optional<LongFilter> optionalCompaniesId() {
        return Optional.ofNullable(companiesId);
    }

    public LongFilter companiesId() {
        if (companiesId == null) {
            setCompaniesId(new LongFilter());
        }
        return companiesId;
    }

    public void setCompaniesId(LongFilter companiesId) {
        this.companiesId = companiesId;
    }

    public LongFilter getProductsId() {
        return productsId;
    }

    public Optional<LongFilter> optionalProductsId() {
        return Optional.ofNullable(productsId);
    }

    public LongFilter productsId() {
        if (productsId == null) {
            setProductsId(new LongFilter());
        }
        return productsId;
    }

    public void setProductsId(LongFilter productsId) {
        this.productsId = productsId;
    }

    public LongFilter getRawMaterialsId() {
        return rawMaterialsId;
    }

    public Optional<LongFilter> optionalRawMaterialsId() {
        return Optional.ofNullable(rawMaterialsId);
    }

    public LongFilter rawMaterialsId() {
        if (rawMaterialsId == null) {
            setRawMaterialsId(new LongFilter());
        }
        return rawMaterialsId;
    }

    public void setRawMaterialsId(LongFilter rawMaterialsId) {
        this.rawMaterialsId = rawMaterialsId;
    }

    public LongFilter getWarehousesId() {
        return warehousesId;
    }

    public Optional<LongFilter> optionalWarehousesId() {
        return Optional.ofNullable(warehousesId);
    }

    public LongFilter warehousesId() {
        if (warehousesId == null) {
            setWarehousesId(new LongFilter());
        }
        return warehousesId;
    }

    public void setWarehousesId(LongFilter warehousesId) {
        this.warehousesId = warehousesId;
    }

    public LongFilter getSalesId() {
        return salesId;
    }

    public Optional<LongFilter> optionalSalesId() {
        return Optional.ofNullable(salesId);
    }

    public LongFilter salesId() {
        if (salesId == null) {
            setSalesId(new LongFilter());
        }
        return salesId;
    }

    public void setSalesId(LongFilter salesId) {
        this.salesId = salesId;
    }

    public LongFilter getSaleItemsId() {
        return saleItemsId;
    }

    public Optional<LongFilter> optionalSaleItemsId() {
        return Optional.ofNullable(saleItemsId);
    }

    public LongFilter saleItemsId() {
        if (saleItemsId == null) {
            setSaleItemsId(new LongFilter());
        }
        return saleItemsId;
    }

    public void setSaleItemsId(LongFilter saleItemsId) {
        this.saleItemsId = saleItemsId;
    }

    public LongFilter getStockMovementsId() {
        return stockMovementsId;
    }

    public Optional<LongFilter> optionalStockMovementsId() {
        return Optional.ofNullable(stockMovementsId);
    }

    public LongFilter stockMovementsId() {
        if (stockMovementsId == null) {
            setStockMovementsId(new LongFilter());
        }
        return stockMovementsId;
    }

    public void setStockMovementsId(LongFilter stockMovementsId) {
        this.stockMovementsId = stockMovementsId;
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
        final TenantCriteria that = (TenantCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(code, that.code) &&
            Objects.equals(active, that.active) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(customersId, that.customersId) &&
            Objects.equals(suppliersId, that.suppliersId) &&
            Objects.equals(peopleId, that.peopleId) &&
            Objects.equals(companiesId, that.companiesId) &&
            Objects.equals(productsId, that.productsId) &&
            Objects.equals(rawMaterialsId, that.rawMaterialsId) &&
            Objects.equals(warehousesId, that.warehousesId) &&
            Objects.equals(salesId, that.salesId) &&
            Objects.equals(saleItemsId, that.saleItemsId) &&
            Objects.equals(stockMovementsId, that.stockMovementsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            code,
            active,
            deletedAt,
            customersId,
            suppliersId,
            peopleId,
            companiesId,
            productsId,
            rawMaterialsId,
            warehousesId,
            salesId,
            saleItemsId,
            stockMovementsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalCustomersId().map(f -> "customersId=" + f + ", ").orElse("") +
            optionalSuppliersId().map(f -> "suppliersId=" + f + ", ").orElse("") +
            optionalPeopleId().map(f -> "peopleId=" + f + ", ").orElse("") +
            optionalCompaniesId().map(f -> "companiesId=" + f + ", ").orElse("") +
            optionalProductsId().map(f -> "productsId=" + f + ", ").orElse("") +
            optionalRawMaterialsId().map(f -> "rawMaterialsId=" + f + ", ").orElse("") +
            optionalWarehousesId().map(f -> "warehousesId=" + f + ", ").orElse("") +
            optionalSalesId().map(f -> "salesId=" + f + ", ").orElse("") +
            optionalSaleItemsId().map(f -> "saleItemsId=" + f + ", ").orElse("") +
            optionalStockMovementsId().map(f -> "stockMovementsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
