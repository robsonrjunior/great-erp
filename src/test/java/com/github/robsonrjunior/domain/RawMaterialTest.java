package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.RawMaterialTestSamples.*;
import static com.github.robsonrjunior.domain.StockMovementTestSamples.*;
import static com.github.robsonrjunior.domain.SupplierTestSamples.*;
import static com.github.robsonrjunior.domain.TenantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RawMaterialTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RawMaterial.class);
        RawMaterial rawMaterial1 = getRawMaterialSample1();
        RawMaterial rawMaterial2 = new RawMaterial();
        assertThat(rawMaterial1).isNotEqualTo(rawMaterial2);

        rawMaterial2.setId(rawMaterial1.getId());
        assertThat(rawMaterial1).isEqualTo(rawMaterial2);

        rawMaterial2 = getRawMaterialSample2();
        assertThat(rawMaterial1).isNotEqualTo(rawMaterial2);
    }

    @Test
    void stockMovementsTest() {
        RawMaterial rawMaterial = getRawMaterialRandomSampleGenerator();
        StockMovement stockMovementBack = getStockMovementRandomSampleGenerator();

        rawMaterial.setStockMovements(stockMovementBack);
        assertThat(rawMaterial.getStockMovements()).isEqualTo(stockMovementBack);

        rawMaterial.stockMovements(null);
        assertThat(rawMaterial.getStockMovements()).isNull();
    }

    @Test
    void tenantTest() {
        RawMaterial rawMaterial = getRawMaterialRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        rawMaterial.addTenant(tenantBack);
        assertThat(rawMaterial.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getRawMaterials()).isEqualTo(rawMaterial);

        rawMaterial.removeTenant(tenantBack);
        assertThat(rawMaterial.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getRawMaterials()).isNull();

        rawMaterial.tenants(new HashSet<>(Set.of(tenantBack)));
        assertThat(rawMaterial.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getRawMaterials()).isEqualTo(rawMaterial);

        rawMaterial.setTenants(new HashSet<>());
        assertThat(rawMaterial.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getRawMaterials()).isNull();
    }

    @Test
    void primarySupplierTest() {
        RawMaterial rawMaterial = getRawMaterialRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        rawMaterial.addPrimarySupplier(supplierBack);
        assertThat(rawMaterial.getPrimarySuppliers()).containsOnly(supplierBack);
        assertThat(supplierBack.getRawMaterials()).isEqualTo(rawMaterial);

        rawMaterial.removePrimarySupplier(supplierBack);
        assertThat(rawMaterial.getPrimarySuppliers()).doesNotContain(supplierBack);
        assertThat(supplierBack.getRawMaterials()).isNull();

        rawMaterial.primarySuppliers(new HashSet<>(Set.of(supplierBack)));
        assertThat(rawMaterial.getPrimarySuppliers()).containsOnly(supplierBack);
        assertThat(supplierBack.getRawMaterials()).isEqualTo(rawMaterial);

        rawMaterial.setPrimarySuppliers(new HashSet<>());
        assertThat(rawMaterial.getPrimarySuppliers()).doesNotContain(supplierBack);
        assertThat(supplierBack.getRawMaterials()).isNull();
    }
}
