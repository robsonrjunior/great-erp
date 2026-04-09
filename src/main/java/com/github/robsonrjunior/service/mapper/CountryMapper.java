package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.Country;
import com.github.robsonrjunior.service.dto.CountryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Country} and its DTO {@link CountryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CountryMapper extends EntityMapper<CountryDTO, Country> {}
