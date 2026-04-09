package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.Supplier;
import com.github.robsonrjunior.repository.SupplierRepository;
import com.github.robsonrjunior.repository.search.SupplierSearchRepository;
import com.github.robsonrjunior.service.dto.SupplierDTO;
import com.github.robsonrjunior.service.mapper.SupplierMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.github.robsonrjunior.domain.Supplier}.
 */
@Service
@Transactional
public class SupplierService {

    private static final Logger LOG = LoggerFactory.getLogger(SupplierService.class);

    private final SupplierRepository supplierRepository;

    private final SupplierMapper supplierMapper;

    private final SupplierSearchRepository supplierSearchRepository;

    public SupplierService(
        SupplierRepository supplierRepository,
        SupplierMapper supplierMapper,
        SupplierSearchRepository supplierSearchRepository
    ) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
        this.supplierSearchRepository = supplierSearchRepository;
    }

    /**
     * Save a supplier.
     *
     * @param supplierDTO the entity to save.
     * @return the persisted entity.
     */
    public SupplierDTO save(SupplierDTO supplierDTO) {
        LOG.debug("Request to save Supplier : {}", supplierDTO);
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        supplier = supplierRepository.save(supplier);
        supplierSearchRepository.index(supplier);
        return supplierMapper.toDto(supplier);
    }

    /**
     * Update a supplier.
     *
     * @param supplierDTO the entity to save.
     * @return the persisted entity.
     */
    public SupplierDTO update(SupplierDTO supplierDTO) {
        LOG.debug("Request to update Supplier : {}", supplierDTO);
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        supplier = supplierRepository.save(supplier);
        supplierSearchRepository.index(supplier);
        return supplierMapper.toDto(supplier);
    }

    /**
     * Partially update a supplier.
     *
     * @param supplierDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SupplierDTO> partialUpdate(SupplierDTO supplierDTO) {
        LOG.debug("Request to partially update Supplier : {}", supplierDTO);

        return supplierRepository
            .findById(supplierDTO.getId())
            .map(existingSupplier -> {
                supplierMapper.partialUpdate(existingSupplier, supplierDTO);

                return existingSupplier;
            })
            .map(supplierRepository::save)
            .map(savedSupplier -> {
                supplierSearchRepository.index(savedSupplier);
                return savedSupplier;
            })
            .map(supplierMapper::toDto);
    }

    /**
     * Get one supplier by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SupplierDTO> findOne(Long id) {
        LOG.debug("Request to get Supplier : {}", id);
        return supplierRepository.findById(id).map(supplierMapper::toDto);
    }

    /**
     * Delete the supplier by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Supplier : {}", id);
        supplierRepository.deleteById(id);
        supplierSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the supplier corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SupplierDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Suppliers for query {}", query);
        return supplierSearchRepository.search(query, pageable).map(supplierMapper::toDto);
    }
}
