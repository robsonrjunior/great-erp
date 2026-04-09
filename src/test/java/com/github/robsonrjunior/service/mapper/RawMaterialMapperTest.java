package com.github.robsonrjunior.service.mapper;

import static com.github.robsonrjunior.domain.RawMaterialAsserts.*;
import static com.github.robsonrjunior.domain.RawMaterialTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RawMaterialMapperTest {

    private RawMaterialMapper rawMaterialMapper;

    @BeforeEach
    void setUp() {
        rawMaterialMapper = new RawMaterialMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRawMaterialSample1();
        var actual = rawMaterialMapper.toEntity(rawMaterialMapper.toDto(expected));
        assertRawMaterialAllPropertiesEquals(expected, actual);
    }
}
