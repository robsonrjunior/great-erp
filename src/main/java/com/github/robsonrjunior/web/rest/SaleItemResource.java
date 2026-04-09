package com.github.robsonrjunior.web.rest;

import com.github.robsonrjunior.repository.SaleItemRepository;
import com.github.robsonrjunior.service.SaleItemQueryService;
import com.github.robsonrjunior.service.SaleItemService;
import com.github.robsonrjunior.service.criteria.SaleItemCriteria;
import com.github.robsonrjunior.service.dto.SaleItemDTO;
import com.github.robsonrjunior.web.rest.errors.BadRequestAlertException;
import com.github.robsonrjunior.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.github.robsonrjunior.domain.SaleItem}.
 */
@RestController
@RequestMapping("/api/sale-items")
public class SaleItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(SaleItemResource.class);

    private static final String ENTITY_NAME = "saleItem";

    @Value("${jhipster.clientApp.name:greaterp}")
    private String applicationName;

    private final SaleItemService saleItemService;

    private final SaleItemRepository saleItemRepository;

    private final SaleItemQueryService saleItemQueryService;

    public SaleItemResource(
        SaleItemService saleItemService,
        SaleItemRepository saleItemRepository,
        SaleItemQueryService saleItemQueryService
    ) {
        this.saleItemService = saleItemService;
        this.saleItemRepository = saleItemRepository;
        this.saleItemQueryService = saleItemQueryService;
    }

    /**
     * {@code POST  /sale-items} : Create a new saleItem.
     *
     * @param saleItemDTO the saleItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new saleItemDTO, or with status {@code 400 (Bad Request)} if the saleItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SaleItemDTO> createSaleItem(@Valid @RequestBody SaleItemDTO saleItemDTO) throws URISyntaxException {
        LOG.debug("REST request to save SaleItem : {}", saleItemDTO);
        if (saleItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new saleItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        saleItemDTO = saleItemService.save(saleItemDTO);
        return ResponseEntity.created(new URI("/api/sale-items/" + saleItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, saleItemDTO.getId().toString()))
            .body(saleItemDTO);
    }

    /**
     * {@code PUT  /sale-items/:id} : Updates an existing saleItem.
     *
     * @param id the id of the saleItemDTO to save.
     * @param saleItemDTO the saleItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleItemDTO,
     * or with status {@code 400 (Bad Request)} if the saleItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the saleItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SaleItemDTO> updateSaleItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SaleItemDTO saleItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SaleItem : {}, {}", id, saleItemDTO);
        if (saleItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        saleItemDTO = saleItemService.update(saleItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, saleItemDTO.getId().toString()))
            .body(saleItemDTO);
    }

    /**
     * {@code PATCH  /sale-items/:id} : Partial updates given fields of an existing saleItem, field will ignore if it is null
     *
     * @param id the id of the saleItemDTO to save.
     * @param saleItemDTO the saleItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleItemDTO,
     * or with status {@code 400 (Bad Request)} if the saleItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the saleItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the saleItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SaleItemDTO> partialUpdateSaleItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SaleItemDTO saleItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SaleItem partially : {}, {}", id, saleItemDTO);
        if (saleItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SaleItemDTO> result = saleItemService.partialUpdate(saleItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, saleItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sale-items} : get all the Sale Items.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Sale Items in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SaleItemDTO>> getAllSaleItems(
        SaleItemCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get SaleItems by criteria: {}", criteria);

        Page<SaleItemDTO> page = saleItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sale-items/count} : count all the saleItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSaleItems(SaleItemCriteria criteria) {
        LOG.debug("REST request to count SaleItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(saleItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sale-items/:id} : get the "id" saleItem.
     *
     * @param id the id of the saleItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the saleItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SaleItemDTO> getSaleItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SaleItem : {}", id);
        Optional<SaleItemDTO> saleItemDTO = saleItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(saleItemDTO);
    }

    /**
     * {@code DELETE  /sale-items/:id} : delete the "id" saleItem.
     *
     * @param id the id of the saleItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaleItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SaleItem : {}", id);
        saleItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /sale-items/_search?query=:query} : search for the saleItem corresponding
     * to the query.
     *
     * @param query the query of the saleItem search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<SaleItemDTO>> searchSaleItems(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of SaleItems for query {}", query);
        try {
            Page<SaleItemDTO> page = saleItemService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
