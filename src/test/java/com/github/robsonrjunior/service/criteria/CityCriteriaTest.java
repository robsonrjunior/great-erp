package com.github.robsonrjunior.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CityCriteriaTest {

    @Test
    void newCityCriteriaHasAllFiltersNullTest() {
        var cityCriteria = new CityCriteria();
        assertThat(cityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void cityCriteriaFluentMethodsCreatesFiltersTest() {
        var cityCriteria = new CityCriteria();

        setAllFilters(cityCriteria);

        assertThat(cityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void cityCriteriaCopyCreatesNullFilterTest() {
        var cityCriteria = new CityCriteria();
        var copy = cityCriteria.copy();

        assertThat(cityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(cityCriteria)
        );
    }

    @Test
    void cityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var cityCriteria = new CityCriteria();
        setAllFilters(cityCriteria);

        var copy = cityCriteria.copy();

        assertThat(cityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(cityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var cityCriteria = new CityCriteria();

        assertThat(cityCriteria).hasToString("CityCriteria{}");
    }

    private static void setAllFilters(CityCriteria cityCriteria) {
        cityCriteria.id();
        cityCriteria.name();
        cityCriteria.suppliersId();
        cityCriteria.customersId();
        cityCriteria.peopleId();
        cityCriteria.companiesId();
        cityCriteria.warehousesId();
        cityCriteria.stateId();
        cityCriteria.distinct();
    }

    private static Condition<CityCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getSuppliersId()) &&
                condition.apply(criteria.getCustomersId()) &&
                condition.apply(criteria.getPeopleId()) &&
                condition.apply(criteria.getCompaniesId()) &&
                condition.apply(criteria.getWarehousesId()) &&
                condition.apply(criteria.getStateId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CityCriteria> copyFiltersAre(CityCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getSuppliersId(), copy.getSuppliersId()) &&
                condition.apply(criteria.getCustomersId(), copy.getCustomersId()) &&
                condition.apply(criteria.getPeopleId(), copy.getPeopleId()) &&
                condition.apply(criteria.getCompaniesId(), copy.getCompaniesId()) &&
                condition.apply(criteria.getWarehousesId(), copy.getWarehousesId()) &&
                condition.apply(criteria.getStateId(), copy.getStateId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
