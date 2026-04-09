package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.StockMovement;
import com.github.robsonrjunior.service.dto.StockMovementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockMovement} and its DTO {@link StockMovementDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockMovementMapper extends EntityMapper<StockMovementDTO, StockMovement> {}
