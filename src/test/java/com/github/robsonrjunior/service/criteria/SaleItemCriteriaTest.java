package com.github.robsonrjunior.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SaleItemCriteriaTest {

    @Test
    void newSaleItemCriteriaHasAllFiltersNullTest() {
        var saleItemCriteria = new SaleItemCriteria();
        assertThat(saleItemCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void saleItemCriteriaFluentMethodsCreatesFiltersTest() {
        var saleItemCriteria = new SaleItemCriteria();

        setAllFilters(saleItemCriteria);

        assertThat(saleItemCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void saleItemCriteriaCopyCreatesNullFilterTest() {
        var saleItemCriteria = new SaleItemCriteria();
        var copy = saleItemCriteria.copy();

        assertThat(saleItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(saleItemCriteria)
        );
    }

    @Test
    void saleItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var saleItemCriteria = new SaleItemCriteria();
        setAllFilters(saleItemCriteria);

        var copy = saleItemCriteria.copy();

        assertThat(saleItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(saleItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var saleItemCriteria = new SaleItemCriteria();

        assertThat(saleItemCriteria).hasToString("SaleItemCriteria{}");
    }

    private static void setAllFilters(SaleItemCriteria saleItemCriteria) {
        saleItemCriteria.id();
        saleItemCriteria.quantity();
        saleItemCriteria.unitPrice();
        saleItemCriteria.discountAmount();
        saleItemCriteria.lineTotal();
        saleItemCriteria.deletedAt();
        saleItemCriteria.tenantId();
        saleItemCriteria.saleId();
        saleItemCriteria.productId();
        saleItemCriteria.distinct();
    }

    private static Condition<SaleItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getUnitPrice()) &&
                condition.apply(criteria.getDiscountAmount()) &&
                condition.apply(criteria.getLineTotal()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getSaleId()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SaleItemCriteria> copyFiltersAre(SaleItemCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getUnitPrice(), copy.getUnitPrice()) &&
                condition.apply(criteria.getDiscountAmount(), copy.getDiscountAmount()) &&
                condition.apply(criteria.getLineTotal(), copy.getLineTotal()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getSaleId(), copy.getSaleId()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
