package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.ProductTestSamples.*;
import static com.github.robsonrjunior.domain.SaleItemTestSamples.*;
import static com.github.robsonrjunior.domain.SaleTestSamples.*;
import static com.github.robsonrjunior.domain.TenantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SaleItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleItem.class);
        SaleItem saleItem1 = getSaleItemSample1();
        SaleItem saleItem2 = new SaleItem();
        assertThat(saleItem1).isNotEqualTo(saleItem2);

        saleItem2.setId(saleItem1.getId());
        assertThat(saleItem1).isEqualTo(saleItem2);

        saleItem2 = getSaleItemSample2();
        assertThat(saleItem1).isNotEqualTo(saleItem2);
    }

    @Test
    void tenantTest() {
        SaleItem saleItem = getSaleItemRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        saleItem.addTenant(tenantBack);
        assertThat(saleItem.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getSaleItems()).isEqualTo(saleItem);

        saleItem.removeTenant(tenantBack);
        assertThat(saleItem.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getSaleItems()).isNull();

        saleItem.tenants(new HashSet<>(Set.of(tenantBack)));
        assertThat(saleItem.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getSaleItems()).isEqualTo(saleItem);

        saleItem.setTenants(new HashSet<>());
        assertThat(saleItem.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getSaleItems()).isNull();
    }

    @Test
    void saleTest() {
        SaleItem saleItem = getSaleItemRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        saleItem.addSale(saleBack);
        assertThat(saleItem.getSales()).containsOnly(saleBack);
        assertThat(saleBack.getItems()).isEqualTo(saleItem);

        saleItem.removeSale(saleBack);
        assertThat(saleItem.getSales()).doesNotContain(saleBack);
        assertThat(saleBack.getItems()).isNull();

        saleItem.sales(new HashSet<>(Set.of(saleBack)));
        assertThat(saleItem.getSales()).containsOnly(saleBack);
        assertThat(saleBack.getItems()).isEqualTo(saleItem);

        saleItem.setSales(new HashSet<>());
        assertThat(saleItem.getSales()).doesNotContain(saleBack);
        assertThat(saleBack.getItems()).isNull();
    }

    @Test
    void productTest() {
        SaleItem saleItem = getSaleItemRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        saleItem.addProduct(productBack);
        assertThat(saleItem.getProducts()).containsOnly(productBack);
        assertThat(productBack.getSaleItems()).isEqualTo(saleItem);

        saleItem.removeProduct(productBack);
        assertThat(saleItem.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getSaleItems()).isNull();

        saleItem.products(new HashSet<>(Set.of(productBack)));
        assertThat(saleItem.getProducts()).containsOnly(productBack);
        assertThat(productBack.getSaleItems()).isEqualTo(saleItem);

        saleItem.setProducts(new HashSet<>());
        assertThat(saleItem.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getSaleItems()).isNull();
    }
}
