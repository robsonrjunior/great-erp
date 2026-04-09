package com.github.robsonrjunior.service.mapper;

import static com.github.robsonrjunior.domain.CustomerAsserts.*;
import static com.github.robsonrjunior.domain.CustomerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerMapperTest {

    private CustomerMapper customerMapper;

    @BeforeEach
    void setUp() {
        customerMapper = new CustomerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCustomerSample1();
        var actual = customerMapper.toEntity(customerMapper.toDto(expected));
        assertCustomerAllPropertiesEquals(expected, actual);
    }
}
