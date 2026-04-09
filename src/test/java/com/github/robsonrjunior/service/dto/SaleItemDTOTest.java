package com.github.robsonrjunior.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.robsonrjunior.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SaleItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleItemDTO.class);
        SaleItemDTO saleItemDTO1 = new SaleItemDTO();
        saleItemDTO1.setId(1L);
        SaleItemDTO saleItemDTO2 = new SaleItemDTO();
        assertThat(saleItemDTO1).isNotEqualTo(saleItemDTO2);
        saleItemDTO2.setId(saleItemDTO1.getId());
        assertThat(saleItemDTO1).isEqualTo(saleItemDTO2);
        saleItemDTO2.setId(2L);
        assertThat(saleItemDTO1).isNotEqualTo(saleItemDTO2);
        saleItemDTO1.setId(null);
        assertThat(saleItemDTO1).isNotEqualTo(saleItemDTO2);
    }
}
