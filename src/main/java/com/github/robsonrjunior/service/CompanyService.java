package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.Company;
import com.github.robsonrjunior.repository.CompanyRepository;
import com.github.robsonrjunior.repository.search.CompanySearchRepository;
import com.github.robsonrjunior.service.dto.CompanyDTO;
import com.github.robsonrjunior.service.mapper.CompanyMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.github.robsonrjunior.domain.Company}.
 */
@Service
@Transactional
public class CompanyService {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository companyRepository;

    private final CompanyMapper companyMapper;

    private final CompanySearchRepository companySearchRepository;

    public CompanyService(
        CompanyRepository companyRepository,
        CompanyMapper companyMapper,
        CompanySearchRepository companySearchRepository
    ) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.companySearchRepository = companySearchRepository;
    }

    /**
     * Save a company.
     *
     * @param companyDTO the entity to save.
     * @return the persisted entity.
     */
    public CompanyDTO save(CompanyDTO companyDTO) {
        LOG.debug("Request to save Company : {}", companyDTO);
        Company company = companyMapper.toEntity(companyDTO);
        company = companyRepository.save(company);
        companySearchRepository.index(company);
        return companyMapper.toDto(company);
    }

    /**
     * Update a company.
     *
     * @param companyDTO the entity to save.
     * @return the persisted entity.
     */
    public CompanyDTO update(CompanyDTO companyDTO) {
        LOG.debug("Request to update Company : {}", companyDTO);
        Company company = companyMapper.toEntity(companyDTO);
        company = companyRepository.save(company);
        companySearchRepository.index(company);
        return companyMapper.toDto(company);
    }

    /**
     * Partially update a company.
     *
     * @param companyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CompanyDTO> partialUpdate(CompanyDTO companyDTO) {
        LOG.debug("Request to partially update Company : {}", companyDTO);

        return companyRepository
            .findById(companyDTO.getId())
            .map(existingCompany -> {
                companyMapper.partialUpdate(existingCompany, companyDTO);

                return existingCompany;
            })
            .map(companyRepository::save)
            .map(savedCompany -> {
                companySearchRepository.index(savedCompany);
                return savedCompany;
            })
            .map(companyMapper::toDto);
    }

    /**
     *  Get all the companies where Customer is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CompanyDTO> findAllWhereCustomerIsNull() {
        LOG.debug("Request to get all companies where Customer is null");
        return StreamSupport.stream(companyRepository.findAll().spliterator(), false)
            .filter(company -> company.getCustomer() == null)
            .map(companyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the companies where Supplier is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CompanyDTO> findAllWhereSupplierIsNull() {
        LOG.debug("Request to get all companies where Supplier is null");
        return StreamSupport.stream(companyRepository.findAll().spliterator(), false)
            .filter(company -> company.getSupplier() == null)
            .map(companyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one company by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CompanyDTO> findOne(Long id) {
        LOG.debug("Request to get Company : {}", id);
        return companyRepository.findById(id).map(companyMapper::toDto);
    }

    /**
     * Delete the company by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Company : {}", id);
        companyRepository.deleteById(id);
        companySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the company corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CompanyDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Companies for query {}", query);
        return companySearchRepository.search(query, pageable).map(companyMapper::toDto);
    }
}
