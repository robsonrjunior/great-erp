package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.Sale;
import com.github.robsonrjunior.domain.StockMovement;
import com.github.robsonrjunior.domain.Warehouse;
import com.github.robsonrjunior.service.dto.SaleDTO;
import com.github.robsonrjunior.service.dto.StockMovementDTO;
import com.github.robsonrjunior.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Warehouse} and its DTO {@link WarehouseDTO}.
 */
@Mapper(componentModel = "spring")
public interface WarehouseMapper extends EntityMapper<WarehouseDTO, Warehouse> {
    @Mapping(target = "stockMovements", source = "stockMovements", qualifiedByName = "stockMovementId")
    @Mapping(target = "sales", source = "sales", qualifiedByName = "saleId")
    WarehouseDTO toDto(Warehouse s);

    @Named("stockMovementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StockMovementDTO toDtoStockMovementId(StockMovement stockMovement);

    @Named("saleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleDTO toDtoSaleId(Sale sale);
}
