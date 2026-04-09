package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.CityTestSamples.*;
import static com.github.robsonrjunior.domain.CompanyTestSamples.*;
import static com.github.robsonrjunior.domain.CustomerTestSamples.*;
import static com.github.robsonrjunior.domain.PersonTestSamples.*;
import static com.github.robsonrjunior.domain.StateTestSamples.*;
import static com.github.robsonrjunior.domain.SupplierTestSamples.*;
import static com.github.robsonrjunior.domain.WarehouseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(City.class);
        City city1 = getCitySample1();
        City city2 = new City();
        assertThat(city1).isNotEqualTo(city2);

        city2.setId(city1.getId());
        assertThat(city1).isEqualTo(city2);

        city2 = getCitySample2();
        assertThat(city1).isNotEqualTo(city2);
    }

    @Test
    void suppliersTest() {
        City city = getCityRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        city.setSuppliers(supplierBack);
        assertThat(city.getSuppliers()).isEqualTo(supplierBack);

        city.suppliers(null);
        assertThat(city.getSuppliers()).isNull();
    }

    @Test
    void customersTest() {
        City city = getCityRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        city.setCustomers(customerBack);
        assertThat(city.getCustomers()).isEqualTo(customerBack);

        city.customers(null);
        assertThat(city.getCustomers()).isNull();
    }

    @Test
    void peopleTest() {
        City city = getCityRandomSampleGenerator();
        Person personBack = getPersonRandomSampleGenerator();

        city.setPeople(personBack);
        assertThat(city.getPeople()).isEqualTo(personBack);

        city.people(null);
        assertThat(city.getPeople()).isNull();
    }

    @Test
    void companiesTest() {
        City city = getCityRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        city.setCompanies(companyBack);
        assertThat(city.getCompanies()).isEqualTo(companyBack);

        city.companies(null);
        assertThat(city.getCompanies()).isNull();
    }

    @Test
    void warehousesTest() {
        City city = getCityRandomSampleGenerator();
        Warehouse warehouseBack = getWarehouseRandomSampleGenerator();

        city.setWarehouses(warehouseBack);
        assertThat(city.getWarehouses()).isEqualTo(warehouseBack);

        city.warehouses(null);
        assertThat(city.getWarehouses()).isNull();
    }

    @Test
    void stateTest() {
        City city = getCityRandomSampleGenerator();
        State stateBack = getStateRandomSampleGenerator();

        city.setState(stateBack);
        assertThat(city.getState()).isEqualTo(stateBack);

        city.state(null);
        assertThat(city.getState()).isNull();
    }
}
