package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.CompanyTestSamples.*;
import static com.github.robsonrjunior.domain.CustomerTestSamples.*;
import static com.github.robsonrjunior.domain.PersonTestSamples.*;
import static com.github.robsonrjunior.domain.ProductTestSamples.*;
import static com.github.robsonrjunior.domain.RawMaterialTestSamples.*;
import static com.github.robsonrjunior.domain.SaleItemTestSamples.*;
import static com.github.robsonrjunior.domain.SaleTestSamples.*;
import static com.github.robsonrjunior.domain.StockMovementTestSamples.*;
import static com.github.robsonrjunior.domain.SupplierTestSamples.*;
import static com.github.robsonrjunior.domain.TenantTestSamples.*;
import static com.github.robsonrjunior.domain.WarehouseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TenantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tenant.class);
        Tenant tenant1 = getTenantSample1();
        Tenant tenant2 = new Tenant();
        assertThat(tenant1).isNotEqualTo(tenant2);

        tenant2.setId(tenant1.getId());
        assertThat(tenant1).isEqualTo(tenant2);

        tenant2 = getTenantSample2();
        assertThat(tenant1).isNotEqualTo(tenant2);
    }

    @Test
    void customersTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        tenant.setCustomers(customerBack);
        assertThat(tenant.getCustomers()).isEqualTo(customerBack);

        tenant.customers(null);
        assertThat(tenant.getCustomers()).isNull();
    }

    @Test
    void suppliersTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        tenant.setSuppliers(supplierBack);
        assertThat(tenant.getSuppliers()).isEqualTo(supplierBack);

        tenant.suppliers(null);
        assertThat(tenant.getSuppliers()).isNull();
    }

    @Test
    void peopleTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        Person personBack = getPersonRandomSampleGenerator();

        tenant.setPeople(personBack);
        assertThat(tenant.getPeople()).isEqualTo(personBack);

        tenant.people(null);
        assertThat(tenant.getPeople()).isNull();
    }

    @Test
    void companiesTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        tenant.setCompanies(companyBack);
        assertThat(tenant.getCompanies()).isEqualTo(companyBack);

        tenant.companies(null);
        assertThat(tenant.getCompanies()).isNull();
    }

    @Test
    void productsTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        tenant.setProducts(productBack);
        assertThat(tenant.getProducts()).isEqualTo(productBack);

        tenant.products(null);
        assertThat(tenant.getProducts()).isNull();
    }

    @Test
    void rawMaterialsTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        RawMaterial rawMaterialBack = getRawMaterialRandomSampleGenerator();

        tenant.setRawMaterials(rawMaterialBack);
        assertThat(tenant.getRawMaterials()).isEqualTo(rawMaterialBack);

        tenant.rawMaterials(null);
        assertThat(tenant.getRawMaterials()).isNull();
    }

    @Test
    void warehousesTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        Warehouse warehouseBack = getWarehouseRandomSampleGenerator();

        tenant.setWarehouses(warehouseBack);
        assertThat(tenant.getWarehouses()).isEqualTo(warehouseBack);

        tenant.warehouses(null);
        assertThat(tenant.getWarehouses()).isNull();
    }

    @Test
    void salesTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        tenant.setSales(saleBack);
        assertThat(tenant.getSales()).isEqualTo(saleBack);

        tenant.sales(null);
        assertThat(tenant.getSales()).isNull();
    }

    @Test
    void saleItemsTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        SaleItem saleItemBack = getSaleItemRandomSampleGenerator();

        tenant.setSaleItems(saleItemBack);
        assertThat(tenant.getSaleItems()).isEqualTo(saleItemBack);

        tenant.saleItems(null);
        assertThat(tenant.getSaleItems()).isNull();
    }

    @Test
    void stockMovementsTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        StockMovement stockMovementBack = getStockMovementRandomSampleGenerator();

        tenant.setStockMovements(stockMovementBack);
        assertThat(tenant.getStockMovements()).isEqualTo(stockMovementBack);

        tenant.stockMovements(null);
        assertThat(tenant.getStockMovements()).isNull();
    }
}
