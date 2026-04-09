package com.github.robsonrjunior.service.mapper;

import static com.github.robsonrjunior.domain.SupplierAsserts.*;
import static com.github.robsonrjunior.domain.SupplierTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SupplierMapperTest {

    private SupplierMapper supplierMapper;

    @BeforeEach
    void setUp() {
        supplierMapper = new SupplierMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSupplierSample1();
        var actual = supplierMapper.toEntity(supplierMapper.toDto(expected));
        assertSupplierAllPropertiesEquals(expected, actual);
    }
}
