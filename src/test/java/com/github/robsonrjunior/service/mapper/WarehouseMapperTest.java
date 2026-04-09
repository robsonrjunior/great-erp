package com.github.robsonrjunior.service.mapper;

import static com.github.robsonrjunior.domain.WarehouseAsserts.*;
import static com.github.robsonrjunior.domain.WarehouseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WarehouseMapperTest {

    private WarehouseMapper warehouseMapper;

    @BeforeEach
    void setUp() {
        warehouseMapper = new WarehouseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWarehouseSample1();
        var actual = warehouseMapper.toEntity(warehouseMapper.toDto(expected));
        assertWarehouseAllPropertiesEquals(expected, actual);
    }
}
