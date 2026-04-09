package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.Company;
import com.github.robsonrjunior.service.dto.CompanyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Company} and its DTO {@link CompanyDTO}.
 */
@Mapper(componentModel = "spring")
public interface CompanyMapper extends EntityMapper<CompanyDTO, Company> {}
