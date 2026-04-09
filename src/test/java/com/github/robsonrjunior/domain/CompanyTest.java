package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.CityTestSamples.*;
import static com.github.robsonrjunior.domain.CompanyTestSamples.*;
import static com.github.robsonrjunior.domain.CustomerTestSamples.*;
import static com.github.robsonrjunior.domain.SupplierTestSamples.*;
import static com.github.robsonrjunior.domain.TenantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CompanyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Company.class);
        Company company1 = getCompanySample1();
        Company company2 = new Company();
        assertThat(company1).isNotEqualTo(company2);

        company2.setId(company1.getId());
        assertThat(company1).isEqualTo(company2);

        company2 = getCompanySample2();
        assertThat(company1).isNotEqualTo(company2);
    }

    @Test
    void customerTest() {
        Company company = getCompanyRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        company.setCustomer(customerBack);
        assertThat(company.getCustomer()).isEqualTo(customerBack);
        assertThat(customerBack.getCompany()).isEqualTo(company);

        company.customer(null);
        assertThat(company.getCustomer()).isNull();
        assertThat(customerBack.getCompany()).isNull();
    }

    @Test
    void supplierTest() {
        Company company = getCompanyRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        company.setSupplier(supplierBack);
        assertThat(company.getSupplier()).isEqualTo(supplierBack);
        assertThat(supplierBack.getCompany()).isEqualTo(company);

        company.supplier(null);
        assertThat(company.getSupplier()).isNull();
        assertThat(supplierBack.getCompany()).isNull();
    }

    @Test
    void tenantTest() {
        Company company = getCompanyRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        company.addTenant(tenantBack);
        assertThat(company.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getCompanies()).isEqualTo(company);

        company.removeTenant(tenantBack);
        assertThat(company.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getCompanies()).isNull();

        company.tenants(new HashSet<>(Set.of(tenantBack)));
        assertThat(company.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getCompanies()).isEqualTo(company);

        company.setTenants(new HashSet<>());
        assertThat(company.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getCompanies()).isNull();
    }

    @Test
    void cityTest() {
        Company company = getCompanyRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        company.addCity(cityBack);
        assertThat(company.getCities()).containsOnly(cityBack);
        assertThat(cityBack.getCompanies()).isEqualTo(company);

        company.removeCity(cityBack);
        assertThat(company.getCities()).doesNotContain(cityBack);
        assertThat(cityBack.getCompanies()).isNull();

        company.cities(new HashSet<>(Set.of(cityBack)));
        assertThat(company.getCities()).containsOnly(cityBack);
        assertThat(cityBack.getCompanies()).isEqualTo(company);

        company.setCities(new HashSet<>());
        assertThat(company.getCities()).doesNotContain(cityBack);
        assertThat(cityBack.getCompanies()).isNull();
    }
}
