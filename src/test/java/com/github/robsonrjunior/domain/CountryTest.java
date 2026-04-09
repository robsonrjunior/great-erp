package com.github.robsonrjunior.domain;

import static com.github.robsonrjunior.domain.CountryTestSamples.*;
import static com.github.robsonrjunior.domain.StateTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CountryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Country.class);
        Country country1 = getCountrySample1();
        Country country2 = new Country();
        assertThat(country1).isNotEqualTo(country2);

        country2.setId(country1.getId());
        assertThat(country1).isEqualTo(country2);

        country2 = getCountrySample2();
        assertThat(country1).isNotEqualTo(country2);
    }

    @Test
    void statesTest() {
        Country country = getCountryRandomSampleGenerator();
        State stateBack = getStateRandomSampleGenerator();

        country.addStates(stateBack);
        assertThat(country.getStateses()).containsOnly(stateBack);
        assertThat(stateBack.getCountry()).isEqualTo(country);

        country.removeStates(stateBack);
        assertThat(country.getStateses()).doesNotContain(stateBack);
        assertThat(stateBack.getCountry()).isNull();

        country.stateses(new HashSet<>(Set.of(stateBack)));
        assertThat(country.getStateses()).containsOnly(stateBack);
        assertThat(stateBack.getCountry()).isEqualTo(country);

        country.setStateses(new HashSet<>());
        assertThat(country.getStateses()).doesNotContain(stateBack);
        assertThat(stateBack.getCountry()).isNull();
    }
}
