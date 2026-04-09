package com.github.robsonrjunior.web.rest;

import com.github.robsonrjunior.repository.StateRepository;
import com.github.robsonrjunior.service.StateQueryService;
import com.github.robsonrjunior.service.StateService;
import com.github.robsonrjunior.service.criteria.StateCriteria;
import com.github.robsonrjunior.service.dto.StateDTO;
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
 * REST controller for managing {@link com.github.robsonrjunior.domain.State}.
 */
@RestController
@RequestMapping("/api/states")
public class StateResource {

    private static final Logger LOG = LoggerFactory.getLogger(StateResource.class);

    private static final String ENTITY_NAME = "state";

    @Value("${jhipster.clientApp.name:greaterp}")
    private String applicationName;

    private final StateService stateService;

    private final StateRepository stateRepository;

    private final StateQueryService stateQueryService;

    public StateResource(StateService stateService, StateRepository stateRepository, StateQueryService stateQueryService) {
        this.stateService = stateService;
        this.stateRepository = stateRepository;
        this.stateQueryService = stateQueryService;
    }

    /**
     * {@code POST  /states} : Create a new state.
     *
     * @param stateDTO the stateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stateDTO, or with status {@code 400 (Bad Request)} if the state has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StateDTO> createState(@Valid @RequestBody StateDTO stateDTO) throws URISyntaxException {
        LOG.debug("REST request to save State : {}", stateDTO);
        if (stateDTO.getId() != null) {
            throw new BadRequestAlertException("A new state cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stateDTO = stateService.save(stateDTO);
        return ResponseEntity.created(new URI("/api/states/" + stateDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, stateDTO.getId().toString()))
            .body(stateDTO);
    }

    /**
     * {@code PUT  /states/:id} : Updates an existing state.
     *
     * @param id the id of the stateDTO to save.
     * @param stateDTO the stateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stateDTO,
     * or with status {@code 400 (Bad Request)} if the stateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StateDTO> updateState(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StateDTO stateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update State : {}, {}", id, stateDTO);
        if (stateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stateDTO = stateService.update(stateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stateDTO.getId().toString()))
            .body(stateDTO);
    }

    /**
     * {@code PATCH  /states/:id} : Partial updates given fields of an existing state, field will ignore if it is null
     *
     * @param id the id of the stateDTO to save.
     * @param stateDTO the stateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stateDTO,
     * or with status {@code 400 (Bad Request)} if the stateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StateDTO> partialUpdateState(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StateDTO stateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update State partially : {}, {}", id, stateDTO);
        if (stateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StateDTO> result = stateService.partialUpdate(stateDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stateDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /states} : get all the States.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of States in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StateDTO>> getAllStates(
        StateCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get States by criteria: {}", criteria);

        Page<StateDTO> page = stateQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /states/count} : count all the states.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStates(StateCriteria criteria) {
        LOG.debug("REST request to count States by criteria: {}", criteria);
        return ResponseEntity.ok().body(stateQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /states/:id} : get the "id" state.
     *
     * @param id the id of the stateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StateDTO> getState(@PathVariable("id") Long id) {
        LOG.debug("REST request to get State : {}", id);
        Optional<StateDTO> stateDTO = stateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stateDTO);
    }

    /**
     * {@code DELETE  /states/:id} : delete the "id" state.
     *
     * @param id the id of the stateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteState(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete State : {}", id);
        stateService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /states/_search?query=:query} : search for the state corresponding
     * to the query.
     *
     * @param query the query of the state search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<StateDTO>> searchStates(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of States for query {}", query);
        try {
            Page<StateDTO> page = stateService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
