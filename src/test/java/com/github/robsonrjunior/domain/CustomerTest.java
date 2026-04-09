package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.CityTestSamples.*;
import static com.github.robsonrjunior.domain.CompanyTestSamples.*;
import static com.github.robsonrjunior.domain.CustomerTestSamples.*;
import static com.github.robsonrjunior.domain.PersonTestSamples.*;
import static com.github.robsonrjunior.domain.SaleTestSamples.*;
import static com.github.robsonrjunior.domain.TenantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void personTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Person personBack = getPersonRandomSampleGenerator();

        customer.setPerson(personBack);
        assertThat(customer.getPerson()).isEqualTo(personBack);

        customer.person(null);
        assertThat(customer.getPerson()).isNull();
    }

    @Test
    void companyTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        customer.setCompany(companyBack);
        assertThat(customer.getCompany()).isEqualTo(companyBack);

        customer.company(null);
        assertThat(customer.getCompany()).isNull();
    }

    @Test
    void salesTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        customer.setSales(saleBack);
        assertThat(customer.getSales()).isEqualTo(saleBack);

        customer.sales(null);
        assertThat(customer.getSales()).isNull();
    }

    @Test
    void tenantTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        customer.addTenant(tenantBack);
        assertThat(customer.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getCustomers()).isEqualTo(customer);

        customer.removeTenant(tenantBack);
        assertThat(customer.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getCustomers()).isNull();

        customer.tenants(new HashSet<>(Set.of(tenantBack)));
        assertThat(customer.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getCustomers()).isEqualTo(customer);

        customer.setTenants(new HashSet<>());
        assertThat(customer.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getCustomers()).isNull();
    }

    @Test
    void cityTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        customer.addCity(cityBack);
        assertThat(customer.getCities()).containsOnly(cityBack);
        assertThat(cityBack.getCustomers()).isEqualTo(customer);

        customer.removeCity(cityBack);
        assertThat(customer.getCities()).doesNotContain(cityBack);
        assertThat(cityBack.getCustomers()).isNull();

        customer.cities(new HashSet<>(Set.of(cityBack)));
        assertThat(customer.getCities()).containsOnly(cityBack);
        assertThat(cityBack.getCustomers()).isEqualTo(customer);

        customer.setCities(new HashSet<>());
        assertThat(customer.getCities()).doesNotContain(cityBack);
        assertThat(cityBack.getCustomers()).isNull();
    }
}
