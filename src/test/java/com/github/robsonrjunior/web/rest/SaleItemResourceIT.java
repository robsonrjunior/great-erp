package com.github.robsonrjunior.web.rest;

import static com.github.robsonrjunior.domain.SaleItemAsserts.*;
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
import com.github.robsonrjunior.domain.SaleItem;
import com.github.robsonrjunior.repository.SaleItemRepository;
import com.github.robsonrjunior.repository.search.SaleItemSearchRepository;
import com.github.robsonrjunior.service.dto.SaleItemDTO;
import com.github.robsonrjunior.service.mapper.SaleItemMapper;
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
 * Integration tests for the {@link SaleItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SaleItemResourceIT {

    private static final BigDecimal DEFAULT_QUANTITY = new BigDecimal(0);
    private static final BigDecimal UPDATED_QUANTITY = new BigDecimal(1);
    private static final BigDecimal SMALLER_QUANTITY = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_UNIT_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_DISCOUNT_AMOUNT = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_LINE_TOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_LINE_TOTAL = new BigDecimal(1);
    private static final BigDecimal SMALLER_LINE_TOTAL = new BigDecimal(0 - 1);

    private static final Instant DEFAULT_DELETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/sale-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/sale-items/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private SaleItemMapper saleItemMapper;

    @Autowired
    private SaleItemSearchRepository saleItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSaleItemMockMvc;

    private SaleItem saleItem;

    private SaleItem insertedSaleItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleItem createEntity(EntityManager em) {
        SaleItem saleItem = new SaleItem()
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT)
            .lineTotal(DEFAULT_LINE_TOTAL)
            .deletedAt(DEFAULT_DELETED_AT);
        return saleItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleItem createUpdatedEntity(EntityManager em) {
        SaleItem updatedSaleItem = new SaleItem()
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .lineTotal(UPDATED_LINE_TOTAL)
            .deletedAt(UPDATED_DELETED_AT);
        return updatedSaleItem;
    }

    @BeforeEach
    void initTest() {
        saleItem = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSaleItem != null) {
            saleItemRepository.delete(insertedSaleItem);
            saleItemSearchRepository.delete(insertedSaleItem);
            insertedSaleItem = null;
        }
    }

    @Test
    @Transactional
    void createSaleItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        // Create the SaleItem
        SaleItemDTO saleItemDTO = saleItemMapper.toDto(saleItem);
        var returnedSaleItemDTO = om.readValue(
            restSaleItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SaleItemDTO.class
        );

        // Validate the SaleItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSaleItem = saleItemMapper.toEntity(returnedSaleItemDTO);
        assertSaleItemUpdatableFieldsEquals(returnedSaleItem, getPersistedSaleItem(returnedSaleItem));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSaleItem = returnedSaleItem;
    }

    @Test
    @Transactional
    void createSaleItemWithExistingId() throws Exception {
        // Create the SaleItem with an existing ID
        saleItem.setId(1L);
        SaleItemDTO saleItemDTO = saleItemMapper.toDto(saleItem);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaleItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SaleItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        // set the field null
        saleItem.setQuantity(null);

        // Create the SaleItem, which fails.
        SaleItemDTO saleItemDTO = saleItemMapper.toDto(saleItem);

        restSaleItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        // set the field null
        saleItem.setUnitPrice(null);

        // Create the SaleItem, which fails.
        SaleItemDTO saleItemDTO = saleItemMapper.toDto(saleItem);

        restSaleItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLineTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        // set the field null
        saleItem.setLineTotal(null);

        // Create the SaleItem, which fails.
        SaleItemDTO saleItemDTO = saleItemMapper.toDto(saleItem);

        restSaleItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSaleItems() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList
        restSaleItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].lineTotal").value(hasItem(sameNumber(DEFAULT_LINE_TOTAL))))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));
    }

    @Test
    @Transactional
    void getSaleItem() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get the saleItem
        restSaleItemMockMvc
            .perform(get(ENTITY_API_URL_ID, saleItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(saleItem.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(sameNumber(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.discountAmount").value(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .andExpect(jsonPath("$.lineTotal").value(sameNumber(DEFAULT_LINE_TOTAL)))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()));
    }

    @Test
    @Transactional
    void getSaleItemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        Long id = saleItem.getId();

        defaultSaleItemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSaleItemFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSaleItemFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSaleItemsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where quantity equals to
        defaultSaleItemFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleItemsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where quantity in
        defaultSaleItemFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleItemsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where quantity is not null
        defaultSaleItemFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleItemsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where quantity is greater than or equal to
        defaultSaleItemFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleItemsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where quantity is less than or equal to
        defaultSaleItemFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleItemsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where quantity is less than
        defaultSaleItemFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleItemsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where quantity is greater than
        defaultSaleItemFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleItemsByUnitPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where unitPrice equals to
        defaultSaleItemFiltering("unitPrice.equals=" + DEFAULT_UNIT_PRICE, "unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleItemsByUnitPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where unitPrice in
        defaultSaleItemFiltering("unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE, "unitPrice.in=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleItemsByUnitPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where unitPrice is not null
        defaultSaleItemFiltering("unitPrice.specified=true", "unitPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleItemsByUnitPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where unitPrice is greater than or equal to
        defaultSaleItemFiltering(
            "unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE,
            "unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllSaleItemsByUnitPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where unitPrice is less than or equal to
        defaultSaleItemFiltering("unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE, "unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleItemsByUnitPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where unitPrice is less than
        defaultSaleItemFiltering("unitPrice.lessThan=" + UPDATED_UNIT_PRICE, "unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleItemsByUnitPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where unitPrice is greater than
        defaultSaleItemFiltering("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE, "unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleItemsByDiscountAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where discountAmount equals to
        defaultSaleItemFiltering("discountAmount.equals=" + DEFAULT_DISCOUNT_AMOUNT, "discountAmount.equals=" + UPDATED_DISCOUNT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSaleItemsByDiscountAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where discountAmount in
        defaultSaleItemFiltering(
            "discountAmount.in=" + DEFAULT_DISCOUNT_AMOUNT + "," + UPDATED_DISCOUNT_AMOUNT,
            "discountAmount.in=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleItemsByDiscountAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where discountAmount is not null
        defaultSaleItemFiltering("discountAmount.specified=true", "discountAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleItemsByDiscountAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where discountAmount is greater than or equal to
        defaultSaleItemFiltering(
            "discountAmount.greaterThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.greaterThanOrEqual=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleItemsByDiscountAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where discountAmount is less than or equal to
        defaultSaleItemFiltering(
            "discountAmount.lessThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.lessThanOrEqual=" + SMALLER_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleItemsByDiscountAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where discountAmount is less than
        defaultSaleItemFiltering(
            "discountAmount.lessThan=" + UPDATED_DISCOUNT_AMOUNT,
            "discountAmount.lessThan=" + DEFAULT_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleItemsByDiscountAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where discountAmount is greater than
        defaultSaleItemFiltering(
            "discountAmount.greaterThan=" + SMALLER_DISCOUNT_AMOUNT,
            "discountAmount.greaterThan=" + DEFAULT_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleItemsByLineTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where lineTotal equals to
        defaultSaleItemFiltering("lineTotal.equals=" + DEFAULT_LINE_TOTAL, "lineTotal.equals=" + UPDATED_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllSaleItemsByLineTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where lineTotal in
        defaultSaleItemFiltering("lineTotal.in=" + DEFAULT_LINE_TOTAL + "," + UPDATED_LINE_TOTAL, "lineTotal.in=" + UPDATED_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllSaleItemsByLineTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where lineTotal is not null
        defaultSaleItemFiltering("lineTotal.specified=true", "lineTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleItemsByLineTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where lineTotal is greater than or equal to
        defaultSaleItemFiltering(
            "lineTotal.greaterThanOrEqual=" + DEFAULT_LINE_TOTAL,
            "lineTotal.greaterThanOrEqual=" + UPDATED_LINE_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSaleItemsByLineTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where lineTotal is less than or equal to
        defaultSaleItemFiltering("lineTotal.lessThanOrEqual=" + DEFAULT_LINE_TOTAL, "lineTotal.lessThanOrEqual=" + SMALLER_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllSaleItemsByLineTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where lineTotal is less than
        defaultSaleItemFiltering("lineTotal.lessThan=" + UPDATED_LINE_TOTAL, "lineTotal.lessThan=" + DEFAULT_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllSaleItemsByLineTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where lineTotal is greater than
        defaultSaleItemFiltering("lineTotal.greaterThan=" + SMALLER_LINE_TOTAL, "lineTotal.greaterThan=" + DEFAULT_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllSaleItemsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where deletedAt equals to
        defaultSaleItemFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllSaleItemsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where deletedAt in
        defaultSaleItemFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllSaleItemsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList where deletedAt is not null
        defaultSaleItemFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    private void defaultSaleItemFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSaleItemShouldBeFound(shouldBeFound);
        defaultSaleItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSaleItemShouldBeFound(String filter) throws Exception {
        restSaleItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].lineTotal").value(hasItem(sameNumber(DEFAULT_LINE_TOTAL))))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));

        // Check, that the count call also returns 1
        restSaleItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSaleItemShouldNotBeFound(String filter) throws Exception {
        restSaleItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSaleItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSaleItem() throws Exception {
        // Get the saleItem
        restSaleItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSaleItem() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleItemSearchRepository.save(saleItem);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());

        // Update the saleItem
        SaleItem updatedSaleItem = saleItemRepository.findById(saleItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSaleItem are not directly saved in db
        em.detach(updatedSaleItem);
        updatedSaleItem
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .lineTotal(UPDATED_LINE_TOTAL)
            .deletedAt(UPDATED_DELETED_AT);
        SaleItemDTO saleItemDTO = saleItemMapper.toDto(updatedSaleItem);

        restSaleItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the SaleItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSaleItemToMatchAllProperties(updatedSaleItem);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SaleItem> saleItemSearchList = Streamable.of(saleItemSearchRepository.findAll()).toList();
                SaleItem testSaleItemSearch = saleItemSearchList.get(searchDatabaseSizeAfter - 1);

                assertSaleItemAllPropertiesEquals(testSaleItemSearch, updatedSaleItem);
            });
    }

    @Test
    @Transactional
    void putNonExistingSaleItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        saleItem.setId(longCount.incrementAndGet());

        // Create the SaleItem
        SaleItemDTO saleItemDTO = saleItemMapper.toDto(saleItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSaleItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        saleItem.setId(longCount.incrementAndGet());

        // Create the SaleItem
        SaleItemDTO saleItemDTO = saleItemMapper.toDto(saleItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSaleItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        saleItem.setId(longCount.incrementAndGet());

        // Create the SaleItem
        SaleItemDTO saleItemDTO = saleItemMapper.toDto(saleItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSaleItemWithPatch() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleItem using partial update
        SaleItem partialUpdatedSaleItem = new SaleItem();
        partialUpdatedSaleItem.setId(saleItem.getId());

        partialUpdatedSaleItem.deletedAt(UPDATED_DELETED_AT);

        restSaleItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSaleItem))
            )
            .andExpect(status().isOk());

        // Validate the SaleItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleItemUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSaleItem, saleItem), getPersistedSaleItem(saleItem));
    }

    @Test
    @Transactional
    void fullUpdateSaleItemWithPatch() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleItem using partial update
        SaleItem partialUpdatedSaleItem = new SaleItem();
        partialUpdatedSaleItem.setId(saleItem.getId());

        partialUpdatedSaleItem
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .lineTotal(UPDATED_LINE_TOTAL)
            .deletedAt(UPDATED_DELETED_AT);

        restSaleItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSaleItem))
            )
            .andExpect(status().isOk());

        // Validate the SaleItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleItemUpdatableFieldsEquals(partialUpdatedSaleItem, getPersistedSaleItem(partialUpdatedSaleItem));
    }

    @Test
    @Transactional
    void patchNonExistingSaleItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        saleItem.setId(longCount.incrementAndGet());

        // Create the SaleItem
        SaleItemDTO saleItemDTO = saleItemMapper.toDto(saleItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, saleItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSaleItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        saleItem.setId(longCount.incrementAndGet());

        // Create the SaleItem
        SaleItemDTO saleItemDTO = saleItemMapper.toDto(saleItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSaleItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        saleItem.setId(longCount.incrementAndGet());

        // Create the SaleItem
        SaleItemDTO saleItemDTO = saleItemMapper.toDto(saleItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(saleItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSaleItem() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);
        saleItemRepository.save(saleItem);
        saleItemSearchRepository.save(saleItem);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the saleItem
        restSaleItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, saleItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSaleItem() throws Exception {
        // Initialize the database
        insertedSaleItem = saleItemRepository.saveAndFlush(saleItem);
        saleItemSearchRepository.save(saleItem);

        // Search the saleItem
        restSaleItemMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + saleItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].lineTotal").value(hasItem(sameNumber(DEFAULT_LINE_TOTAL))))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return saleItemRepository.count();
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

    protected SaleItem getPersistedSaleItem(SaleItem saleItem) {
        return saleItemRepository.findById(saleItem.getId()).orElseThrow();
    }

    protected void assertPersistedSaleItemToMatchAllProperties(SaleItem expectedSaleItem) {
        assertSaleItemAllPropertiesEquals(expectedSaleItem, getPersistedSaleItem(expectedSaleItem));
    }

    protected void assertPersistedSaleItemToMatchUpdatableProperties(SaleItem expectedSaleItem) {
        assertSaleItemAllUpdatablePropertiesEquals(expectedSaleItem, getPersistedSaleItem(expectedSaleItem));
    }
}
