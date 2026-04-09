package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.Product;
import com.github.robsonrjunior.domain.SaleItem;
import com.github.robsonrjunior.domain.StockMovement;
import com.github.robsonrjunior.service.dto.ProductDTO;
import com.github.robsonrjunior.service.dto.SaleItemDTO;
import com.github.robsonrjunior.service.dto.StockMovementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "saleItems", source = "saleItems", qualifiedByName = "saleItemId")
    @Mapping(target = "stockMovements", source = "stockMovements", qualifiedByName = "stockMovementId")
    ProductDTO toDto(Product s);

    @Named("saleItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleItemDTO toDtoSaleItemId(SaleItem saleItem);

    @Named("stockMovementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StockMovementDTO toDtoStockMovementId(StockMovement stockMovement);
}
