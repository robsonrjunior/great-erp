package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.Company;
import com.github.robsonrjunior.domain.Person;
import com.github.robsonrjunior.domain.RawMaterial;
import com.github.robsonrjunior.domain.Supplier;
import com.github.robsonrjunior.service.dto.CompanyDTO;
import com.github.robsonrjunior.service.dto.PersonDTO;
import com.github.robsonrjunior.service.dto.RawMaterialDTO;
import com.github.robsonrjunior.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Supplier} and its DTO {@link SupplierDTO}.
 */
@Mapper(componentModel = "spring")
public interface SupplierMapper extends EntityMapper<SupplierDTO, Supplier> {
    @Mapping(target = "person", source = "person", qualifiedByName = "personId")
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    @Mapping(target = "rawMaterials", source = "rawMaterials", qualifiedByName = "rawMaterialId")
    SupplierDTO toDto(Supplier s);

    @Named("personId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonDTO toDtoPersonId(Person person);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("rawMaterialId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RawMaterialDTO toDtoRawMaterialId(RawMaterial rawMaterial);
}
