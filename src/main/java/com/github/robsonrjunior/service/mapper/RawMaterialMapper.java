package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.RawMaterial;
import com.github.robsonrjunior.domain.StockMovement;
import com.github.robsonrjunior.service.dto.RawMaterialDTO;
import com.github.robsonrjunior.service.dto.StockMovementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RawMaterial} and its DTO {@link RawMaterialDTO}.
 */
@Mapper(componentModel = "spring")
public interface RawMaterialMapper extends EntityMapper<RawMaterialDTO, RawMaterial> {
    @Mapping(target = "stockMovements", source = "stockMovements", qualifiedByName = "stockMovementId")
    RawMaterialDTO toDto(RawMaterial s);

    @Named("stockMovementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StockMovementDTO toDtoStockMovementId(StockMovement stockMovement);
}
