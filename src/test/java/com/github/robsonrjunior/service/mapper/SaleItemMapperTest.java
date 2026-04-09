package com.github.robsonrjunior.service.mapper;

import static com.github.robsonrjunior.domain.SaleItemAsserts.*;
import static com.github.robsonrjunior.domain.SaleItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaleItemMapperTest {

    private SaleItemMapper saleItemMapper;

    @BeforeEach
    void setUp() {
        saleItemMapper = new SaleItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSaleItemSample1();
        var actual = saleItemMapper.toEntity(saleItemMapper.toDto(expected));
        assertSaleItemAllPropertiesEquals(expected, actual);
    }
}
