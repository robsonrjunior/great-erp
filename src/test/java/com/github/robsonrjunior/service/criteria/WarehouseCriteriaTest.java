package com.github.robsonrjunior.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WarehouseCriteriaTest {

    @Test
    void newWarehouseCriteriaHasAllFiltersNullTest() {
        var warehouseCriteria = new WarehouseCriteria();
        assertThat(warehouseCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void warehouseCriteriaFluentMethodsCreatesFiltersTest() {
        var warehouseCriteria = new WarehouseCriteria();

        setAllFilters(warehouseCriteria);

        assertThat(warehouseCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void warehouseCriteriaCopyCreatesNullFilterTest() {
        var warehouseCriteria = new WarehouseCriteria();
        var copy = warehouseCriteria.copy();

        assertThat(warehouseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(warehouseCriteria)
        );
    }

    @Test
    void warehouseCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var warehouseCriteria = new WarehouseCriteria();
        setAllFilters(warehouseCriteria);

        var copy = warehouseCriteria.copy();

        assertThat(warehouseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(warehouseCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var warehouseCriteria = new WarehouseCriteria();

        assertThat(warehouseCriteria).hasToString("WarehouseCriteria{}");
    }

    private static void setAllFilters(WarehouseCriteria warehouseCriteria) {
        warehouseCriteria.id();
        warehouseCriteria.name();
        warehouseCriteria.code();
        warehouseCriteria.active();
        warehouseCriteria.deletedAt();
        warehouseCriteria.stockMovementsId();
        warehouseCriteria.salesId();
        warehouseCriteria.tenantId();
        warehouseCriteria.cityId();
        warehouseCriteria.distinct();
    }

    private static Condition<WarehouseCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getStockMovementsId()) &&
                condition.apply(criteria.getSalesId()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getCityId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WarehouseCriteria> copyFiltersAre(WarehouseCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getStockMovementsId(), copy.getStockMovementsId()) &&
                condition.apply(criteria.getSalesId(), copy.getSalesId()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getCityId(), copy.getCityId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
