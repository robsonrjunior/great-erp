package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.CityTestSamples.*;
import static com.github.robsonrjunior.domain.CountryTestSamples.*;
import static com.github.robsonrjunior.domain.StateTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(State.class);
        State state1 = getStateSample1();
        State state2 = new State();
        assertThat(state1).isNotEqualTo(state2);

        state2.setId(state1.getId());
        assertThat(state1).isEqualTo(state2);

        state2 = getStateSample2();
        assertThat(state1).isNotEqualTo(state2);
    }

    @Test
    void citiesTest() {
        State state = getStateRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        state.addCities(cityBack);
        assertThat(state.getCitieses()).containsOnly(cityBack);
        assertThat(cityBack.getState()).isEqualTo(state);

        state.removeCities(cityBack);
        assertThat(state.getCitieses()).doesNotContain(cityBack);
        assertThat(cityBack.getState()).isNull();

        state.citieses(new HashSet<>(Set.of(cityBack)));
        assertThat(state.getCitieses()).containsOnly(cityBack);
        assertThat(cityBack.getState()).isEqualTo(state);

        state.setCitieses(new HashSet<>());
        assertThat(state.getCitieses()).doesNotContain(cityBack);
        assertThat(cityBack.getState()).isNull();
    }

    @Test
    void countryTest() {
        State state = getStateRandomSampleGenerator();
        Country countryBack = getCountryRandomSampleGenerator();

        state.setCountry(countryBack);
        assertThat(state.getCountry()).isEqualTo(countryBack);

        state.country(null);
        assertThat(state.getCountry()).isNull();
    }
}
