package com.github.robsonrjunior.service.criteria;

import com.github.robsonrjunior.domain.enumeration.PartyType;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.github.robsonrjunior.domain.Customer} entity. This class is used
 * in {@link com.github.robsonrjunior.web.rest.CustomerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerCriteria implements Serializable, Criteria {

    /**
     * Class for filtering PartyType
     */
    public static class PartyTypeFilter extends Filter<PartyType> {

        public PartyTypeFilter() {}

        public PartyTypeFilter(PartyTypeFilter filter) {
            super(filter);
        }

        @Override
        public PartyTypeFilter copy() {
            return new PartyTypeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter legalName;

    private StringFilter tradeName;

    private StringFilter taxId;

    private PartyTypeFilter partyType;

    private StringFilter email;

    private StringFilter phone;

    private BooleanFilter active;

    private InstantFilter deletedAt;

    private LongFilter personId;

    private LongFilter companyId;

    private LongFilter salesId;

    private LongFilter tenantId;

    private LongFilter cityId;

    private Boolean distinct;

    public CustomerCriteria() {}

    public CustomerCriteria(CustomerCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.legalName = other.optionalLegalName().map(StringFilter::copy).orElse(null);
        this.tradeName = other.optionalTradeName().map(StringFilter::copy).orElse(null);
        this.taxId = other.optionalTaxId().map(StringFilter::copy).orElse(null);
        this.partyType = other.optionalPartyType().map(PartyTypeFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.personId = other.optionalPersonId().map(LongFilter::copy).orElse(null);
        this.companyId = other.optionalCompanyId().map(LongFilter::copy).orElse(null);
        this.salesId = other.optionalSalesId().map(LongFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.cityId = other.optionalCityId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CustomerCriteria copy() {
        return new CustomerCriteria(this);
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

    public StringFilter getLegalName() {
        return legalName;
    }

    public Optional<StringFilter> optionalLegalName() {
        return Optional.ofNullable(legalName);
    }

    public StringFilter legalName() {
        if (legalName == null) {
            setLegalName(new StringFilter());
        }
        return legalName;
    }

    public void setLegalName(StringFilter legalName) {
        this.legalName = legalName;
    }

    public StringFilter getTradeName() {
        return tradeName;
    }

    public Optional<StringFilter> optionalTradeName() {
        return Optional.ofNullable(tradeName);
    }

    public StringFilter tradeName() {
        if (tradeName == null) {
            setTradeName(new StringFilter());
        }
        return tradeName;
    }

    public void setTradeName(StringFilter tradeName) {
        this.tradeName = tradeName;
    }

    public StringFilter getTaxId() {
        return taxId;
    }

    public Optional<StringFilter> optionalTaxId() {
        return Optional.ofNullable(taxId);
    }

    public StringFilter taxId() {
        if (taxId == null) {
            setTaxId(new StringFilter());
        }
        return taxId;
    }

    public void setTaxId(StringFilter taxId) {
        this.taxId = taxId;
    }

    public PartyTypeFilter getPartyType() {
        return partyType;
    }

    public Optional<PartyTypeFilter> optionalPartyType() {
        return Optional.ofNullable(partyType);
    }

    public PartyTypeFilter partyType() {
        if (partyType == null) {
            setPartyType(new PartyTypeFilter());
        }
        return partyType;
    }

    public void setPartyType(PartyTypeFilter partyType) {
        this.partyType = partyType;
    }

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
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

    public LongFilter getPersonId() {
        return personId;
    }

    public Optional<LongFilter> optionalPersonId() {
        return Optional.ofNullable(personId);
    }

    public LongFilter personId() {
        if (personId == null) {
            setPersonId(new LongFilter());
        }
        return personId;
    }

    public void setPersonId(LongFilter personId) {
        this.personId = personId;
    }

    public LongFilter getCompanyId() {
        return companyId;
    }

    public Optional<LongFilter> optionalCompanyId() {
        return Optional.ofNullable(companyId);
    }

    public LongFilter companyId() {
        if (companyId == null) {
            setCompanyId(new LongFilter());
        }
        return companyId;
    }

    public void setCompanyId(LongFilter companyId) {
        this.companyId = companyId;
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

    public LongFilter getTenantId() {
        return tenantId;
    }

    public Optional<LongFilter> optionalTenantId() {
        return Optional.ofNullable(tenantId);
    }

    public LongFilter tenantId() {
        if (tenantId == null) {
            setTenantId(new LongFilter());
        }
        return tenantId;
    }

    public void setTenantId(LongFilter tenantId) {
        this.tenantId = tenantId;
    }

    public LongFilter getCityId() {
        return cityId;
    }

    public Optional<LongFilter> optionalCityId() {
        return Optional.ofNullable(cityId);
    }

    public LongFilter cityId() {
        if (cityId == null) {
            setCityId(new LongFilter());
        }
        return cityId;
    }

    public void setCityId(LongFilter cityId) {
        this.cityId = cityId;
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
        final CustomerCriteria that = (CustomerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(legalName, that.legalName) &&
            Objects.equals(tradeName, that.tradeName) &&
            Objects.equals(taxId, that.taxId) &&
            Objects.equals(partyType, that.partyType) &&
            Objects.equals(email, that.email) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(active, that.active) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(personId, that.personId) &&
            Objects.equals(companyId, that.companyId) &&
            Objects.equals(salesId, that.salesId) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(cityId, that.cityId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            legalName,
            tradeName,
            taxId,
            partyType,
            email,
            phone,
            active,
            deletedAt,
            personId,
            companyId,
            salesId,
            tenantId,
            cityId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLegalName().map(f -> "legalName=" + f + ", ").orElse("") +
            optionalTradeName().map(f -> "tradeName=" + f + ", ").orElse("") +
            optionalTaxId().map(f -> "taxId=" + f + ", ").orElse("") +
            optionalPartyType().map(f -> "partyType=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalPersonId().map(f -> "personId=" + f + ", ").orElse("") +
            optionalCompanyId().map(f -> "companyId=" + f + ", ").orElse("") +
            optionalSalesId().map(f -> "salesId=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalCityId().map(f -> "cityId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
