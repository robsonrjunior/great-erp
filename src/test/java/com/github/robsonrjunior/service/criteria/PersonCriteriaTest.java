package com.github.robsonrjunior.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PersonCriteriaTest {

    @Test
    void newPersonCriteriaHasAllFiltersNullTest() {
        var personCriteria = new PersonCriteria();
        assertThat(personCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void personCriteriaFluentMethodsCreatesFiltersTest() {
        var personCriteria = new PersonCriteria();

        setAllFilters(personCriteria);

        assertThat(personCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void personCriteriaCopyCreatesNullFilterTest() {
        var personCriteria = new PersonCriteria();
        var copy = personCriteria.copy();

        assertThat(personCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(personCriteria)
        );
    }

    @Test
    void personCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var personCriteria = new PersonCriteria();
        setAllFilters(personCriteria);

        var copy = personCriteria.copy();

        assertThat(personCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(personCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var personCriteria = new PersonCriteria();

        assertThat(personCriteria).hasToString("PersonCriteria{}");
    }

    private static void setAllFilters(PersonCriteria personCriteria) {
        personCriteria.id();
        personCriteria.fullName();
        personCriteria.cpf();
        personCriteria.birthDate();
        personCriteria.email();
        personCriteria.phone();
        personCriteria.active();
        personCriteria.deletedAt();
        personCriteria.customerId();
        personCriteria.supplierId();
        personCriteria.tenantId();
        personCriteria.cityId();
        personCriteria.distinct();
    }

    private static Condition<PersonCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFullName()) &&
                condition.apply(criteria.getCpf()) &&
                condition.apply(criteria.getBirthDate()) &&
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

    private static Condition<PersonCriteria> copyFiltersAre(PersonCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFullName(), copy.getFullName()) &&
                condition.apply(criteria.getCpf(), copy.getCpf()) &&
                condition.apply(criteria.getBirthDate(), copy.getBirthDate()) &&
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
