package com.github.robsonrjunior.service.mapper;

import static com.github.robsonrjunior.domain.CompanyAsserts.*;
import static com.github.robsonrjunior.domain.CompanyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompanyMapperTest {

    private CompanyMapper companyMapper;

    @BeforeEach
    void setUp() {
        companyMapper = new CompanyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCompanySample1();
        var actual = companyMapper.toEntity(companyMapper.toDto(expected));
        assertCompanyAllPropertiesEquals(expected, actual);
    }
}
