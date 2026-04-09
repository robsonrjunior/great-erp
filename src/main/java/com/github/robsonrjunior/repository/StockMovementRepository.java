package com.github.robsonrjunior.repository;

import com.github.robsonrjunior.domain.StockMovement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockMovement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long>, JpaSpecificationExecutor<StockMovement> {}
