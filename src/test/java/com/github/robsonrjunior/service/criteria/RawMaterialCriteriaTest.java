package com.github.robsonrjunior.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RawMaterialCriteriaTest {

    @Test
    void newRawMaterialCriteriaHasAllFiltersNullTest() {
        var rawMaterialCriteria = new RawMaterialCriteria();
        assertThat(rawMaterialCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void rawMaterialCriteriaFluentMethodsCreatesFiltersTest() {
        var rawMaterialCriteria = new RawMaterialCriteria();

        setAllFilters(rawMaterialCriteria);

        assertThat(rawMaterialCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void rawMaterialCriteriaCopyCreatesNullFilterTest() {
        var rawMaterialCriteria = new RawMaterialCriteria();
        var copy = rawMaterialCriteria.copy();

        assertThat(rawMaterialCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(rawMaterialCriteria)
        );
    }

    @Test
    void rawMaterialCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var rawMaterialCriteria = new RawMaterialCriteria();
        setAllFilters(rawMaterialCriteria);

        var copy = rawMaterialCriteria.copy();

        assertThat(rawMaterialCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(rawMaterialCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var rawMaterialCriteria = new RawMaterialCriteria();

        assertThat(rawMaterialCriteria).hasToString("RawMaterialCriteria{}");
    }

    private static void setAllFilters(RawMaterialCriteria rawMaterialCriteria) {
        rawMaterialCriteria.id();
        rawMaterialCriteria.name();
        rawMaterialCriteria.sku();
        rawMaterialCriteria.unitOfMeasure();
        rawMaterialCriteria.unitDecimalPlaces();
        rawMaterialCriteria.unitCost();
        rawMaterialCriteria.minStock();
        rawMaterialCriteria.active();
        rawMaterialCriteria.deletedAt();
        rawMaterialCriteria.stockMovementsId();
        rawMaterialCriteria.tenantId();
        rawMaterialCriteria.primarySupplierId();
        rawMaterialCriteria.distinct();
    }

    private static Condition<RawMaterialCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getSku()) &&
                condition.apply(criteria.getUnitOfMeasure()) &&
                condition.apply(criteria.getUnitDecimalPlaces()) &&
                condition.apply(criteria.getUnitCost()) &&
                condition.apply(criteria.getMinStock()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getStockMovementsId()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getPrimarySupplierId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RawMaterialCriteria> copyFiltersAre(RawMaterialCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getSku(), copy.getSku()) &&
                condition.apply(criteria.getUnitOfMeasure(), copy.getUnitOfMeasure()) &&
                condition.apply(criteria.getUnitDecimalPlaces(), copy.getUnitDecimalPlaces()) &&
                condition.apply(criteria.getUnitCost(), copy.getUnitCost()) &&
                condition.apply(criteria.getMinStock(), copy.getMinStock()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getStockMovementsId(), copy.getStockMovementsId()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getPrimarySupplierId(), copy.getPrimarySupplierId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
