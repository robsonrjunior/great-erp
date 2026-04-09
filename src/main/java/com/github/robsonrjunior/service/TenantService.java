package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.Tenant;
import com.github.robsonrjunior.repository.TenantRepository;
import com.github.robsonrjunior.repository.search.TenantSearchRepository;
import com.github.robsonrjunior.service.dto.TenantDTO;
import com.github.robsonrjunior.service.mapper.TenantMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.github.robsonrjunior.domain.Tenant}.
 */
@Service
@Transactional
public class TenantService {

    private static final Logger LOG = LoggerFactory.getLogger(TenantService.class);

    private final TenantRepository tenantRepository;

    private final TenantMapper tenantMapper;

    private final TenantSearchRepository tenantSearchRepository;

    public TenantService(TenantRepository tenantRepository, TenantMapper tenantMapper, TenantSearchRepository tenantSearchRepository) {
        this.tenantRepository = tenantRepository;
        this.tenantMapper = tenantMapper;
        this.tenantSearchRepository = tenantSearchRepository;
    }

    /**
     * Save a tenant.
     *
     * @param tenantDTO the entity to save.
     * @return the persisted entity.
     */
    public TenantDTO save(TenantDTO tenantDTO) {
        LOG.debug("Request to save Tenant : {}", tenantDTO);
        Tenant tenant = tenantMapper.toEntity(tenantDTO);
        tenant = tenantRepository.save(tenant);
        tenantSearchRepository.index(tenant);
        return tenantMapper.toDto(tenant);
    }

    /**
     * Update a tenant.
     *
     * @param tenantDTO the entity to save.
     * @return the persisted entity.
     */
    public TenantDTO update(TenantDTO tenantDTO) {
        LOG.debug("Request to update Tenant : {}", tenantDTO);
        Tenant tenant = tenantMapper.toEntity(tenantDTO);
        tenant = tenantRepository.save(tenant);
        tenantSearchRepository.index(tenant);
        return tenantMapper.toDto(tenant);
    }

    /**
     * Partially update a tenant.
     *
     * @param tenantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TenantDTO> partialUpdate(TenantDTO tenantDTO) {
        LOG.debug("Request to partially update Tenant : {}", tenantDTO);

        return tenantRepository
            .findById(tenantDTO.getId())
            .map(existingTenant -> {
                tenantMapper.partialUpdate(existingTenant, tenantDTO);

                return existingTenant;
            })
            .map(tenantRepository::save)
            .map(savedTenant -> {
                tenantSearchRepository.index(savedTenant);
                return savedTenant;
            })
            .map(tenantMapper::toDto);
    }

    /**
     * Get one tenant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TenantDTO> findOne(Long id) {
        LOG.debug("Request to get Tenant : {}", id);
        return tenantRepository.findById(id).map(tenantMapper::toDto);
    }

    /**
     * Delete the tenant by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Tenant : {}", id);
        tenantRepository.deleteById(id);
        tenantSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the tenant corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TenantDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Tenants for query {}", query);
        return tenantSearchRepository.search(query, pageable).map(tenantMapper::toDto);
    }
}
