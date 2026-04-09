package com.github.robsonrjunior.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SaleCriteriaTest {

    @Test
    void newSaleCriteriaHasAllFiltersNullTest() {
        var saleCriteria = new SaleCriteria();
        assertThat(saleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void saleCriteriaFluentMethodsCreatesFiltersTest() {
        var saleCriteria = new SaleCriteria();

        setAllFilters(saleCriteria);

        assertThat(saleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void saleCriteriaCopyCreatesNullFilterTest() {
        var saleCriteria = new SaleCriteria();
        var copy = saleCriteria.copy();

        assertThat(saleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(saleCriteria)
        );
    }

    @Test
    void saleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var saleCriteria = new SaleCriteria();
        setAllFilters(saleCriteria);

        var copy = saleCriteria.copy();

        assertThat(saleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(saleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var saleCriteria = new SaleCriteria();

        assertThat(saleCriteria).hasToString("SaleCriteria{}");
    }

    private static void setAllFilters(SaleCriteria saleCriteria) {
        saleCriteria.id();
        saleCriteria.saleDate();
        saleCriteria.saleNumber();
        saleCriteria.status();
        saleCriteria.grossAmount();
        saleCriteria.discountAmount();
        saleCriteria.netAmount();
        saleCriteria.notes();
        saleCriteria.deletedAt();
        saleCriteria.itemsId();
        saleCriteria.tenantId();
        saleCriteria.warehouseId();
        saleCriteria.customerId();
        saleCriteria.distinct();
    }

    private static Condition<SaleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSaleDate()) &&
                condition.apply(criteria.getSaleNumber()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getGrossAmount()) &&
                condition.apply(criteria.getDiscountAmount()) &&
                condition.apply(criteria.getNetAmount()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getItemsId()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getWarehouseId()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SaleCriteria> copyFiltersAre(SaleCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSaleDate(), copy.getSaleDate()) &&
                condition.apply(criteria.getSaleNumber(), copy.getSaleNumber()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getGrossAmount(), copy.getGrossAmount()) &&
                condition.apply(criteria.getDiscountAmount(), copy.getDiscountAmount()) &&
                condition.apply(criteria.getNetAmount(), copy.getNetAmount()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getItemsId(), copy.getItemsId()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getWarehouseId(), copy.getWarehouseId()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
