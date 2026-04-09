package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.CityTestSamples.*;
import static com.github.robsonrjunior.domain.CompanyTestSamples.*;
import static com.github.robsonrjunior.domain.PersonTestSamples.*;
import static com.github.robsonrjunior.domain.RawMaterialTestSamples.*;
import static com.github.robsonrjunior.domain.SupplierTestSamples.*;
import static com.github.robsonrjunior.domain.TenantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SupplierTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Supplier.class);
        Supplier supplier1 = getSupplierSample1();
        Supplier supplier2 = new Supplier();
        assertThat(supplier1).isNotEqualTo(supplier2);

        supplier2.setId(supplier1.getId());
        assertThat(supplier1).isEqualTo(supplier2);

        supplier2 = getSupplierSample2();
        assertThat(supplier1).isNotEqualTo(supplier2);
    }

    @Test
    void personTest() {
        Supplier supplier = getSupplierRandomSampleGenerator();
        Person personBack = getPersonRandomSampleGenerator();

        supplier.setPerson(personBack);
        assertThat(supplier.getPerson()).isEqualTo(personBack);

        supplier.person(null);
        assertThat(supplier.getPerson()).isNull();
    }

    @Test
    void companyTest() {
        Supplier supplier = getSupplierRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        supplier.setCompany(companyBack);
        assertThat(supplier.getCompany()).isEqualTo(companyBack);

        supplier.company(null);
        assertThat(supplier.getCompany()).isNull();
    }

    @Test
    void rawMaterialsTest() {
        Supplier supplier = getSupplierRandomSampleGenerator();
        RawMaterial rawMaterialBack = getRawMaterialRandomSampleGenerator();

        supplier.setRawMaterials(rawMaterialBack);
        assertThat(supplier.getRawMaterials()).isEqualTo(rawMaterialBack);

        supplier.rawMaterials(null);
        assertThat(supplier.getRawMaterials()).isNull();
    }

    @Test
    void tenantTest() {
        Supplier supplier = getSupplierRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        supplier.addTenant(tenantBack);
        assertThat(supplier.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getSuppliers()).isEqualTo(supplier);

        supplier.removeTenant(tenantBack);
        assertThat(supplier.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getSuppliers()).isNull();

        supplier.tenants(new HashSet<>(Set.of(tenantBack)));
        assertThat(supplier.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getSuppliers()).isEqualTo(supplier);

        supplier.setTenants(new HashSet<>());
        assertThat(supplier.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getSuppliers()).isNull();
    }

    @Test
    void cityTest() {
        Supplier supplier = getSupplierRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        supplier.addCity(cityBack);
        assertThat(supplier.getCities()).containsOnly(cityBack);
        assertThat(cityBack.getSuppliers()).isEqualTo(supplier);

        supplier.removeCity(cityBack);
        assertThat(supplier.getCities()).doesNotContain(cityBack);
        assertThat(cityBack.getSuppliers()).isNull();

        supplier.cities(new HashSet<>(Set.of(cityBack)));
        assertThat(supplier.getCities()).containsOnly(cityBack);
        assertThat(cityBack.getSuppliers()).isEqualTo(supplier);

        supplier.setCities(new HashSet<>());
        assertThat(supplier.getCities()).doesNotContain(cityBack);
        assertThat(cityBack.getSuppliers()).isNull();
    }
}
