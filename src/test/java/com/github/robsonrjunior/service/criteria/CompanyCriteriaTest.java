package com.github.robsonrjunior.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CompanyCriteriaTest {

    @Test
    void newCompanyCriteriaHasAllFiltersNullTest() {
        var companyCriteria = new CompanyCriteria();
        assertThat(companyCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void companyCriteriaFluentMethodsCreatesFiltersTest() {
        var companyCriteria = new CompanyCriteria();

        setAllFilters(companyCriteria);

        assertThat(companyCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void companyCriteriaCopyCreatesNullFilterTest() {
        var companyCriteria = new CompanyCriteria();
        var copy = companyCriteria.copy();

        assertThat(companyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(companyCriteria)
        );
    }

    @Test
    void companyCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var companyCriteria = new CompanyCriteria();
        setAllFilters(companyCriteria);

        var copy = companyCriteria.copy();

        assertThat(companyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(companyCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var companyCriteria = new CompanyCriteria();

        assertThat(companyCriteria).hasToString("CompanyCriteria{}");
    }

    private static void setAllFilters(CompanyCriteria companyCriteria) {
        companyCriteria.id();
        companyCriteria.legalName();
        companyCriteria.tradeName();
        companyCriteria.cnpj();
        companyCriteria.stateRegistration();
        companyCriteria.email();
        companyCriteria.phone();
        companyCriteria.active();
        companyCriteria.deletedAt();
        companyCriteria.customerId();
        companyCriteria.supplierId();
        companyCriteria.tenantId();
        companyCriteria.cityId();
        companyCriteria.distinct();
    }

    private static Condition<CompanyCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getLegalName()) &&
                condition.apply(criteria.getTradeName()) &&
                condition.apply(criteria.getCnpj()) &&
                condition.apply(criteria.getStateRegistration()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getSupplierId()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getCityId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CompanyCriteria> copyFiltersAre(CompanyCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getLegalName(), copy.getLegalName()) &&
                condition.apply(criteria.getTradeName(), copy.getTradeName()) &&
                condition.apply(criteria.getCnpj(), copy.getCnpj()) &&
                condition.apply(criteria.getStateRegistration(), copy.getStateRegistration()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getSupplierId(), copy.getSupplierId()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getCityId(), copy.getCityId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
