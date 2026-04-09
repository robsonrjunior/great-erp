package com.github.robsonrjunior.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StockMovementCriteriaTest {

    @Test
    void newStockMovementCriteriaHasAllFiltersNullTest() {
        var stockMovementCriteria = new StockMovementCriteria();
        assertThat(stockMovementCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void stockMovementCriteriaFluentMethodsCreatesFiltersTest() {
        var stockMovementCriteria = new StockMovementCriteria();

        setAllFilters(stockMovementCriteria);

        assertThat(stockMovementCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void stockMovementCriteriaCopyCreatesNullFilterTest() {
        var stockMovementCriteria = new StockMovementCriteria();
        var copy = stockMovementCriteria.copy();

        assertThat(stockMovementCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(stockMovementCriteria)
        );
    }

    @Test
    void stockMovementCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var stockMovementCriteria = new StockMovementCriteria();
        setAllFilters(stockMovementCriteria);

        var copy = stockMovementCriteria.copy();

        assertThat(stockMovementCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(stockMovementCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var stockMovementCriteria = new StockMovementCriteria();

        assertThat(stockMovementCriteria).hasToString("StockMovementCriteria{}");
    }

    private static void setAllFilters(StockMovementCriteria stockMovementCriteria) {
        stockMovementCriteria.id();
        stockMovementCriteria.movementDate();
        stockMovementCriteria.movementType();
        stockMovementCriteria.quantity();
        stockMovementCriteria.unitCost();
        stockMovementCriteria.referenceNumber();
        stockMovementCriteria.notes();
        stockMovementCriteria.deletedAt();
        stockMovementCriteria.tenantId();
        stockMovementCriteria.warehouseId();
        stockMovementCriteria.productId();
        stockMovementCriteria.rawMaterialId();
        stockMovementCriteria.distinct();
    }

    private static Condition<StockMovementCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMovementDate()) &&
                condition.apply(criteria.getMovementType()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getUnitCost()) &&
                condition.apply(criteria.getReferenceNumber()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getWarehouseId()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getRawMaterialId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StockMovementCriteria> copyFiltersAre(
        StockMovementCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMovementDate(), copy.getMovementDate()) &&
                condition.apply(criteria.getMovementType(), copy.getMovementType()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getUnitCost(), copy.getUnitCost()) &&
                condition.apply(criteria.getReferenceNumber(), copy.getReferenceNumber()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getWarehouseId(), copy.getWarehouseId()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getRawMaterialId(), copy.getRawMaterialId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
