package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.CustomerTestSamples.*;
import static com.github.robsonrjunior.domain.SaleItemTestSamples.*;
import static com.github.robsonrjunior.domain.SaleTestSamples.*;
import static com.github.robsonrjunior.domain.TenantTestSamples.*;
import static com.github.robsonrjunior.domain.WarehouseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SaleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sale.class);
        Sale sale1 = getSaleSample1();
        Sale sale2 = new Sale();
        assertThat(sale1).isNotEqualTo(sale2);

        sale2.setId(sale1.getId());
        assertThat(sale1).isEqualTo(sale2);

        sale2 = getSaleSample2();
        assertThat(sale1).isNotEqualTo(sale2);
    }

    @Test
    void itemsTest() {
        Sale sale = getSaleRandomSampleGenerator();
        SaleItem saleItemBack = getSaleItemRandomSampleGenerator();

        sale.setItems(saleItemBack);
        assertThat(sale.getItems()).isEqualTo(saleItemBack);

        sale.items(null);
        assertThat(sale.getItems()).isNull();
    }

    @Test
    void tenantTest() {
        Sale sale = getSaleRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        sale.addTenant(tenantBack);
        assertThat(sale.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getSales()).isEqualTo(sale);

        sale.removeTenant(tenantBack);
        assertThat(sale.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getSales()).isNull();

        sale.tenants(new HashSet<>(Set.of(tenantBack)));
        assertThat(sale.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getSales()).isEqualTo(sale);

        sale.setTenants(new HashSet<>());
        assertThat(sale.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getSales()).isNull();
    }

    @Test
    void warehouseTest() {
        Sale sale = getSaleRandomSampleGenerator();
        Warehouse warehouseBack = getWarehouseRandomSampleGenerator();

        sale.addWarehouse(warehouseBack);
        assertThat(sale.getWarehouses()).containsOnly(warehouseBack);
        assertThat(warehouseBack.getSales()).isEqualTo(sale);

        sale.removeWarehouse(warehouseBack);
        assertThat(sale.getWarehouses()).doesNotContain(warehouseBack);
        assertThat(warehouseBack.getSales()).isNull();

        sale.warehouses(new HashSet<>(Set.of(warehouseBack)));
        assertThat(sale.getWarehouses()).containsOnly(warehouseBack);
        assertThat(warehouseBack.getSales()).isEqualTo(sale);

        sale.setWarehouses(new HashSet<>());
        assertThat(sale.getWarehouses()).doesNotContain(warehouseBack);
        assertThat(warehouseBack.getSales()).isNull();
    }

    @Test
    void customerTest() {
        Sale sale = getSaleRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        sale.addCustomer(customerBack);
        assertThat(sale.getCustomers()).containsOnly(customerBack);
        assertThat(customerBack.getSales()).isEqualTo(sale);

        sale.removeCustomer(customerBack);
        assertThat(sale.getCustomers()).doesNotContain(customerBack);
        assertThat(customerBack.getSales()).isNull();

        sale.customers(new HashSet<>(Set.of(customerBack)));
        assertThat(sale.getCustomers()).containsOnly(customerBack);
        assertThat(customerBack.getSales()).isEqualTo(sale);

        sale.setCustomers(new HashSet<>());
        assertThat(sale.getCustomers()).doesNotContain(customerBack);
        assertThat(customerBack.getSales()).isNull();
    }
}
