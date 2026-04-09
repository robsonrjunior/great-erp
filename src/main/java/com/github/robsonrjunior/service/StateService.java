package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.State;
import com.github.robsonrjunior.repository.StateRepository;
import com.github.robsonrjunior.repository.search.StateSearchRepository;
import com.github.robsonrjunior.service.dto.StateDTO;
import com.github.robsonrjunior.service.mapper.StateMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.github.robsonrjunior.domain.State}.
 */
@Service
@Transactional
public class StateService {

    private static final Logger LOG = LoggerFactory.getLogger(StateService.class);

    private final StateRepository stateRepository;

    private final StateMapper stateMapper;

    private final StateSearchRepository stateSearchRepository;

    public StateService(StateRepository stateRepository, StateMapper stateMapper, StateSearchRepository stateSearchRepository) {
        this.stateRepository = stateRepository;
        this.stateMapper = stateMapper;
        this.stateSearchRepository = stateSearchRepository;
    }

    /**
     * Save a state.
     *
     * @param stateDTO the entity to save.
     * @return the persisted entity.
     */
    public StateDTO save(StateDTO stateDTO) {
        LOG.debug("Request to save State : {}", stateDTO);
        State state = stateMapper.toEntity(stateDTO);
        state = stateRepository.save(state);
        stateSearchRepository.index(state);
        return stateMapper.toDto(state);
    }

    /**
     * Update a state.
     *
     * @param stateDTO the entity to save.
     * @return the persisted entity.
     */
    public StateDTO update(StateDTO stateDTO) {
        LOG.debug("Request to update State : {}", stateDTO);
        State state = stateMapper.toEntity(stateDTO);
        state = stateRepository.save(state);
        stateSearchRepository.index(state);
        return stateMapper.toDto(state);
    }

    /**
     * Partially update a state.
     *
     * @param stateDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StateDTO> partialUpdate(StateDTO stateDTO) {
        LOG.debug("Request to partially update State : {}", stateDTO);

        return stateRepository
            .findById(stateDTO.getId())
            .map(existingState -> {
                stateMapper.partialUpdate(existingState, stateDTO);

                return existingState;
            })
            .map(stateRepository::save)
            .map(savedState -> {
                stateSearchRepository.index(savedState);
                return savedState;
            })
            .map(stateMapper::toDto);
    }

    /**
     * Get one state by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StateDTO> findOne(Long id) {
        LOG.debug("Request to get State : {}", id);
        return stateRepository.findById(id).map(stateMapper::toDto);
    }

    /**
     * Delete the state by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete State : {}", id);
        stateRepository.deleteById(id);
        stateSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the state corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StateDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of States for query {}", query);
        return stateSearchRepository.search(query, pageable).map(stateMapper::toDto);
    }
}
