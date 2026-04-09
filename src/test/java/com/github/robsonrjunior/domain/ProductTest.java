package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.ProductTestSamples.*;
import static com.github.robsonrjunior.domain.SaleItemTestSamples.*;
import static com.github.robsonrjunior.domain.StockMovementTestSamples.*;
import static com.github.robsonrjunior.domain.TenantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void saleItemsTest() {
        Product product = getProductRandomSampleGenerator();
        SaleItem saleItemBack = getSaleItemRandomSampleGenerator();

        product.setSaleItems(saleItemBack);
        assertThat(product.getSaleItems()).isEqualTo(saleItemBack);

        product.saleItems(null);
        assertThat(product.getSaleItems()).isNull();
    }

    @Test
    void stockMovementsTest() {
        Product product = getProductRandomSampleGenerator();
        StockMovement stockMovementBack = getStockMovementRandomSampleGenerator();

        product.setStockMovements(stockMovementBack);
        assertThat(product.getStockMovements()).isEqualTo(stockMovementBack);

        product.stockMovements(null);
        assertThat(product.getStockMovements()).isNull();
    }

    @Test
    void tenantTest() {
        Product product = getProductRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        product.addTenant(tenantBack);
        assertThat(product.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getProducts()).isEqualTo(product);

        product.removeTenant(tenantBack);
        assertThat(product.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getProducts()).isNull();

        product.tenants(new HashSet<>(Set.of(tenantBack)));
        assertThat(product.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getProducts()).isEqualTo(product);

        product.setTenants(new HashSet<>());
        assertThat(product.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getProducts()).isNull();
    }
}
