package com.github.robsonrjunior.web.rest;

import static com.github.robsonrjunior.domain.RawMaterialAsserts.*;
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
import com.github.robsonrjunior.domain.RawMaterial;
import com.github.robsonrjunior.domain.StockMovement;
import com.github.robsonrjunior.domain.enumeration.UnitOfMeasure;
import com.github.robsonrjunior.repository.RawMaterialRepository;
import com.github.robsonrjunior.repository.search.RawMaterialSearchRepository;
import com.github.robsonrjunior.service.dto.RawMaterialDTO;
import com.github.robsonrjunior.service.mapper.RawMaterialMapper;
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
 * Integration tests for the {@link RawMaterialResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RawMaterialResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SKU = "AAAAAAAAAA";
    private static final String UPDATED_SKU = "BBBBBBBBBB";

    private static final UnitOfMeasure DEFAULT_UNIT_OF_MEASURE = UnitOfMeasure.UNIT;
    private static final UnitOfMeasure UPDATED_UNIT_OF_MEASURE = UnitOfMeasure.KG;

    private static final Integer DEFAULT_UNIT_DECIMAL_PLACES = 0;
    private static final Integer UPDATED_UNIT_DECIMAL_PLACES = 1;
    private static final Integer SMALLER_UNIT_DECIMAL_PLACES = 0 - 1;

    private static final BigDecimal DEFAULT_UNIT_COST = new BigDecimal(0);
    private static final BigDecimal UPDATED_UNIT_COST = new BigDecimal(1);
    private static final BigDecimal SMALLER_UNIT_COST = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_MIN_STOCK = new BigDecimal(0);
    private static final BigDecimal UPDATED_MIN_STOCK = new BigDecimal(1);
    private static final BigDecimal SMALLER_MIN_STOCK = new BigDecimal(0 - 1);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_DELETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/raw-materials";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/raw-materials/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RawMaterialRepository rawMaterialRepository;

    @Autowired
    private RawMaterialMapper rawMaterialMapper;

    @Autowired
    private RawMaterialSearchRepository rawMaterialSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRawMaterialMockMvc;

    private RawMaterial rawMaterial;

    private RawMaterial insertedRawMaterial;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RawMaterial createEntity(EntityManager em) {
        RawMaterial rawMaterial = new RawMaterial()
            .name(DEFAULT_NAME)
            .sku(DEFAULT_SKU)
            .unitOfMeasure(DEFAULT_UNIT_OF_MEASURE)
            .unitDecimalPlaces(DEFAULT_UNIT_DECIMAL_PLACES)
            .unitCost(DEFAULT_UNIT_COST)
            .minStock(DEFAULT_MIN_STOCK)
            .active(DEFAULT_ACTIVE)
            .deletedAt(DEFAULT_DELETED_AT);
        return rawMaterial;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RawMaterial createUpdatedEntity(EntityManager em) {
        RawMaterial updatedRawMaterial = new RawMaterial()
            .name(UPDATED_NAME)
            .sku(UPDATED_SKU)
            .unitOfMeasure(UPDATED_UNIT_OF_MEASURE)
            .unitDecimalPlaces(UPDATED_UNIT_DECIMAL_PLACES)
            .unitCost(UPDATED_UNIT_COST)
            .minStock(UPDATED_MIN_STOCK)
            .active(UPDATED_ACTIVE)
            .deletedAt(UPDATED_DELETED_AT);
        return updatedRawMaterial;
    }

    @BeforeEach
    void initTest() {
        rawMaterial = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedRawMaterial != null) {
            rawMaterialRepository.delete(insertedRawMaterial);
            rawMaterialSearchRepository.delete(insertedRawMaterial);
            insertedRawMaterial = null;
        }
    }

    @Test
    @Transactional
    void createRawMaterial() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);
        var returnedRawMaterialDTO = om.readValue(
            restRawMaterialMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rawMaterialDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RawMaterialDTO.class
        );

        // Validate the RawMaterial in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRawMaterial = rawMaterialMapper.toEntity(returnedRawMaterialDTO);
        assertRawMaterialUpdatableFieldsEquals(returnedRawMaterial, getPersistedRawMaterial(returnedRawMaterial));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedRawMaterial = returnedRawMaterial;
    }

    @Test
    @Transactional
    void createRawMaterialWithExistingId() throws Exception {
        // Create the RawMaterial with an existing ID
        rawMaterial.setId(1L);
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restRawMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rawMaterialDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RawMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        // set the field null
        rawMaterial.setName(null);

        // Create the RawMaterial, which fails.
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        restRawMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rawMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSkuIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        // set the field null
        rawMaterial.setSku(null);

        // Create the RawMaterial, which fails.
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        restRawMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rawMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUnitOfMeasureIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        // set the field null
        rawMaterial.setUnitOfMeasure(null);

        // Create the RawMaterial, which fails.
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        restRawMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rawMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUnitDecimalPlacesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        // set the field null
        rawMaterial.setUnitDecimalPlaces(null);

        // Create the RawMaterial, which fails.
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        restRawMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rawMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        // set the field null
        rawMaterial.setActive(null);

        // Create the RawMaterial, which fails.
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        restRawMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rawMaterialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllRawMaterials() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList
        restRawMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rawMaterial.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].unitOfMeasure").value(hasItem(DEFAULT_UNIT_OF_MEASURE.toString())))
            .andExpect(jsonPath("$.[*].unitDecimalPlaces").value(hasItem(DEFAULT_UNIT_DECIMAL_PLACES)))
            .andExpect(jsonPath("$.[*].unitCost").value(hasItem(sameNumber(DEFAULT_UNIT_COST))))
            .andExpect(jsonPath("$.[*].minStock").value(hasItem(sameNumber(DEFAULT_MIN_STOCK))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));
    }

    @Test
    @Transactional
    void getRawMaterial() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get the rawMaterial
        restRawMaterialMockMvc
            .perform(get(ENTITY_API_URL_ID, rawMaterial.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rawMaterial.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.sku").value(DEFAULT_SKU))
            .andExpect(jsonPath("$.unitOfMeasure").value(DEFAULT_UNIT_OF_MEASURE.toString()))
            .andExpect(jsonPath("$.unitDecimalPlaces").value(DEFAULT_UNIT_DECIMAL_PLACES))
            .andExpect(jsonPath("$.unitCost").value(sameNumber(DEFAULT_UNIT_COST)))
            .andExpect(jsonPath("$.minStock").value(sameNumber(DEFAULT_MIN_STOCK)))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()));
    }

    @Test
    @Transactional
    void getRawMaterialsByIdFiltering() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        Long id = rawMaterial.getId();

        defaultRawMaterialFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRawMaterialFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRawMaterialFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where name equals to
        defaultRawMaterialFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where name in
        defaultRawMaterialFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where name is not null
        defaultRawMaterialFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where name contains
        defaultRawMaterialFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where name does not contain
        defaultRawMaterialFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllRawMaterialsBySkuIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where sku equals to
        defaultRawMaterialFiltering("sku.equals=" + DEFAULT_SKU, "sku.equals=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllRawMaterialsBySkuIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where sku in
        defaultRawMaterialFiltering("sku.in=" + DEFAULT_SKU + "," + UPDATED_SKU, "sku.in=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllRawMaterialsBySkuIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where sku is not null
        defaultRawMaterialFiltering("sku.specified=true", "sku.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsBySkuContainsSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where sku contains
        defaultRawMaterialFiltering("sku.contains=" + DEFAULT_SKU, "sku.contains=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllRawMaterialsBySkuNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where sku does not contain
        defaultRawMaterialFiltering("sku.doesNotContain=" + UPDATED_SKU, "sku.doesNotContain=" + DEFAULT_SKU);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitOfMeasureIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitOfMeasure equals to
        defaultRawMaterialFiltering("unitOfMeasure.equals=" + DEFAULT_UNIT_OF_MEASURE, "unitOfMeasure.equals=" + UPDATED_UNIT_OF_MEASURE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitOfMeasureIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitOfMeasure in
        defaultRawMaterialFiltering(
            "unitOfMeasure.in=" + DEFAULT_UNIT_OF_MEASURE + "," + UPDATED_UNIT_OF_MEASURE,
            "unitOfMeasure.in=" + UPDATED_UNIT_OF_MEASURE
        );
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitOfMeasureIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitOfMeasure is not null
        defaultRawMaterialFiltering("unitOfMeasure.specified=true", "unitOfMeasure.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitDecimalPlacesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitDecimalPlaces equals to
        defaultRawMaterialFiltering(
            "unitDecimalPlaces.equals=" + DEFAULT_UNIT_DECIMAL_PLACES,
            "unitDecimalPlaces.equals=" + UPDATED_UNIT_DECIMAL_PLACES
        );
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitDecimalPlacesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitDecimalPlaces in
        defaultRawMaterialFiltering(
            "unitDecimalPlaces.in=" + DEFAULT_UNIT_DECIMAL_PLACES + "," + UPDATED_UNIT_DECIMAL_PLACES,
            "unitDecimalPlaces.in=" + UPDATED_UNIT_DECIMAL_PLACES
        );
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitDecimalPlacesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitDecimalPlaces is not null
        defaultRawMaterialFiltering("unitDecimalPlaces.specified=true", "unitDecimalPlaces.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitDecimalPlacesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitDecimalPlaces is greater than or equal to
        defaultRawMaterialFiltering(
            "unitDecimalPlaces.greaterThanOrEqual=" + DEFAULT_UNIT_DECIMAL_PLACES,
            "unitDecimalPlaces.greaterThanOrEqual=" + (DEFAULT_UNIT_DECIMAL_PLACES + 1)
        );
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitDecimalPlacesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitDecimalPlaces is less than or equal to
        defaultRawMaterialFiltering(
            "unitDecimalPlaces.lessThanOrEqual=" + DEFAULT_UNIT_DECIMAL_PLACES,
            "unitDecimalPlaces.lessThanOrEqual=" + SMALLER_UNIT_DECIMAL_PLACES
        );
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitDecimalPlacesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitDecimalPlaces is less than
        defaultRawMaterialFiltering(
            "unitDecimalPlaces.lessThan=" + (DEFAULT_UNIT_DECIMAL_PLACES + 1),
            "unitDecimalPlaces.lessThan=" + DEFAULT_UNIT_DECIMAL_PLACES
        );
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitDecimalPlacesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitDecimalPlaces is greater than
        defaultRawMaterialFiltering(
            "unitDecimalPlaces.greaterThan=" + SMALLER_UNIT_DECIMAL_PLACES,
            "unitDecimalPlaces.greaterThan=" + DEFAULT_UNIT_DECIMAL_PLACES
        );
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitCostIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitCost equals to
        defaultRawMaterialFiltering("unitCost.equals=" + DEFAULT_UNIT_COST, "unitCost.equals=" + UPDATED_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitCostIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitCost in
        defaultRawMaterialFiltering("unitCost.in=" + DEFAULT_UNIT_COST + "," + UPDATED_UNIT_COST, "unitCost.in=" + UPDATED_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitCost is not null
        defaultRawMaterialFiltering("unitCost.specified=true", "unitCost.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitCost is greater than or equal to
        defaultRawMaterialFiltering("unitCost.greaterThanOrEqual=" + DEFAULT_UNIT_COST, "unitCost.greaterThanOrEqual=" + UPDATED_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitCost is less than or equal to
        defaultRawMaterialFiltering("unitCost.lessThanOrEqual=" + DEFAULT_UNIT_COST, "unitCost.lessThanOrEqual=" + SMALLER_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitCostIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitCost is less than
        defaultRawMaterialFiltering("unitCost.lessThan=" + UPDATED_UNIT_COST, "unitCost.lessThan=" + DEFAULT_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitCost is greater than
        defaultRawMaterialFiltering("unitCost.greaterThan=" + SMALLER_UNIT_COST, "unitCost.greaterThan=" + DEFAULT_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByMinStockIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where minStock equals to
        defaultRawMaterialFiltering("minStock.equals=" + DEFAULT_MIN_STOCK, "minStock.equals=" + UPDATED_MIN_STOCK);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByMinStockIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where minStock in
        defaultRawMaterialFiltering("minStock.in=" + DEFAULT_MIN_STOCK + "," + UPDATED_MIN_STOCK, "minStock.in=" + UPDATED_MIN_STOCK);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByMinStockIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where minStock is not null
        defaultRawMaterialFiltering("minStock.specified=true", "minStock.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByMinStockIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where minStock is greater than or equal to
        defaultRawMaterialFiltering("minStock.greaterThanOrEqual=" + DEFAULT_MIN_STOCK, "minStock.greaterThanOrEqual=" + UPDATED_MIN_STOCK);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByMinStockIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where minStock is less than or equal to
        defaultRawMaterialFiltering("minStock.lessThanOrEqual=" + DEFAULT_MIN_STOCK, "minStock.lessThanOrEqual=" + SMALLER_MIN_STOCK);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByMinStockIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where minStock is less than
        defaultRawMaterialFiltering("minStock.lessThan=" + UPDATED_MIN_STOCK, "minStock.lessThan=" + DEFAULT_MIN_STOCK);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByMinStockIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where minStock is greater than
        defaultRawMaterialFiltering("minStock.greaterThan=" + SMALLER_MIN_STOCK, "minStock.greaterThan=" + DEFAULT_MIN_STOCK);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where active equals to
        defaultRawMaterialFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where active in
        defaultRawMaterialFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where active is not null
        defaultRawMaterialFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where deletedAt equals to
        defaultRawMaterialFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where deletedAt in
        defaultRawMaterialFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where deletedAt is not null
        defaultRawMaterialFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByStockMovementsIsEqualToSomething() throws Exception {
        StockMovement stockMovements;
        if (TestUtil.findAll(em, StockMovement.class).isEmpty()) {
            rawMaterialRepository.saveAndFlush(rawMaterial);
            stockMovements = StockMovementResourceIT.createEntity(em);
        } else {
            stockMovements = TestUtil.findAll(em, StockMovement.class).get(0);
        }
        em.persist(stockMovements);
        em.flush();
        rawMaterial.setStockMovements(stockMovements);
        rawMaterialRepository.saveAndFlush(rawMaterial);
        Long stockMovementsId = stockMovements.getId();
        // Get all the rawMaterialList where stockMovements equals to stockMovementsId
        defaultRawMaterialShouldBeFound("stockMovementsId.equals=" + stockMovementsId);

        // Get all the rawMaterialList where stockMovements equals to (stockMovementsId + 1)
        defaultRawMaterialShouldNotBeFound("stockMovementsId.equals=" + (stockMovementsId + 1));
    }

    private void defaultRawMaterialFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRawMaterialShouldBeFound(shouldBeFound);
        defaultRawMaterialShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRawMaterialShouldBeFound(String filter) throws Exception {
        restRawMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rawMaterial.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].unitOfMeasure").value(hasItem(DEFAULT_UNIT_OF_MEASURE.toString())))
            .andExpect(jsonPath("$.[*].unitDecimalPlaces").value(hasItem(DEFAULT_UNIT_DECIMAL_PLACES)))
            .andExpect(jsonPath("$.[*].unitCost").value(hasItem(sameNumber(DEFAULT_UNIT_COST))))
            .andExpect(jsonPath("$.[*].minStock").value(hasItem(sameNumber(DEFAULT_MIN_STOCK))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));

        // Check, that the count call also returns 1
        restRawMaterialMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRawMaterialShouldNotBeFound(String filter) throws Exception {
        restRawMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRawMaterialMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRawMaterial() throws Exception {
        // Get the rawMaterial
        restRawMaterialMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRawMaterial() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        rawMaterialSearchRepository.save(rawMaterial);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());

        // Update the rawMaterial
        RawMaterial updatedRawMaterial = rawMaterialRepository.findById(rawMaterial.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRawMaterial are not directly saved in db
        em.detach(updatedRawMaterial);
        updatedRawMaterial
            .name(UPDATED_NAME)
            .sku(UPDATED_SKU)
            .unitOfMeasure(UPDATED_UNIT_OF_MEASURE)
            .unitDecimalPlaces(UPDATED_UNIT_DECIMAL_PLACES)
            .unitCost(UPDATED_UNIT_COST)
            .minStock(UPDATED_MIN_STOCK)
            .active(UPDATED_ACTIVE)
            .deletedAt(UPDATED_DELETED_AT);
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(updatedRawMaterial);

        restRawMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rawMaterialDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rawMaterialDTO))
            )
            .andExpect(status().isOk());

        // Validate the RawMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRawMaterialToMatchAllProperties(updatedRawMaterial);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<RawMaterial> rawMaterialSearchList = Streamable.of(rawMaterialSearchRepository.findAll()).toList();
                RawMaterial testRawMaterialSearch = rawMaterialSearchList.get(searchDatabaseSizeAfter - 1);

                assertRawMaterialAllPropertiesEquals(testRawMaterialSearch, updatedRawMaterial);
            });
    }

    @Test
    @Transactional
    void putNonExistingRawMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        rawMaterial.setId(longCount.incrementAndGet());

        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRawMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rawMaterialDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rawMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchRawMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        rawMaterial.setId(longCount.incrementAndGet());

        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRawMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rawMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRawMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        rawMaterial.setId(longCount.incrementAndGet());

        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRawMaterialMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rawMaterialDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RawMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateRawMaterialWithPatch() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rawMaterial using partial update
        RawMaterial partialUpdatedRawMaterial = new RawMaterial();
        partialUpdatedRawMaterial.setId(rawMaterial.getId());

        partialUpdatedRawMaterial.sku(UPDATED_SKU).unitDecimalPlaces(UPDATED_UNIT_DECIMAL_PLACES).deletedAt(UPDATED_DELETED_AT);

        restRawMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRawMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRawMaterial))
            )
            .andExpect(status().isOk());

        // Validate the RawMaterial in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRawMaterialUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRawMaterial, rawMaterial),
            getPersistedRawMaterial(rawMaterial)
        );
    }

    @Test
    @Transactional
    void fullUpdateRawMaterialWithPatch() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rawMaterial using partial update
        RawMaterial partialUpdatedRawMaterial = new RawMaterial();
        partialUpdatedRawMaterial.setId(rawMaterial.getId());

        partialUpdatedRawMaterial
            .name(UPDATED_NAME)
            .sku(UPDATED_SKU)
            .unitOfMeasure(UPDATED_UNIT_OF_MEASURE)
            .unitDecimalPlaces(UPDATED_UNIT_DECIMAL_PLACES)
            .unitCost(UPDATED_UNIT_COST)
            .minStock(UPDATED_MIN_STOCK)
            .active(UPDATED_ACTIVE)
            .deletedAt(UPDATED_DELETED_AT);

        restRawMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRawMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRawMaterial))
            )
            .andExpect(status().isOk());

        // Validate the RawMaterial in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRawMaterialUpdatableFieldsEquals(partialUpdatedRawMaterial, getPersistedRawMaterial(partialUpdatedRawMaterial));
    }

    @Test
    @Transactional
    void patchNonExistingRawMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        rawMaterial.setId(longCount.incrementAndGet());

        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRawMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rawMaterialDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rawMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRawMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        rawMaterial.setId(longCount.incrementAndGet());

        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRawMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rawMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRawMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        rawMaterial.setId(longCount.incrementAndGet());

        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRawMaterialMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(rawMaterialDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RawMaterial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteRawMaterial() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);
        rawMaterialRepository.save(rawMaterial);
        rawMaterialSearchRepository.save(rawMaterial);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the rawMaterial
        restRawMaterialMockMvc
            .perform(delete(ENTITY_API_URL_ID, rawMaterial.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rawMaterialSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchRawMaterial() throws Exception {
        // Initialize the database
        insertedRawMaterial = rawMaterialRepository.saveAndFlush(rawMaterial);
        rawMaterialSearchRepository.save(rawMaterial);

        // Search the rawMaterial
        restRawMaterialMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + rawMaterial.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rawMaterial.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].unitOfMeasure").value(hasItem(DEFAULT_UNIT_OF_MEASURE.toString())))
            .andExpect(jsonPath("$.[*].unitDecimalPlaces").value(hasItem(DEFAULT_UNIT_DECIMAL_PLACES)))
            .andExpect(jsonPath("$.[*].unitCost").value(hasItem(sameNumber(DEFAULT_UNIT_COST))))
            .andExpect(jsonPath("$.[*].minStock").value(hasItem(sameNumber(DEFAULT_MIN_STOCK))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return rawMaterialRepository.count();
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

    protected RawMaterial getPersistedRawMaterial(RawMaterial rawMaterial) {
        return rawMaterialRepository.findById(rawMaterial.getId()).orElseThrow();
    }

    protected void assertPersistedRawMaterialToMatchAllProperties(RawMaterial expectedRawMaterial) {
        assertRawMaterialAllPropertiesEquals(expectedRawMaterial, getPersistedRawMaterial(expectedRawMaterial));
    }

    protected void assertPersistedRawMaterialToMatchUpdatableProperties(RawMaterial expectedRawMaterial) {
        assertRawMaterialAllUpdatablePropertiesEquals(expectedRawMaterial, getPersistedRawMaterial(expectedRawMaterial));
    }
}
