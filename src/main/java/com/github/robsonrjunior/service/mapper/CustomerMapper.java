package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.Company;
import com.github.robsonrjunior.domain.Customer;
import com.github.robsonrjunior.domain.Person;
import com.github.robsonrjunior.domain.Sale;
import com.github.robsonrjunior.service.dto.CompanyDTO;
import com.github.robsonrjunior.service.dto.CustomerDTO;
import com.github.robsonrjunior.service.dto.PersonDTO;
import com.github.robsonrjunior.service.dto.SaleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {
    @Mapping(target = "person", source = "person", qualifiedByName = "personId")
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    @Mapping(target = "sales", source = "sales", qualifiedByName = "saleId")
    CustomerDTO toDto(Customer s);

    @Named("personId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonDTO toDtoPersonId(Person person);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("saleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleDTO toDtoSaleId(Sale sale);
}
