package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.Warehouse;
import com.github.robsonrjunior.repository.WarehouseRepository;
import com.github.robsonrjunior.repository.search.WarehouseSearchRepository;
import com.github.robsonrjunior.service.dto.WarehouseDTO;
import com.github.robsonrjunior.service.mapper.WarehouseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.github.robsonrjunior.domain.Warehouse}.
 */
@Service
@Transactional
public class WarehouseService {

    private static final Logger LOG = LoggerFactory.getLogger(WarehouseService.class);

    private final WarehouseRepository warehouseRepository;

    private final WarehouseMapper warehouseMapper;

    private final WarehouseSearchRepository warehouseSearchRepository;

    public WarehouseService(
        WarehouseRepository warehouseRepository,
        WarehouseMapper warehouseMapper,
        WarehouseSearchRepository warehouseSearchRepository
    ) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
        this.warehouseSearchRepository = warehouseSearchRepository;
    }

    /**
     * Save a warehouse.
     *
     * @param warehouseDTO the entity to save.
     * @return the persisted entity.
     */
    public WarehouseDTO save(WarehouseDTO warehouseDTO) {
        LOG.debug("Request to save Warehouse : {}", warehouseDTO);
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        warehouse = warehouseRepository.save(warehouse);
        warehouseSearchRepository.index(warehouse);
        return warehouseMapper.toDto(warehouse);
    }

    /**
     * Update a warehouse.
     *
     * @param warehouseDTO the entity to save.
     * @return the persisted entity.
     */
    public WarehouseDTO update(WarehouseDTO warehouseDTO) {
        LOG.debug("Request to update Warehouse : {}", warehouseDTO);
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        warehouse = warehouseRepository.save(warehouse);
        warehouseSearchRepository.index(warehouse);
        return warehouseMapper.toDto(warehouse);
    }

    /**
     * Partially update a warehouse.
     *
     * @param warehouseDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WarehouseDTO> partialUpdate(WarehouseDTO warehouseDTO) {
        LOG.debug("Request to partially update Warehouse : {}", warehouseDTO);

        return warehouseRepository
            .findById(warehouseDTO.getId())
            .map(existingWarehouse -> {
                warehouseMapper.partialUpdate(existingWarehouse, warehouseDTO);

                return existingWarehouse;
            })
            .map(warehouseRepository::save)
            .map(savedWarehouse -> {
                warehouseSearchRepository.index(savedWarehouse);
                return savedWarehouse;
            })
            .map(warehouseMapper::toDto);
    }

    /**
     * Get one warehouse by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WarehouseDTO> findOne(Long id) {
        LOG.debug("Request to get Warehouse : {}", id);
        return warehouseRepository.findById(id).map(warehouseMapper::toDto);
    }

    /**
     * Delete the warehouse by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Warehouse : {}", id);
        warehouseRepository.deleteById(id);
        warehouseSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the warehouse corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WarehouseDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Warehouses for query {}", query);
        return warehouseSearchRepository.search(query, pageable).map(warehouseMapper::toDto);
    }
}
