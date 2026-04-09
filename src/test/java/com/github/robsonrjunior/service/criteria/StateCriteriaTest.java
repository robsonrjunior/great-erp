package com.github.robsonrjunior.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StateCriteriaTest {

    @Test
    void newStateCriteriaHasAllFiltersNullTest() {
        var stateCriteria = new StateCriteria();
        assertThat(stateCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void stateCriteriaFluentMethodsCreatesFiltersTest() {
        var stateCriteria = new StateCriteria();

        setAllFilters(stateCriteria);

        assertThat(stateCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void stateCriteriaCopyCreatesNullFilterTest() {
        var stateCriteria = new StateCriteria();
        var copy = stateCriteria.copy();

        assertThat(stateCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(stateCriteria)
        );
    }

    @Test
    void stateCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var stateCriteria = new StateCriteria();
        setAllFilters(stateCriteria);

        var copy = stateCriteria.copy();

        assertThat(stateCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(stateCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var stateCriteria = new StateCriteria();

        assertThat(stateCriteria).hasToString("StateCriteria{}");
    }

    private static void setAllFilters(StateCriteria stateCriteria) {
        stateCriteria.id();
        stateCriteria.name();
        stateCriteria.code();
        stateCriteria.citiesId();
        stateCriteria.countryId();
        stateCriteria.distinct();
    }

    private static Condition<StateCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getCitiesId()) &&
                condition.apply(criteria.getCountryId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StateCriteria> copyFiltersAre(StateCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getCitiesId(), copy.getCitiesId()) &&
                condition.apply(criteria.getCountryId(), copy.getCountryId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
