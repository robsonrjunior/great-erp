package com.github.robsonrjunior.repository;

import com.github.robsonrjunior.domain.RawMaterial;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RawMaterial entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long>, JpaSpecificationExecutor<RawMaterial> {}
