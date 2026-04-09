package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.CityTestSamples.*;
import static com.github.robsonrjunior.domain.SaleTestSamples.*;
import static com.github.robsonrjunior.domain.StockMovementTestSamples.*;
import static com.github.robsonrjunior.domain.TenantTestSamples.*;
import static com.github.robsonrjunior.domain.WarehouseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WarehouseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Warehouse.class);
        Warehouse warehouse1 = getWarehouseSample1();
        Warehouse warehouse2 = new Warehouse();
        assertThat(warehouse1).isNotEqualTo(warehouse2);

        warehouse2.setId(warehouse1.getId());
        assertThat(warehouse1).isEqualTo(warehouse2);

        warehouse2 = getWarehouseSample2();
        assertThat(warehouse1).isNotEqualTo(warehouse2);
    }

    @Test
    void stockMovementsTest() {
        Warehouse warehouse = getWarehouseRandomSampleGenerator();
        StockMovement stockMovementBack = getStockMovementRandomSampleGenerator();

        warehouse.setStockMovements(stockMovementBack);
        assertThat(warehouse.getStockMovements()).isEqualTo(stockMovementBack);

        warehouse.stockMovements(null);
        assertThat(warehouse.getStockMovements()).isNull();
    }

    @Test
    void salesTest() {
        Warehouse warehouse = getWarehouseRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        warehouse.setSales(saleBack);
        assertThat(warehouse.getSales()).isEqualTo(saleBack);

        warehouse.sales(null);
        assertThat(warehouse.getSales()).isNull();
    }

    @Test
    void tenantTest() {
        Warehouse warehouse = getWarehouseRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        warehouse.addTenant(tenantBack);
        assertThat(warehouse.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getWarehouses()).isEqualTo(warehouse);

        warehouse.removeTenant(tenantBack);
        assertThat(warehouse.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getWarehouses()).isNull();

        warehouse.tenants(new HashSet<>(Set.of(tenantBack)));
        assertThat(warehouse.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getWarehouses()).isEqualTo(warehouse);

        warehouse.setTenants(new HashSet<>());
        assertThat(warehouse.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getWarehouses()).isNull();
    }

    @Test
    void cityTest() {
        Warehouse warehouse = getWarehouseRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        warehouse.addCity(cityBack);
        assertThat(warehouse.getCities()).containsOnly(cityBack);
        assertThat(cityBack.getWarehouses()).isEqualTo(warehouse);

        warehouse.removeCity(cityBack);
        assertThat(warehouse.getCities()).doesNotContain(cityBack);
        assertThat(cityBack.getWarehouses()).isNull();

        warehouse.cities(new HashSet<>(Set.of(cityBack)));
        assertThat(warehouse.getCities()).containsOnly(cityBack);
        assertThat(cityBack.getWarehouses()).isEqualTo(warehouse);

        warehouse.setCities(new HashSet<>());
        assertThat(warehouse.getCities()).doesNotContain(cityBack);
        assertThat(cityBack.getWarehouses()).isNull();
    }
}
