package com.github.robsonrjunior.web.rest;

import static com.github.robsonrjunior.domain.StockMovementAsserts.*;
import static com.github.robsonrjunior.web.rest.TestUtil.createUpdateProxyForBean;
import static com.github.robsonrjunior.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.robsonrjunior.IntegrationTest;
import com.github.robsonrjunior.domain.StockMovement;
import com.github.robsonrjunior.domain.enumeration.MovementType;
import com.github.robsonrjunior.repository.StockMovementRepository;
import com.github.robsonrjunior.repository.search.StockMovementSearchRepository;
import com.github.robsonrjunior.service.dto.StockMovementDTO;
import com.github.robsonrjunior.service.mapper.StockMovementMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StockMovementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StockMovementResourceIT {

    private static final Instant DEFAULT_MOVEMENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MOVEMENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final MovementType DEFAULT_MOVEMENT_TYPE = MovementType.INBOUND;
    private static final MovementType UPDATED_MOVEMENT_TYPE = MovementType.OUTBOUND;

    private static final BigDecimal DEFAULT_QUANTITY = new BigDecimal(0);
    private static final BigDecimal UPDATED_QUANTITY = new BigDecimal(1);
    private static final BigDecimal SMALLER_QUANTITY = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_UNIT_COST = new BigDecimal(0);
    private static final BigDecimal UPDATED_UNIT_COST = new BigDecimal(1);
    private static final BigDecimal SMALLER_UNIT_COST = new BigDecimal(0 - 1);

    private static final String DEFAULT_REFERENCE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Instant DEFAULT_DELETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/stock-movements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/stock-movements/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private StockMovementMapper stockMovementMapper;

    @Autowired
    private StockMovementSearchRepository stockMovementSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockMovementMockMvc;

    private StockMovement stockMovement;

    private StockMovement insertedStockMovement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockMovement createEntity(EntityManager em) {
        StockMovement stockMovement = new StockMovement()
            .movementDate(DEFAULT_MOVEMENT_DATE)
            .movementType(DEFAULT_MOVEMENT_TYPE)
            .quantity(DEFAULT_QUANTITY)
            .unitCost(DEFAULT_UNIT_COST)
            .referenceNumber(DEFAULT_REFERENCE_NUMBER)
            .notes(DEFAULT_NOTES)
            .deletedAt(DEFAULT_DELETED_AT);
        return stockMovement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockMovement createUpdatedEntity(EntityManager em) {
        StockMovement updatedStockMovement = new StockMovement()
            .movementDate(UPDATED_MOVEMENT_DATE)
            .movementType(UPDATED_MOVEMENT_TYPE)
            .quantity(UPDATED_QUANTITY)
            .unitCost(UPDATED_UNIT_COST)
            .referenceNumber(UPDATED_REFERENCE_NUMBER)
            .notes(UPDATED_NOTES)
            .deletedAt(UPDATED_DELETED_AT);
        return updatedStockMovement;
    }

    @BeforeEach
    void initTest() {
        stockMovement = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedStockMovement != null) {
            stockMovementRepository.delete(insertedStockMovement);
            stockMovementSearchRepository.delete(insertedStockMovement);
            insertedStockMovement = null;
        }
    }

    @Test
    @Transactional
    void createStockMovement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);
        var returnedStockMovementDTO = om.readValue(
            restStockMovementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StockMovementDTO.class
        );

        // Validate the StockMovement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStockMovement = stockMovementMapper.toEntity(returnedStockMovementDTO);
        assertStockMovementUpdatableFieldsEquals(returnedStockMovement, getPersistedStockMovement(returnedStockMovement));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedStockMovement = returnedStockMovement;
    }

    @Test
    @Transactional
    void createStockMovementWithExistingId() throws Exception {
        // Create the StockMovement with an existing ID
        stockMovement.setId(1L);
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkMovementDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        // set the field null
        stockMovement.setMovementDate(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkMovementTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        // set the field null
        stockMovement.setMovementType(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        // set the field null
        stockMovement.setQuantity(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllStockMovements() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockMovement.getId().intValue())))
            .andExpect(jsonPath("$.[*].movementDate").value(hasItem(DEFAULT_MOVEMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].movementType").value(hasItem(DEFAULT_MOVEMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].unitCost").value(hasItem(sameNumber(DEFAULT_UNIT_COST))))
            .andExpect(jsonPath("$.[*].referenceNumber").value(hasItem(DEFAULT_REFERENCE_NUMBER)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));
    }

    @Test
    @Transactional
    void getStockMovement() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get the stockMovement
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL_ID, stockMovement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockMovement.getId().intValue()))
            .andExpect(jsonPath("$.movementDate").value(DEFAULT_MOVEMENT_DATE.toString()))
            .andExpect(jsonPath("$.movementType").value(DEFAULT_MOVEMENT_TYPE.toString()))
            .andExpect(jsonPath("$.quantity").value(sameNumber(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.unitCost").value(sameNumber(DEFAULT_UNIT_COST)))
            .andExpect(jsonPath("$.referenceNumber").value(DEFAULT_REFERENCE_NUMBER))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()));
    }

    @Test
    @Transactional
    void getStockMovementsByIdFiltering() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        Long id = stockMovement.getId();

        defaultStockMovementFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStockMovementFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStockMovementFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStockMovementsByMovementDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where movementDate equals to
        defaultStockMovementFiltering("movementDate.equals=" + DEFAULT_MOVEMENT_DATE, "movementDate.equals=" + UPDATED_MOVEMENT_DATE);
    }

    @Test
    @Transactional
    void getAllStockMovementsByMovementDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where movementDate in
        defaultStockMovementFiltering(
            "movementDate.in=" + DEFAULT_MOVEMENT_DATE + "," + UPDATED_MOVEMENT_DATE,
            "movementDate.in=" + UPDATED_MOVEMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllStockMovementsByMovementDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where movementDate is not null
        defaultStockMovementFiltering("movementDate.specified=true", "movementDate.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMovementsByMovementTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where movementType equals to
        defaultStockMovementFiltering("movementType.equals=" + DEFAULT_MOVEMENT_TYPE, "movementType.equals=" + UPDATED_MOVEMENT_TYPE);
    }

    @Test
    @Transactional
    void getAllStockMovementsByMovementTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where movementType in
        defaultStockMovementFiltering(
            "movementType.in=" + DEFAULT_MOVEMENT_TYPE + "," + UPDATED_MOVEMENT_TYPE,
            "movementType.in=" + UPDATED_MOVEMENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllStockMovementsByMovementTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where movementType is not null
        defaultStockMovementFiltering("movementType.specified=true", "movementType.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity equals to
        defaultStockMovementFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity in
        defaultStockMovementFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity is not null
        defaultStockMovementFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity is greater than or equal to
        defaultStockMovementFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity is less than or equal to
        defaultStockMovementFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity is less than
        defaultStockMovementFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity is greater than
        defaultStockMovementFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockMovementsByUnitCostIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where unitCost equals to
        defaultStockMovementFiltering("unitCost.equals=" + DEFAULT_UNIT_COST, "unitCost.equals=" + UPDATED_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllStockMovementsByUnitCostIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where unitCost in
        defaultStockMovementFiltering("unitCost.in=" + DEFAULT_UNIT_COST + "," + UPDATED_UNIT_COST, "unitCost.in=" + UPDATED_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllStockMovementsByUnitCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where unitCost is not null
        defaultStockMovementFiltering("unitCost.specified=true", "unitCost.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMovementsByUnitCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where unitCost is greater than or equal to
        defaultStockMovementFiltering(
            "unitCost.greaterThanOrEqual=" + DEFAULT_UNIT_COST,
            "unitCost.greaterThanOrEqual=" + UPDATED_UNIT_COST
        );
    }

    @Test
    @Transactional
    void getAllStockMovementsByUnitCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where unitCost is less than or equal to
        defaultStockMovementFiltering("unitCost.lessThanOrEqual=" + DEFAULT_UNIT_COST, "unitCost.lessThanOrEqual=" + SMALLER_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllStockMovementsByUnitCostIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where unitCost is less than
        defaultStockMovementFiltering("unitCost.lessThan=" + UPDATED_UNIT_COST, "unitCost.lessThan=" + DEFAULT_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllStockMovementsByUnitCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where unitCost is greater than
        defaultStockMovementFiltering("unitCost.greaterThan=" + SMALLER_UNIT_COST, "unitCost.greaterThan=" + DEFAULT_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllStockMovementsByReferenceNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where referenceNumber equals to
        defaultStockMovementFiltering(
            "referenceNumber.equals=" + DEFAULT_REFERENCE_NUMBER,
            "referenceNumber.equals=" + UPDATED_REFERENCE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllStockMovementsByReferenceNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where referenceNumber in
        defaultStockMovementFiltering(
            "referenceNumber.in=" + DEFAULT_REFERENCE_NUMBER + "," + UPDATED_REFERENCE_NUMBER,
            "referenceNumber.in=" + UPDATED_REFERENCE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllStockMovementsByReferenceNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where referenceNumber is not null
        defaultStockMovementFiltering("referenceNumber.specified=true", "referenceNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMovementsByReferenceNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where referenceNumber contains
        defaultStockMovementFiltering(
            "referenceNumber.contains=" + DEFAULT_REFERENCE_NUMBER,
            "referenceNumber.contains=" + UPDATED_REFERENCE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllStockMovementsByReferenceNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where referenceNumber does not contain
        defaultStockMovementFiltering(
            "referenceNumber.doesNotContain=" + UPDATED_REFERENCE_NUMBER,
            "referenceNumber.doesNotContain=" + DEFAULT_REFERENCE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllStockMovementsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where notes equals to
        defaultStockMovementFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllStockMovementsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where notes in
        defaultStockMovementFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllStockMovementsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where notes is not null
        defaultStockMovementFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMovementsByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where notes contains
        defaultStockMovementFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllStockMovementsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where notes does not contain
        defaultStockMovementFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllStockMovementsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where deletedAt equals to
        defaultStockMovementFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllStockMovementsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where deletedAt in
        defaultStockMovementFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllStockMovementsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where deletedAt is not null
        defaultStockMovementFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    private void defaultStockMovementFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStockMovementShouldBeFound(shouldBeFound);
        defaultStockMovementShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockMovementShouldBeFound(String filter) throws Exception {
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockMovement.getId().intValue())))
            .andExpect(jsonPath("$.[*].movementDate").value(hasItem(DEFAULT_MOVEMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].movementType").value(hasItem(DEFAULT_MOVEMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].unitCost").value(hasItem(sameNumber(DEFAULT_UNIT_COST))))
            .andExpect(jsonPath("$.[*].referenceNumber").value(hasItem(DEFAULT_REFERENCE_NUMBER)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));

        // Check, that the count call also returns 1
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockMovementShouldNotBeFound(String filter) throws Exception {
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStockMovement() throws Exception {
        // Get the stockMovement
        restStockMovementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockMovement() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockMovementSearchRepository.save(stockMovement);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());

        // Update the stockMovement
        StockMovement updatedStockMovement = stockMovementRepository.findById(stockMovement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStockMovement are not directly saved in db
        em.detach(updatedStockMovement);
        updatedStockMovement
            .movementDate(UPDATED_MOVEMENT_DATE)
            .movementType(UPDATED_MOVEMENT_TYPE)
            .quantity(UPDATED_QUANTITY)
            .unitCost(UPDATED_UNIT_COST)
            .referenceNumber(UPDATED_REFERENCE_NUMBER)
            .notes(UPDATED_NOTES)
            .deletedAt(UPDATED_DELETED_AT);
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(updatedStockMovement);

        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockMovementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStockMovementToMatchAllProperties(updatedStockMovement);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<StockMovement> stockMovementSearchList = Streamable.of(stockMovementSearchRepository.findAll()).toList();
                StockMovement testStockMovementSearch = stockMovementSearchList.get(searchDatabaseSizeAfter - 1);

                assertStockMovementAllPropertiesEquals(testStockMovementSearch, updatedStockMovement);
            });
    }

    @Test
    @Transactional
    void putNonExistingStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockMovementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateStockMovementWithPatch() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockMovement using partial update
        StockMovement partialUpdatedStockMovement = new StockMovement();
        partialUpdatedStockMovement.setId(stockMovement.getId());

        partialUpdatedStockMovement.deletedAt(UPDATED_DELETED_AT);

        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockMovement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockMovement))
            )
            .andExpect(status().isOk());

        // Validate the StockMovement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockMovementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStockMovement, stockMovement),
            getPersistedStockMovement(stockMovement)
        );
    }

    @Test
    @Transactional
    void fullUpdateStockMovementWithPatch() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockMovement using partial update
        StockMovement partialUpdatedStockMovement = new StockMovement();
        partialUpdatedStockMovement.setId(stockMovement.getId());

        partialUpdatedStockMovement
            .movementDate(UPDATED_MOVEMENT_DATE)
            .movementType(UPDATED_MOVEMENT_TYPE)
            .quantity(UPDATED_QUANTITY)
            .unitCost(UPDATED_UNIT_COST)
            .referenceNumber(UPDATED_REFERENCE_NUMBER)
            .notes(UPDATED_NOTES)
            .deletedAt(UPDATED_DELETED_AT);

        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockMovement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockMovement))
            )
            .andExpect(status().isOk());

        // Validate the StockMovement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockMovementUpdatableFieldsEquals(partialUpdatedStockMovement, getPersistedStockMovement(partialUpdatedStockMovement));
    }

    @Test
    @Transactional
    void patchNonExistingStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockMovementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteStockMovement() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);
        stockMovementRepository.save(stockMovement);
        stockMovementSearchRepository.save(stockMovement);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the stockMovement
        restStockMovementMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockMovement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMovementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchStockMovement() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);
        stockMovementSearchRepository.save(stockMovement);

        // Search the stockMovement
        restStockMovementMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + stockMovement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockMovement.getId().intValue())))
            .andExpect(jsonPath("$.[*].movementDate").value(hasItem(DEFAULT_MOVEMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].movementType").value(hasItem(DEFAULT_MOVEMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].unitCost").value(hasItem(sameNumber(DEFAULT_UNIT_COST))))
            .andExpect(jsonPath("$.[*].referenceNumber").value(hasItem(DEFAULT_REFERENCE_NUMBER)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return stockMovementRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected StockMovement getPersistedStockMovement(StockMovement stockMovement) {
        return stockMovementRepository.findById(stockMovement.getId()).orElseThrow();
    }

    protected void assertPersistedStockMovementToMatchAllProperties(StockMovement expectedStockMovement) {
        assertStockMovementAllPropertiesEquals(expectedStockMovement, getPersistedStockMovement(expectedStockMovement));
    }

    protected void assertPersistedStockMovementToMatchUpdatableProperties(StockMovement expectedStockMovement) {
        assertStockMovementAllUpdatablePropertiesEquals(expectedStockMovement, getPersistedStockMovement(expectedStockMovement));
    }
}
