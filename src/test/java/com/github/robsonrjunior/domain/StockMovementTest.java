package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.ProductTestSamples.*;
import static com.github.robsonrjunior.domain.RawMaterialTestSamples.*;
import static com.github.robsonrjunior.domain.StockMovementTestSamples.*;
import static com.github.robsonrjunior.domain.TenantTestSamples.*;
import static com.github.robsonrjunior.domain.WarehouseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StockMovementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockMovement.class);
        StockMovement stockMovement1 = getStockMovementSample1();
        StockMovement stockMovement2 = new StockMovement();
        assertThat(stockMovement1).isNotEqualTo(stockMovement2);

        stockMovement2.setId(stockMovement1.getId());
        assertThat(stockMovement1).isEqualTo(stockMovement2);

        stockMovement2 = getStockMovementSample2();
        assertThat(stockMovement1).isNotEqualTo(stockMovement2);
    }

    @Test
    void tenantTest() {
        StockMovement stockMovement = getStockMovementRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        stockMovement.addTenant(tenantBack);
        assertThat(stockMovement.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getStockMovements()).isEqualTo(stockMovement);

        stockMovement.removeTenant(tenantBack);
        assertThat(stockMovement.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getStockMovements()).isNull();

        stockMovement.tenants(new HashSet<>(Set.of(tenantBack)));
        assertThat(stockMovement.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getStockMovements()).isEqualTo(stockMovement);

        stockMovement.setTenants(new HashSet<>());
        assertThat(stockMovement.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getStockMovements()).isNull();
    }

    @Test
    void warehouseTest() {
        StockMovement stockMovement = getStockMovementRandomSampleGenerator();
        Warehouse warehouseBack = getWarehouseRandomSampleGenerator();

        stockMovement.addWarehouse(warehouseBack);
        assertThat(stockMovement.getWarehouses()).containsOnly(warehouseBack);
        assertThat(warehouseBack.getStockMovements()).isEqualTo(stockMovement);

        stockMovement.removeWarehouse(warehouseBack);
        assertThat(stockMovement.getWarehouses()).doesNotContain(warehouseBack);
        assertThat(warehouseBack.getStockMovements()).isNull();

        stockMovement.warehouses(new HashSet<>(Set.of(warehouseBack)));
        assertThat(stockMovement.getWarehouses()).containsOnly(warehouseBack);
        assertThat(warehouseBack.getStockMovements()).isEqualTo(stockMovement);

        stockMovement.setWarehouses(new HashSet<>());
        assertThat(stockMovement.getWarehouses()).doesNotContain(warehouseBack);
        assertThat(warehouseBack.getStockMovements()).isNull();
    }

    @Test
    void productTest() {
        StockMovement stockMovement = getStockMovementRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        stockMovement.addProduct(productBack);
        assertThat(stockMovement.getProducts()).containsOnly(productBack);
        assertThat(productBack.getStockMovements()).isEqualTo(stockMovement);

        stockMovement.removeProduct(productBack);
        assertThat(stockMovement.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getStockMovements()).isNull();

        stockMovement.products(new HashSet<>(Set.of(productBack)));
        assertThat(stockMovement.getProducts()).containsOnly(productBack);
        assertThat(productBack.getStockMovements()).isEqualTo(stockMovement);

        stockMovement.setProducts(new HashSet<>());
        assertThat(stockMovement.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getStockMovements()).isNull();
    }

    @Test
    void rawMaterialTest() {
        StockMovement stockMovement = getStockMovementRandomSampleGenerator();
        RawMaterial rawMaterialBack = getRawMaterialRandomSampleGenerator();

        stockMovement.addRawMaterial(rawMaterialBack);
        assertThat(stockMovement.getRawMaterials()).containsOnly(rawMaterialBack);
        assertThat(rawMaterialBack.getStockMovements()).isEqualTo(stockMovement);

        stockMovement.removeRawMaterial(rawMaterialBack);
        assertThat(stockMovement.getRawMaterials()).doesNotContain(rawMaterialBack);
        assertThat(rawMaterialBack.getStockMovements()).isNull();

        stockMovement.rawMaterials(new HashSet<>(Set.of(rawMaterialBack)));
        assertThat(stockMovement.getRawMaterials()).containsOnly(rawMaterialBack);
        assertThat(rawMaterialBack.getStockMovements()).isEqualTo(stockMovement);

        stockMovement.setRawMaterials(new HashSet<>());
        assertThat(stockMovement.getRawMaterials()).doesNotContain(rawMaterialBack);
        assertThat(rawMaterialBack.getStockMovements()).isNull();
    }
}
