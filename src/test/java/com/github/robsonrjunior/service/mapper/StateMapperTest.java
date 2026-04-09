package com.github.robsonrjunior.service.mapper;

import static com.github.robsonrjunior.domain.StateAsserts.*;
import static com.github.robsonrjunior.domain.StateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StateMapperTest {

    private StateMapper stateMapper;

    @BeforeEach
    void setUp() {
        stateMapper = new StateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStateSample1();
        var actual = stateMapper.toEntity(stateMapper.toDto(expected));
        assertStateAllPropertiesEquals(expected, actual);
    }
}
