package com.github.robsonrjunior.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TenantCriteriaTest {

    @Test
    void newTenantCriteriaHasAllFiltersNullTest() {
        var tenantCriteria = new TenantCriteria();
        assertThat(tenantCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tenantCriteriaFluentMethodsCreatesFiltersTest() {
        var tenantCriteria = new TenantCriteria();

        setAllFilters(tenantCriteria);

        assertThat(tenantCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tenantCriteriaCopyCreatesNullFilterTest() {
        var tenantCriteria = new TenantCriteria();
        var copy = tenantCriteria.copy();

        assertThat(tenantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tenantCriteria)
        );
    }

    @Test
    void tenantCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tenantCriteria = new TenantCriteria();
        setAllFilters(tenantCriteria);

        var copy = tenantCriteria.copy();

        assertThat(tenantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tenantCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tenantCriteria = new TenantCriteria();

        assertThat(tenantCriteria).hasToString("TenantCriteria{}");
    }

    private static void setAllFilters(TenantCriteria tenantCriteria) {
        tenantCriteria.id();
        tenantCriteria.name();
        tenantCriteria.code();
        tenantCriteria.active();
        tenantCriteria.deletedAt();
        tenantCriteria.customersId();
        tenantCriteria.suppliersId();
        tenantCriteria.peopleId();
        tenantCriteria.companiesId();
        tenantCriteria.productsId();
        tenantCriteria.rawMaterialsId();
        tenantCriteria.warehousesId();
        tenantCriteria.salesId();
        tenantCriteria.saleItemsId();
        tenantCriteria.stockMovementsId();
        tenantCriteria.distinct();
    }

    private static Condition<TenantCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getCustomersId()) &&
                condition.apply(criteria.getSuppliersId()) &&
                condition.apply(criteria.getPeopleId()) &&
                condition.apply(criteria.getCompaniesId()) &&
                condition.apply(criteria.getProductsId()) &&
                condition.apply(criteria.getRawMaterialsId()) &&
                condition.apply(criteria.getWarehousesId()) &&
                condition.apply(criteria.getSalesId()) &&
                condition.apply(criteria.getSaleItemsId()) &&
                condition.apply(criteria.getStockMovementsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TenantCriteria> copyFiltersAre(TenantCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getCustomersId(), copy.getCustomersId()) &&
                condition.apply(criteria.getSuppliersId(), copy.getSuppliersId()) &&
                condition.apply(criteria.getPeopleId(), copy.getPeopleId()) &&
                condition.apply(criteria.getCompaniesId(), copy.getCompaniesId()) &&
                condition.apply(criteria.getProductsId(), copy.getProductsId()) &&
                condition.apply(criteria.getRawMaterialsId(), copy.getRawMaterialsId()) &&
                condition.apply(criteria.getWarehousesId(), copy.getWarehousesId()) &&
                condition.apply(criteria.getSalesId(), copy.getSalesId()) &&
                condition.apply(criteria.getSaleItemsId(), copy.getSaleItemsId()) &&
                condition.apply(criteria.getStockMovementsId(), copy.getStockMovementsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
