package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.SaleItem;
import com.github.robsonrjunior.service.dto.SaleItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SaleItem} and its DTO {@link SaleItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface SaleItemMapper extends EntityMapper<SaleItemDTO, SaleItem> {}
