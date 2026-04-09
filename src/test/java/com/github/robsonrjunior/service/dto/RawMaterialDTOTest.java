package com.github.robsonrjunior.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RawMaterialDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RawMaterialDTO.class);
        RawMaterialDTO rawMaterialDTO1 = new RawMaterialDTO();
        rawMaterialDTO1.setId(1L);
        RawMaterialDTO rawMaterialDTO2 = new RawMaterialDTO();
        assertThat(rawMaterialDTO1).isNotEqualTo(rawMaterialDTO2);
        rawMaterialDTO2.setId(rawMaterialDTO1.getId());
        assertThat(rawMaterialDTO1).isEqualTo(rawMaterialDTO2);
        rawMaterialDTO2.setId(2L);
        assertThat(rawMaterialDTO1).isNotEqualTo(rawMaterialDTO2);
        rawMaterialDTO1.setId(null);
        assertThat(rawMaterialDTO1).isNotEqualTo(rawMaterialDTO2);
    }
}
