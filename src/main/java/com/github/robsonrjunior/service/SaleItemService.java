package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.SaleItem;
import com.github.robsonrjunior.repository.SaleItemRepository;
import com.github.robsonrjunior.repository.search.SaleItemSearchRepository;
import com.github.robsonrjunior.service.dto.SaleItemDTO;
import com.github.robsonrjunior.service.mapper.SaleItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.github.robsonrjunior.domain.SaleItem}.
 */
@Service
@Transactional
public class SaleItemService {

    private static final Logger LOG = LoggerFactory.getLogger(SaleItemService.class);

    private final SaleItemRepository saleItemRepository;

    private final SaleItemMapper saleItemMapper;

    private final SaleItemSearchRepository saleItemSearchRepository;

    public SaleItemService(
        SaleItemRepository saleItemRepository,
        SaleItemMapper saleItemMapper,
        SaleItemSearchRepository saleItemSearchRepository
    ) {
        this.saleItemRepository = saleItemRepository;
        this.saleItemMapper = saleItemMapper;
        this.saleItemSearchRepository = saleItemSearchRepository;
    }

    /**
     * Save a saleItem.
     *
     * @param saleItemDTO the entity to save.
     * @return the persisted entity.
     */
    public SaleItemDTO save(SaleItemDTO saleItemDTO) {
        LOG.debug("Request to save SaleItem : {}", saleItemDTO);
        SaleItem saleItem = saleItemMapper.toEntity(saleItemDTO);
        saleItem = saleItemRepository.save(saleItem);
        saleItemSearchRepository.index(saleItem);
        return saleItemMapper.toDto(saleItem);
    }

    /**
     * Update a saleItem.
     *
     * @param saleItemDTO the entity to save.
     * @return the persisted entity.
     */
    public SaleItemDTO update(SaleItemDTO saleItemDTO) {
        LOG.debug("Request to update SaleItem : {}", saleItemDTO);
        SaleItem saleItem = saleItemMapper.toEntity(saleItemDTO);
        saleItem = saleItemRepository.save(saleItem);
        saleItemSearchRepository.index(saleItem);
        return saleItemMapper.toDto(saleItem);
    }

    /**
     * Partially update a saleItem.
     *
     * @param saleItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SaleItemDTO> partialUpdate(SaleItemDTO saleItemDTO) {
        LOG.debug("Request to partially update SaleItem : {}", saleItemDTO);

        return saleItemRepository
            .findById(saleItemDTO.getId())
            .map(existingSaleItem -> {
                saleItemMapper.partialUpdate(existingSaleItem, saleItemDTO);

                return existingSaleItem;
            })
            .map(saleItemRepository::save)
            .map(savedSaleItem -> {
                saleItemSearchRepository.index(savedSaleItem);
                return savedSaleItem;
            })
            .map(saleItemMapper::toDto);
    }

    /**
     * Get one saleItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SaleItemDTO> findOne(Long id) {
        LOG.debug("Request to get SaleItem : {}", id);
        return saleItemRepository.findById(id).map(saleItemMapper::toDto);
    }

    /**
     * Delete the saleItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SaleItem : {}", id);
        saleItemRepository.deleteById(id);
        saleItemSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the saleItem corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SaleItemDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of SaleItems for query {}", query);
        return saleItemSearchRepository.search(query, pageable).map(saleItemMapper::toDto);
    }
}
