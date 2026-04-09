package com.github.robsonrjunior.service.mapper;

import static com.github.robsonrjunior.domain.SaleAsserts.*;
import static com.github.robsonrjunior.domain.SaleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaleMapperTest {

    private SaleMapper saleMapper;

    @BeforeEach
    void setUp() {
        saleMapper = new SaleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSaleSample1();
        var actual = saleMapper.toEntity(saleMapper.toDto(expected));
        assertSaleAllPropertiesEquals(expected, actual);
    }
}
