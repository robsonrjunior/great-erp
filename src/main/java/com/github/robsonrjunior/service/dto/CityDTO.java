package com.github.robsonrjunior.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.github.robsonrjunior.domain.City} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CityDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    private SupplierDTO suppliers;

    private CustomerDTO customers;

    private PersonDTO people;

    private CompanyDTO companies;

    private WarehouseDTO warehouses;

    @NotNull
    private StateDTO state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SupplierDTO getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(SupplierDTO suppliers) {
        this.suppliers = suppliers;
    }

    public CustomerDTO getCustomers() {
        return customers;
    }

    public void setCustomers(CustomerDTO customers) {
        this.customers = customers;
    }

    public PersonDTO getPeople() {
        return people;
    }

    public void setPeople(PersonDTO people) {
        this.people = people;
    }

    public CompanyDTO getCompanies() {
        return companies;
    }

    public void setCompanies(CompanyDTO companies) {
        this.companies = companies;
    }

    public WarehouseDTO getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(WarehouseDTO warehouses) {
        this.warehouses = warehouses;
    }

    public StateDTO getState() {
        return state;
    }

    public void setState(StateDTO state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CityDTO)) {
            return false;
        }

        CityDTO cityDTO = (CityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CityDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", suppliers=" + getSuppliers() +
            ", customers=" + getCustomers() +
            ", people=" + getPeople() +
            ", companies=" + getCompanies() +
            ", warehouses=" + getWarehouses() +
            ", state=" + getState() +
            "}";
    }
}
