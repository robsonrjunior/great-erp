package com.github.robsonrjunior.web.rest;

import com.github.robsonrjunior.repository.RawMaterialRepository;
import com.github.robsonrjunior.service.RawMaterialQueryService;
import com.github.robsonrjunior.service.RawMaterialService;
import com.github.robsonrjunior.service.criteria.RawMaterialCriteria;
import com.github.robsonrjunior.service.dto.RawMaterialDTO;
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
 * REST controller for managing {@link com.github.robsonrjunior.domain.RawMaterial}.
 */
@RestController
@RequestMapping("/api/raw-materials")
public class RawMaterialResource {

    private static final Logger LOG = LoggerFactory.getLogger(RawMaterialResource.class);

    private static final String ENTITY_NAME = "rawMaterial";

    @Value("${jhipster.clientApp.name:greaterp}")
    private String applicationName;

    private final RawMaterialService rawMaterialService;

    private final RawMaterialRepository rawMaterialRepository;

    private final RawMaterialQueryService rawMaterialQueryService;

    public RawMaterialResource(
        RawMaterialService rawMaterialService,
        RawMaterialRepository rawMaterialRepository,
        RawMaterialQueryService rawMaterialQueryService
    ) {
        this.rawMaterialService = rawMaterialService;
        this.rawMaterialRepository = rawMaterialRepository;
        this.rawMaterialQueryService = rawMaterialQueryService;
    }

    /**
     * {@code POST  /raw-materials} : Create a new rawMaterial.
     *
     * @param rawMaterialDTO the rawMaterialDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rawMaterialDTO, or with status {@code 400 (Bad Request)} if the rawMaterial has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RawMaterialDTO> createRawMaterial(@Valid @RequestBody RawMaterialDTO rawMaterialDTO) throws URISyntaxException {
        LOG.debug("REST request to save RawMaterial : {}", rawMaterialDTO);
        if (rawMaterialDTO.getId() != null) {
            throw new BadRequestAlertException("A new rawMaterial cannot already have an ID", ENTITY_NAME, "idexists");
        }
        rawMaterialDTO = rawMaterialService.save(rawMaterialDTO);
        return ResponseEntity.created(new URI("/api/raw-materials/" + rawMaterialDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, rawMaterialDTO.getId().toString()))
            .body(rawMaterialDTO);
    }

    /**
     * {@code PUT  /raw-materials/:id} : Updates an existing rawMaterial.
     *
     * @param id the id of the rawMaterialDTO to save.
     * @param rawMaterialDTO the rawMaterialDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rawMaterialDTO,
     * or with status {@code 400 (Bad Request)} if the rawMaterialDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rawMaterialDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialDTO> updateRawMaterial(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RawMaterialDTO rawMaterialDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RawMaterial : {}, {}", id, rawMaterialDTO);
        if (rawMaterialDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rawMaterialDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rawMaterialRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        rawMaterialDTO = rawMaterialService.update(rawMaterialDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rawMaterialDTO.getId().toString()))
            .body(rawMaterialDTO);
    }

    /**
     * {@code PATCH  /raw-materials/:id} : Partial updates given fields of an existing rawMaterial, field will ignore if it is null
     *
     * @param id the id of the rawMaterialDTO to save.
     * @param rawMaterialDTO the rawMaterialDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rawMaterialDTO,
     * or with status {@code 400 (Bad Request)} if the rawMaterialDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rawMaterialDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rawMaterialDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RawMaterialDTO> partialUpdateRawMaterial(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RawMaterialDTO rawMaterialDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RawMaterial partially : {}, {}", id, rawMaterialDTO);
        if (rawMaterialDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rawMaterialDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rawMaterialRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RawMaterialDTO> result = rawMaterialService.partialUpdate(rawMaterialDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rawMaterialDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /raw-materials} : get all the Raw Materials.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Raw Materials in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RawMaterialDTO>> getAllRawMaterials(
        RawMaterialCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get RawMaterials by criteria: {}", criteria);

        Page<RawMaterialDTO> page = rawMaterialQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /raw-materials/count} : count all the rawMaterials.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRawMaterials(RawMaterialCriteria criteria) {
        LOG.debug("REST request to count RawMaterials by criteria: {}", criteria);
        return ResponseEntity.ok().body(rawMaterialQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /raw-materials/:id} : get the "id" rawMaterial.
     *
     * @param id the id of the rawMaterialDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rawMaterialDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialDTO> getRawMaterial(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RawMaterial : {}", id);
        Optional<RawMaterialDTO> rawMaterialDTO = rawMaterialService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rawMaterialDTO);
    }

    /**
     * {@code DELETE  /raw-materials/:id} : delete the "id" rawMaterial.
     *
     * @param id the id of the rawMaterialDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRawMaterial(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RawMaterial : {}", id);
        rawMaterialService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /raw-materials/_search?query=:query} : search for the rawMaterial corresponding
     * to the query.
     *
     * @param query the query of the rawMaterial search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<RawMaterialDTO>> searchRawMaterials(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of RawMaterials for query {}", query);
        try {
            Page<RawMaterialDTO> page = rawMaterialService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
