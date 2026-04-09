package com.github.robsonrjunior.service;

import com.github.robsonrjunior.domain.Person;
import com.github.robsonrjunior.repository.PersonRepository;
import com.github.robsonrjunior.repository.search.PersonSearchRepository;
import com.github.robsonrjunior.service.dto.PersonDTO;
import com.github.robsonrjunior.service.mapper.PersonMapper;
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
 * Service Implementation for managing {@link com.github.robsonrjunior.domain.Person}.
 */
@Service
@Transactional
public class PersonService {

    private static final Logger LOG = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    private final PersonSearchRepository personSearchRepository;

    public PersonService(PersonRepository personRepository, PersonMapper personMapper, PersonSearchRepository personSearchRepository) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.personSearchRepository = personSearchRepository;
    }

    /**
     * Save a person.
     *
     * @param personDTO the entity to save.
     * @return the persisted entity.
     */
    public PersonDTO save(PersonDTO personDTO) {
        LOG.debug("Request to save Person : {}", personDTO);
        Person person = personMapper.toEntity(personDTO);
        person = personRepository.save(person);
        personSearchRepository.index(person);
        return personMapper.toDto(person);
    }

    /**
     * Update a person.
     *
     * @param personDTO the entity to save.
     * @return the persisted entity.
     */
    public PersonDTO update(PersonDTO personDTO) {
        LOG.debug("Request to update Person : {}", personDTO);
        Person person = personMapper.toEntity(personDTO);
        person = personRepository.save(person);
        personSearchRepository.index(person);
        return personMapper.toDto(person);
    }

    /**
     * Partially update a person.
     *
     * @param personDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PersonDTO> partialUpdate(PersonDTO personDTO) {
        LOG.debug("Request to partially update Person : {}", personDTO);

        return personRepository
            .findById(personDTO.getId())
            .map(existingPerson -> {
                personMapper.partialUpdate(existingPerson, personDTO);

                return existingPerson;
            })
            .map(personRepository::save)
            .map(savedPerson -> {
                personSearchRepository.index(savedPerson);
                return savedPerson;
            })
            .map(personMapper::toDto);
    }

    /**
     *  Get all the people where Customer is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PersonDTO> findAllWhereCustomerIsNull() {
        LOG.debug("Request to get all people where Customer is null");
        return StreamSupport.stream(personRepository.findAll().spliterator(), false)
            .filter(person -> person.getCustomer() == null)
            .map(personMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the people where Supplier is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PersonDTO> findAllWhereSupplierIsNull() {
        LOG.debug("Request to get all people where Supplier is null");
        return StreamSupport.stream(personRepository.findAll().spliterator(), false)
            .filter(person -> person.getSupplier() == null)
            .map(personMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one person by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PersonDTO> findOne(Long id) {
        LOG.debug("Request to get Person : {}", id);
        return personRepository.findById(id).map(personMapper::toDto);
    }

    /**
     * Delete the person by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Person : {}", id);
        personRepository.deleteById(id);
        personSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the person corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PersonDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of People for query {}", query);
        return personSearchRepository.search(query, pageable).map(personMapper::toDto);
    }
}
