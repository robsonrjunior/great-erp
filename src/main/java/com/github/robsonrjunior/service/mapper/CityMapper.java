package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.City;
import com.github.robsonrjunior.domain.Company;
import com.github.robsonrjunior.domain.Customer;
import com.github.robsonrjunior.domain.Person;
import com.github.robsonrjunior.domain.State;
import com.github.robsonrjunior.domain.Supplier;
import com.github.robsonrjunior.domain.Warehouse;
import com.github.robsonrjunior.service.dto.CityDTO;
import com.github.robsonrjunior.service.dto.CompanyDTO;
import com.github.robsonrjunior.service.dto.CustomerDTO;
import com.github.robsonrjunior.service.dto.PersonDTO;
import com.github.robsonrjunior.service.dto.StateDTO;
import com.github.robsonrjunior.service.dto.SupplierDTO;
import com.github.robsonrjunior.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link City} and its DTO {@link CityDTO}.
 */
@Mapper(componentModel = "spring")
public interface CityMapper extends EntityMapper<CityDTO, City> {
    @Mapping(target = "suppliers", source = "suppliers", qualifiedByName = "supplierId")
    @Mapping(target = "customers", source = "customers", qualifiedByName = "customerId")
    @Mapping(target = "people", source = "people", qualifiedByName = "personId")
    @Mapping(target = "companies", source = "companies", qualifiedByName = "companyId")
    @Mapping(target = "warehouses", source = "warehouses", qualifiedByName = "warehouseId")
    @Mapping(target = "state", source = "state", qualifiedByName = "stateId")
    CityDTO toDto(City s);

    @Named("supplierId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SupplierDTO toDtoSupplierId(Supplier supplier);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);

    @Named("personId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonDTO toDtoPersonId(Person person);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("warehouseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WarehouseDTO toDtoWarehouseId(Warehouse warehouse);

    @Named("stateId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StateDTO toDtoStateId(State state);
}
