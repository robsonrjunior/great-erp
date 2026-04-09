package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.Country;
import com.github.robsonrjunior.domain.State;
import com.github.robsonrjunior.service.dto.CountryDTO;
import com.github.robsonrjunior.service.dto.StateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link State} and its DTO {@link StateDTO}.
 */
@Mapper(componentModel = "spring")
public interface StateMapper extends EntityMapper<StateDTO, State> {
    @Mapping(target = "country", source = "country", qualifiedByName = "countryId")
    StateDTO toDto(State s);

    @Named("countryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CountryDTO toDtoCountryId(Country country);
}
