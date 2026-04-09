package com.github.robsonrjunior.service.mapper;

import static com.github.robsonrjunior.domain.CityAsserts.*;
import static com.github.robsonrjunior.domain.CityTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CityMapperTest {

    private CityMapper cityMapper;

    @BeforeEach
    void setUp() {
        cityMapper = new CityMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCitySample1();
        var actual = cityMapper.toEntity(cityMapper.toDto(expected));
        assertCityAllPropertiesEquals(expected, actual);
    }
}
