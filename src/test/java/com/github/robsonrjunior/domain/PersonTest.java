package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.CityTestSamples.*;
import static com.github.robsonrjunior.domain.CustomerTestSamples.*;
import static com.github.robsonrjunior.domain.PersonTestSamples.*;
import static com.github.robsonrjunior.domain.SupplierTestSamples.*;
import static com.github.robsonrjunior.domain.TenantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PersonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Person.class);
        Person person1 = getPersonSample1();
        Person person2 = new Person();
        assertThat(person1).isNotEqualTo(person2);

        person2.setId(person1.getId());
        assertThat(person1).isEqualTo(person2);

        person2 = getPersonSample2();
        assertThat(person1).isNotEqualTo(person2);
    }

    @Test
    void customerTest() {
        Person person = getPersonRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        person.setCustomer(customerBack);
        assertThat(person.getCustomer()).isEqualTo(customerBack);
        assertThat(customerBack.getPerson()).isEqualTo(person);

        person.customer(null);
        assertThat(person.getCustomer()).isNull();
        assertThat(customerBack.getPerson()).isNull();
    }

    @Test
    void supplierTest() {
        Person person = getPersonRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        person.setSupplier(supplierBack);
        assertThat(person.getSupplier()).isEqualTo(supplierBack);
        assertThat(supplierBack.getPerson()).isEqualTo(person);

        person.supplier(null);
        assertThat(person.getSupplier()).isNull();
        assertThat(supplierBack.getPerson()).isNull();
    }

    @Test
    void tenantTest() {
        Person person = getPersonRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        person.addTenant(tenantBack);
        assertThat(person.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getPeople()).isEqualTo(person);

        person.removeTenant(tenantBack);
        assertThat(person.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getPeople()).isNull();

        person.tenants(new HashSet<>(Set.of(tenantBack)));
        assertThat(person.getTenants()).containsOnly(tenantBack);
        assertThat(tenantBack.getPeople()).isEqualTo(person);

        person.setTenants(new HashSet<>());
        assertThat(person.getTenants()).doesNotContain(tenantBack);
        assertThat(tenantBack.getPeople()).isNull();
    }

    @Test
    void cityTest() {
        Person person = getPersonRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        person.addCity(cityBack);
        assertThat(person.getCities()).containsOnly(cityBack);
        assertThat(cityBack.getPeople()).isEqualTo(person);

        person.removeCity(cityBack);
        assertThat(person.getCities()).doesNotContain(cityBack);
        assertThat(cityBack.getPeople()).isNull();

        person.cities(new HashSet<>(Set.of(cityBack)));
        assertThat(person.getCities()).containsOnly(cityBack);
        assertThat(cityBack.getPeople()).isEqualTo(person);

        person.setCities(new HashSet<>());
        assertThat(person.getCities()).doesNotContain(cityBack);
        assertThat(cityBack.getPeople()).isNull();
    }
}
