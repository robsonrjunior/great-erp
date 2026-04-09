package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.Sale;
import com.github.robsonrjunior.domain.SaleItem;
import com.github.robsonrjunior.service.dto.SaleDTO;
import com.github.robsonrjunior.service.dto.SaleItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Sale} and its DTO {@link SaleDTO}.
 */
@Mapper(componentModel = "spring")
public interface SaleMapper extends EntityMapper<SaleDTO, Sale> {
    @Mapping(target = "items", source = "items", qualifiedByName = "saleItemId")
    SaleDTO toDto(Sale s);

    @Named("saleItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleItemDTO toDtoSaleItemId(SaleItem saleItem);
}
