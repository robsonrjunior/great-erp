package com.github.robsonrjunior.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SupplierCriteriaTest {

    @Test
    void newSupplierCriteriaHasAllFiltersNullTest() {
        var supplierCriteria = new SupplierCriteria();
        assertThat(supplierCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void supplierCriteriaFluentMethodsCreatesFiltersTest() {
        var supplierCriteria = new SupplierCriteria();

        setAllFilters(supplierCriteria);

        assertThat(supplierCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void supplierCriteriaCopyCreatesNullFilterTest() {
        var supplierCriteria = new SupplierCriteria();
        var copy = supplierCriteria.copy();

        assertThat(supplierCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(supplierCriteria)
        );
    }

    @Test
    void supplierCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var supplierCriteria = new SupplierCriteria();
        setAllFilters(supplierCriteria);

        var copy = supplierCriteria.copy();

        assertThat(supplierCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(supplierCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var supplierCriteria = new SupplierCriteria();

        assertThat(supplierCriteria).hasToString("SupplierCriteria{}");
    }

    private static void setAllFilters(SupplierCriteria supplierCriteria) {
        supplierCriteria.id();
        supplierCriteria.legalName();
        supplierCriteria.tradeName();
        supplierCriteria.taxId();
        supplierCriteria.partyType();
        supplierCriteria.email();
        supplierCriteria.phone();
        supplierCriteria.active();
        supplierCriteria.deletedAt();
        supplierCriteria.personId();
        supplierCriteria.companyId();
        supplierCriteria.rawMaterialsId();
        supplierCriteria.tenantId();
        supplierCriteria.cityId();
        supplierCriteria.distinct();
    }

    private static Condition<SupplierCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getLegalName()) &&
                condition.apply(criteria.getTradeName()) &&
                condition.apply(criteria.getTaxId()) &&
                condition.apply(criteria.getPartyType()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getPersonId()) &&
                condition.apply(criteria.getCompanyId()) &&
                condition.apply(criteria.getRawMaterialsId()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getCityId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SupplierCriteria> copyFiltersAre(SupplierCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getLegalName(), copy.getLegalName()) &&
                condition.apply(criteria.getTradeName(), copy.getTradeName()) &&
                condition.apply(criteria.getTaxId(), copy.getTaxId()) &&
                condition.apply(criteria.getPartyType(), copy.getPartyType()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getPersonId(), copy.getPersonId()) &&
                condition.apply(criteria.getCompanyId(), copy.getCompanyId()) &&
                condition.apply(criteria.getRawMaterialsId(), copy.getRawMaterialsId()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getCityId(), copy.getCityId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
