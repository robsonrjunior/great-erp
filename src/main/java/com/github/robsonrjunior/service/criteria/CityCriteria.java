package com.github.robsonrjunior.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.github.robsonrjunior.domain.City} entity. This class is used
 * in {@link com.github.robsonrjunior.web.rest.CityResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cities?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CityCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LongFilter suppliersId;

    private LongFilter customersId;

    private LongFilter peopleId;

    private LongFilter companiesId;

    private LongFilter warehousesId;

    private LongFilter stateId;

    private Boolean distinct;

    public CityCriteria() {}

    public CityCriteria(CityCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.suppliersId = other.optionalSuppliersId().map(LongFilter::copy).orElse(null);
        this.customersId = other.optionalCustomersId().map(LongFilter::copy).orElse(null);
        this.peopleId = other.optionalPeopleId().map(LongFilter::copy).orElse(null);
        this.companiesId = other.optionalCompaniesId().map(LongFilter::copy).orElse(null);
        this.warehousesId = other.optionalWarehousesId().map(LongFilter::copy).orElse(null);
        this.stateId = other.optionalStateId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CityCriteria copy() {
        return new CityCriteria(this);
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

    public LongFilter getStateId() {
        return stateId;
    }

    public Optional<LongFilter> optionalStateId() {
        return Optional.ofNullable(stateId);
    }

    public LongFilter stateId() {
        if (stateId == null) {
            setStateId(new LongFilter());
        }
        return stateId;
    }

    public void setStateId(LongFilter stateId) {
        this.stateId = stateId;
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
        final CityCriteria that = (CityCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(suppliersId, that.suppliersId) &&
            Objects.equals(customersId, that.customersId) &&
            Objects.equals(peopleId, that.peopleId) &&
            Objects.equals(companiesId, that.companiesId) &&
            Objects.equals(warehousesId, that.warehousesId) &&
            Objects.equals(stateId, that.stateId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, suppliersId, customersId, peopleId, companiesId, warehousesId, stateId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CityCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalSuppliersId().map(f -> "suppliersId=" + f + ", ").orElse("") +
            optionalCustomersId().map(f -> "customersId=" + f + ", ").orElse("") +
            optionalPeopleId().map(f -> "peopleId=" + f + ", ").orElse("") +
            optionalCompaniesId().map(f -> "companiesId=" + f + ", ").orElse("") +
            optionalWarehousesId().map(f -> "warehousesId=" + f + ", ").orElse("") +
            optionalStateId().map(f -> "stateId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
