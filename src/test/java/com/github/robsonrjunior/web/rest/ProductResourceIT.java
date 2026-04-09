package com.github.robsonrjunior.web.rest;

import static com.github.robsonrjunior.domain.ProductAsserts.*;
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
import com.github.robsonrjunior.domain.Product;
import com.github.robsonrjunior.domain.SaleItem;
import com.github.robsonrjunior.domain.StockMovement;
import com.github.robsonrjunior.domain.enumeration.UnitOfMeasure;
import com.github.robsonrjunior.repository.ProductRepository;
import com.github.robsonrjunior.repository.search.ProductSearchRepository;
import com.github.robsonrjunior.service.dto.ProductDTO;
import com.github.robsonrjunior.service.mapper.ProductMapper;
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
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SKU = "AAAAAAAAAA";
    private static final String UPDATED_SKU = "BBBBBBBBBB";

    private static final UnitOfMeasure DEFAULT_UNIT_OF_MEASURE = UnitOfMeasure.UNIT;
    private static final UnitOfMeasure UPDATED_UNIT_OF_MEASURE = UnitOfMeasure.KG;

    private static final Integer DEFAULT_UNIT_DECIMAL_PLACES = 0;
    private static final Integer UPDATED_UNIT_DECIMAL_PLACES = 1;
    private static final Integer SMALLER_UNIT_DECIMAL_PLACES = 0 - 1;

    private static final BigDecimal DEFAULT_SALE_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_SALE_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_SALE_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_COST_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_COST_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_COST_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_MIN_STOCK = new BigDecimal(0);
    private static final BigDecimal UPDATED_MIN_STOCK = new BigDecimal(1);
    private static final BigDecimal SMALLER_MIN_STOCK = new BigDecimal(0 - 1);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_DELETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/products/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    private Product insertedProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .name(DEFAULT_NAME)
            .sku(DEFAULT_SKU)
            .unitOfMeasure(DEFAULT_UNIT_OF_MEASURE)
            .unitDecimalPlaces(DEFAULT_UNIT_DECIMAL_PLACES)
            .salePrice(DEFAULT_SALE_PRICE)
            .costPrice(DEFAULT_COST_PRICE)
            .minStock(DEFAULT_MIN_STOCK)
            .active(DEFAULT_ACTIVE)
            .deletedAt(DEFAULT_DELETED_AT);
        return product;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product updatedProduct = new Product()
            .name(UPDATED_NAME)
            .sku(UPDATED_SKU)
            .unitOfMeasure(UPDATED_UNIT_OF_MEASURE)
            .unitDecimalPlaces(UPDATED_UNIT_DECIMAL_PLACES)
            .salePrice(UPDATED_SALE_PRICE)
            .costPrice(UPDATED_COST_PRICE)
            .minStock(UPDATED_MIN_STOCK)
            .active(UPDATED_ACTIVE)
            .deletedAt(UPDATED_DELETED_AT);
        return updatedProduct;
    }

    @BeforeEach
    void initTest() {
        product = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedProduct != null) {
            productRepository.delete(insertedProduct);
            productSearchRepository.delete(insertedProduct);
            insertedProduct = null;
        }
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        var returnedProductDTO = om.readValue(
            restProductMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductDTO.class
        );

        // Validate the Product in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProduct = productMapper.toEntity(returnedProductDTO);
        assertProductUpdatableFieldsEquals(returnedProduct, getPersistedProduct(returnedProduct));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedProduct = returnedProduct;
    }

    @Test
    @Transactional
    void createProductWithExistingId() throws Exception {
        // Create the Product with an existing ID
        product.setId(1L);
        ProductDTO productDTO = productMapper.toDto(product);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        // set the field null
        product.setName(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSkuIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        // set the field null
        product.setSku(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUnitOfMeasureIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        // set the field null
        product.setUnitOfMeasure(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUnitDecimalPlacesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        // set the field null
        product.setUnitDecimalPlaces(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSalePriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        // set the field null
        product.setSalePrice(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        // set the field null
        product.setActive(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].unitOfMeasure").value(hasItem(DEFAULT_UNIT_OF_MEASURE.toString())))
            .andExpect(jsonPath("$.[*].unitDecimalPlaces").value(hasItem(DEFAULT_UNIT_DECIMAL_PLACES)))
            .andExpect(jsonPath("$.[*].salePrice").value(hasItem(sameNumber(DEFAULT_SALE_PRICE))))
            .andExpect(jsonPath("$.[*].costPrice").value(hasItem(sameNumber(DEFAULT_COST_PRICE))))
            .andExpect(jsonPath("$.[*].minStock").value(hasItem(sameNumber(DEFAULT_MIN_STOCK))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));
    }

    @Test
    @Transactional
    void getProduct() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc
            .perform(get(ENTITY_API_URL_ID, product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.sku").value(DEFAULT_SKU))
            .andExpect(jsonPath("$.unitOfMeasure").value(DEFAULT_UNIT_OF_MEASURE.toString()))
            .andExpect(jsonPath("$.unitDecimalPlaces").value(DEFAULT_UNIT_DECIMAL_PLACES))
            .andExpect(jsonPath("$.salePrice").value(sameNumber(DEFAULT_SALE_PRICE)))
            .andExpect(jsonPath("$.costPrice").value(sameNumber(DEFAULT_COST_PRICE)))
            .andExpect(jsonPath("$.minStock").value(sameNumber(DEFAULT_MIN_STOCK)))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        Long id = product.getId();

        defaultProductFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProductFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProductFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where name equals to
        defaultProductFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where name in
        defaultProductFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where name is not null
        defaultProductFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where name contains
        defaultProductFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where name does not contain
        defaultProductFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllProductsBySkuIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where sku equals to
        defaultProductFiltering("sku.equals=" + DEFAULT_SKU, "sku.equals=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProductsBySkuIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where sku in
        defaultProductFiltering("sku.in=" + DEFAULT_SKU + "," + UPDATED_SKU, "sku.in=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProductsBySkuIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where sku is not null
        defaultProductFiltering("sku.specified=true", "sku.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsBySkuContainsSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where sku contains
        defaultProductFiltering("sku.contains=" + DEFAULT_SKU, "sku.contains=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProductsBySkuNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where sku does not contain
        defaultProductFiltering("sku.doesNotContain=" + UPDATED_SKU, "sku.doesNotContain=" + DEFAULT_SKU);
    }

    @Test
    @Transactional
    void getAllProductsByUnitOfMeasureIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitOfMeasure equals to
        defaultProductFiltering("unitOfMeasure.equals=" + DEFAULT_UNIT_OF_MEASURE, "unitOfMeasure.equals=" + UPDATED_UNIT_OF_MEASURE);
    }

    @Test
    @Transactional
    void getAllProductsByUnitOfMeasureIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitOfMeasure in
        defaultProductFiltering(
            "unitOfMeasure.in=" + DEFAULT_UNIT_OF_MEASURE + "," + UPDATED_UNIT_OF_MEASURE,
            "unitOfMeasure.in=" + UPDATED_UNIT_OF_MEASURE
        );
    }

    @Test
    @Transactional
    void getAllProductsByUnitOfMeasureIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitOfMeasure is not null
        defaultProductFiltering("unitOfMeasure.specified=true", "unitOfMeasure.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByUnitDecimalPlacesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitDecimalPlaces equals to
        defaultProductFiltering(
            "unitDecimalPlaces.equals=" + DEFAULT_UNIT_DECIMAL_PLACES,
            "unitDecimalPlaces.equals=" + UPDATED_UNIT_DECIMAL_PLACES
        );
    }

    @Test
    @Transactional
    void getAllProductsByUnitDecimalPlacesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitDecimalPlaces in
        defaultProductFiltering(
            "unitDecimalPlaces.in=" + DEFAULT_UNIT_DECIMAL_PLACES + "," + UPDATED_UNIT_DECIMAL_PLACES,
            "unitDecimalPlaces.in=" + UPDATED_UNIT_DECIMAL_PLACES
        );
    }

    @Test
    @Transactional
    void getAllProductsByUnitDecimalPlacesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitDecimalPlaces is not null
        defaultProductFiltering("unitDecimalPlaces.specified=true", "unitDecimalPlaces.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByUnitDecimalPlacesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitDecimalPlaces is greater than or equal to
        defaultProductFiltering(
            "unitDecimalPlaces.greaterThanOrEqual=" + DEFAULT_UNIT_DECIMAL_PLACES,
            "unitDecimalPlaces.greaterThanOrEqual=" + (DEFAULT_UNIT_DECIMAL_PLACES + 1)
        );
    }

    @Test
    @Transactional
    void getAllProductsByUnitDecimalPlacesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitDecimalPlaces is less than or equal to
        defaultProductFiltering(
            "unitDecimalPlaces.lessThanOrEqual=" + DEFAULT_UNIT_DECIMAL_PLACES,
            "unitDecimalPlaces.lessThanOrEqual=" + SMALLER_UNIT_DECIMAL_PLACES
        );
    }

    @Test
    @Transactional
    void getAllProductsByUnitDecimalPlacesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitDecimalPlaces is less than
        defaultProductFiltering(
            "unitDecimalPlaces.lessThan=" + (DEFAULT_UNIT_DECIMAL_PLACES + 1),
            "unitDecimalPlaces.lessThan=" + DEFAULT_UNIT_DECIMAL_PLACES
        );
    }

    @Test
    @Transactional
    void getAllProductsByUnitDecimalPlacesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitDecimalPlaces is greater than
        defaultProductFiltering(
            "unitDecimalPlaces.greaterThan=" + SMALLER_UNIT_DECIMAL_PLACES,
            "unitDecimalPlaces.greaterThan=" + DEFAULT_UNIT_DECIMAL_PLACES
        );
    }

    @Test
    @Transactional
    void getAllProductsBySalePriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where salePrice equals to
        defaultProductFiltering("salePrice.equals=" + DEFAULT_SALE_PRICE, "salePrice.equals=" + UPDATED_SALE_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsBySalePriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where salePrice in
        defaultProductFiltering("salePrice.in=" + DEFAULT_SALE_PRICE + "," + UPDATED_SALE_PRICE, "salePrice.in=" + UPDATED_SALE_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsBySalePriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where salePrice is not null
        defaultProductFiltering("salePrice.specified=true", "salePrice.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsBySalePriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where salePrice is greater than or equal to
        defaultProductFiltering("salePrice.greaterThanOrEqual=" + DEFAULT_SALE_PRICE, "salePrice.greaterThanOrEqual=" + UPDATED_SALE_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsBySalePriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where salePrice is less than or equal to
        defaultProductFiltering("salePrice.lessThanOrEqual=" + DEFAULT_SALE_PRICE, "salePrice.lessThanOrEqual=" + SMALLER_SALE_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsBySalePriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where salePrice is less than
        defaultProductFiltering("salePrice.lessThan=" + UPDATED_SALE_PRICE, "salePrice.lessThan=" + DEFAULT_SALE_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsBySalePriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where salePrice is greater than
        defaultProductFiltering("salePrice.greaterThan=" + SMALLER_SALE_PRICE, "salePrice.greaterThan=" + DEFAULT_SALE_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByCostPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where costPrice equals to
        defaultProductFiltering("costPrice.equals=" + DEFAULT_COST_PRICE, "costPrice.equals=" + UPDATED_COST_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByCostPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where costPrice in
        defaultProductFiltering("costPrice.in=" + DEFAULT_COST_PRICE + "," + UPDATED_COST_PRICE, "costPrice.in=" + UPDATED_COST_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByCostPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where costPrice is not null
        defaultProductFiltering("costPrice.specified=true", "costPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByCostPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where costPrice is greater than or equal to
        defaultProductFiltering("costPrice.greaterThanOrEqual=" + DEFAULT_COST_PRICE, "costPrice.greaterThanOrEqual=" + UPDATED_COST_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByCostPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where costPrice is less than or equal to
        defaultProductFiltering("costPrice.lessThanOrEqual=" + DEFAULT_COST_PRICE, "costPrice.lessThanOrEqual=" + SMALLER_COST_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByCostPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where costPrice is less than
        defaultProductFiltering("costPrice.lessThan=" + UPDATED_COST_PRICE, "costPrice.lessThan=" + DEFAULT_COST_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByCostPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where costPrice is greater than
        defaultProductFiltering("costPrice.greaterThan=" + SMALLER_COST_PRICE, "costPrice.greaterThan=" + DEFAULT_COST_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByMinStockIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where minStock equals to
        defaultProductFiltering("minStock.equals=" + DEFAULT_MIN_STOCK, "minStock.equals=" + UPDATED_MIN_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByMinStockIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where minStock in
        defaultProductFiltering("minStock.in=" + DEFAULT_MIN_STOCK + "," + UPDATED_MIN_STOCK, "minStock.in=" + UPDATED_MIN_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByMinStockIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where minStock is not null
        defaultProductFiltering("minStock.specified=true", "minStock.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByMinStockIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where minStock is greater than or equal to
        defaultProductFiltering("minStock.greaterThanOrEqual=" + DEFAULT_MIN_STOCK, "minStock.greaterThanOrEqual=" + UPDATED_MIN_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByMinStockIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where minStock is less than or equal to
        defaultProductFiltering("minStock.lessThanOrEqual=" + DEFAULT_MIN_STOCK, "minStock.lessThanOrEqual=" + SMALLER_MIN_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByMinStockIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where minStock is less than
        defaultProductFiltering("minStock.lessThan=" + UPDATED_MIN_STOCK, "minStock.lessThan=" + DEFAULT_MIN_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByMinStockIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where minStock is greater than
        defaultProductFiltering("minStock.greaterThan=" + SMALLER_MIN_STOCK, "minStock.greaterThan=" + DEFAULT_MIN_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where active equals to
        defaultProductFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllProductsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where active in
        defaultProductFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllProductsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where active is not null
        defaultProductFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where deletedAt equals to
        defaultProductFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where deletedAt in
        defaultProductFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where deletedAt is not null
        defaultProductFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsBySaleItemsIsEqualToSomething() throws Exception {
        SaleItem saleItems;
        if (TestUtil.findAll(em, SaleItem.class).isEmpty()) {
            productRepository.saveAndFlush(product);
            saleItems = SaleItemResourceIT.createEntity(em);
        } else {
            saleItems = TestUtil.findAll(em, SaleItem.class).get(0);
        }
        em.persist(saleItems);
        em.flush();
        product.setSaleItems(saleItems);
        productRepository.saveAndFlush(product);
        Long saleItemsId = saleItems.getId();
        // Get all the productList where saleItems equals to saleItemsId
        defaultProductShouldBeFound("saleItemsId.equals=" + saleItemsId);

        // Get all the productList where saleItems equals to (saleItemsId + 1)
        defaultProductShouldNotBeFound("saleItemsId.equals=" + (saleItemsId + 1));
    }

    @Test
    @Transactional
    void getAllProductsByStockMovementsIsEqualToSomething() throws Exception {
        StockMovement stockMovements;
        if (TestUtil.findAll(em, StockMovement.class).isEmpty()) {
            productRepository.saveAndFlush(product);
            stockMovements = StockMovementResourceIT.createEntity(em);
        } else {
            stockMovements = TestUtil.findAll(em, StockMovement.class).get(0);
        }
        em.persist(stockMovements);
        em.flush();
        product.setStockMovements(stockMovements);
        productRepository.saveAndFlush(product);
        Long stockMovementsId = stockMovements.getId();
        // Get all the productList where stockMovements equals to stockMovementsId
        defaultProductShouldBeFound("stockMovementsId.equals=" + stockMovementsId);

        // Get all the productList where stockMovements equals to (stockMovementsId + 1)
        defaultProductShouldNotBeFound("stockMovementsId.equals=" + (stockMovementsId + 1));
    }

    private void defaultProductFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProductShouldBeFound(shouldBeFound);
        defaultProductShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].unitOfMeasure").value(hasItem(DEFAULT_UNIT_OF_MEASURE.toString())))
            .andExpect(jsonPath("$.[*].unitDecimalPlaces").value(hasItem(DEFAULT_UNIT_DECIMAL_PLACES)))
            .andExpect(jsonPath("$.[*].salePrice").value(hasItem(sameNumber(DEFAULT_SALE_PRICE))))
            .andExpect(jsonPath("$.[*].costPrice").value(hasItem(sameNumber(DEFAULT_COST_PRICE))))
            .andExpect(jsonPath("$.[*].minStock").value(hasItem(sameNumber(DEFAULT_MIN_STOCK))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));

        // Check, that the count call also returns 1
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProduct() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        productSearchRepository.save(product);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .name(UPDATED_NAME)
            .sku(UPDATED_SKU)
            .unitOfMeasure(UPDATED_UNIT_OF_MEASURE)
            .unitDecimalPlaces(UPDATED_UNIT_DECIMAL_PLACES)
            .salePrice(UPDATED_SALE_PRICE)
            .costPrice(UPDATED_COST_PRICE)
            .minStock(UPDATED_MIN_STOCK)
            .active(UPDATED_ACTIVE)
            .deletedAt(UPDATED_DELETED_AT);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductToMatchAllProperties(updatedProduct);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Product> productSearchList = Streamable.of(productSearchRepository.findAll()).toList();
                Product testProductSearch = productSearchList.get(searchDatabaseSizeAfter - 1);

                assertProductAllPropertiesEquals(testProductSearch, updatedProduct);
            });
    }

    @Test
    @Transactional
    void putNonExistingProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateProductWithPatch() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct.unitOfMeasure(UPDATED_UNIT_OF_MEASURE).salePrice(UPDATED_SALE_PRICE).active(UPDATED_ACTIVE);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProduct, product), getPersistedProduct(product));
    }

    @Test
    @Transactional
    void fullUpdateProductWithPatch() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .name(UPDATED_NAME)
            .sku(UPDATED_SKU)
            .unitOfMeasure(UPDATED_UNIT_OF_MEASURE)
            .unitDecimalPlaces(UPDATED_UNIT_DECIMAL_PLACES)
            .salePrice(UPDATED_SALE_PRICE)
            .costPrice(UPDATED_COST_PRICE)
            .minStock(UPDATED_MIN_STOCK)
            .active(UPDATED_ACTIVE)
            .deletedAt(UPDATED_DELETED_AT);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductUpdatableFieldsEquals(partialUpdatedProduct, getPersistedProduct(partialUpdatedProduct));
    }

    @Test
    @Transactional
    void patchNonExistingProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);
        productRepository.save(product);
        productSearchRepository.save(product);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the product
        restProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, product.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProduct() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);
        productSearchRepository.save(product);

        // Search the product
        restProductMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].unitOfMeasure").value(hasItem(DEFAULT_UNIT_OF_MEASURE.toString())))
            .andExpect(jsonPath("$.[*].unitDecimalPlaces").value(hasItem(DEFAULT_UNIT_DECIMAL_PLACES)))
            .andExpect(jsonPath("$.[*].salePrice").value(hasItem(sameNumber(DEFAULT_SALE_PRICE))))
            .andExpect(jsonPath("$.[*].costPrice").value(hasItem(sameNumber(DEFAULT_COST_PRICE))))
            .andExpect(jsonPath("$.[*].minStock").value(hasItem(sameNumber(DEFAULT_MIN_STOCK))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return productRepository.count();
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

    protected Product getPersistedProduct(Product product) {
        return productRepository.findById(product.getId()).orElseThrow();
    }

    protected void assertPersistedProductToMatchAllProperties(Product expectedProduct) {
        assertProductAllPropertiesEquals(expectedProduct, getPersistedProduct(expectedProduct));
    }

    protected void assertPersistedProductToMatchUpdatableProperties(Product expectedProduct) {
        assertProductAllUpdatablePropertiesEquals(expectedProduct, getPersistedProduct(expectedProduct));
    }
}
