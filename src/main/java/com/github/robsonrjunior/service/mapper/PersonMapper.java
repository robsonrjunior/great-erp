package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.Person;
import com.github.robsonrjunior.service.dto.PersonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Person} and its DTO {@link PersonDTO}.
 */
@Mapper(componentModel = "spring")
public interface PersonMapper extends EntityMapper<PersonDTO, Person> {}
