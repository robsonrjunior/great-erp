package com.github.robsonrjunior.web.rest;

import static com.github.robsonrjunior.domain.TenantAsserts.*;
import static com.github.robsonrjunior.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.robsonrjunior.IntegrationTest;
import com.github.robsonrjunior.domain.Company;
import com.github.robsonrjunior.domain.Customer;
import com.github.robsonrjunior.domain.Person;
import com.github.robsonrjunior.domain.Product;
import com.github.robsonrjunior.domain.RawMaterial;
import com.github.robsonrjunior.domain.Sale;
import com.github.robsonrjunior.domain.SaleItem;
import com.github.robsonrjunior.domain.StockMovement;
import com.github.robsonrjunior.domain.Supplier;
import com.github.robsonrjunior.domain.Tenant;
import com.github.robsonrjunior.domain.Warehouse;
import com.github.robsonrjunior.repository.TenantRepository;
import com.github.robsonrjunior.repository.search.TenantSearchRepository;
import com.github.robsonrjunior.service.dto.TenantDTO;
import com.github.robsonrjunior.service.mapper.TenantMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link TenantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TenantResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_DELETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/tenants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/tenants/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private TenantSearchRepository tenantSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTenantMockMvc;

    private Tenant tenant;

    private Tenant insertedTenant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tenant createEntity() {
        return new Tenant().name(DEFAULT_NAME).code(DEFAULT_CODE).active(DEFAULT_ACTIVE).deletedAt(DEFAULT_DELETED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tenant createUpdatedEntity() {
        return new Tenant().name(UPDATED_NAME).code(UPDATED_CODE).active(UPDATED_ACTIVE).deletedAt(UPDATED_DELETED_AT);
    }

    @BeforeEach
    void initTest() {
        tenant = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTenant != null) {
            tenantRepository.delete(insertedTenant);
            tenantSearchRepository.delete(insertedTenant);
            insertedTenant = null;
        }
    }

    @Test
    @Transactional
    void createTenant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        // Create the Tenant
        TenantDTO tenantDTO = tenantMapper.toDto(tenant);
        var returnedTenantDTO = om.readValue(
            restTenantMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TenantDTO.class
        );

        // Validate the Tenant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTenant = tenantMapper.toEntity(returnedTenantDTO);
        assertTenantUpdatableFieldsEquals(returnedTenant, getPersistedTenant(returnedTenant));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTenant = returnedTenant;
    }

    @Test
    @Transactional
    void createTenantWithExistingId() throws Exception {
        // Create the Tenant with an existing ID
        tenant.setId(1L);
        TenantDTO tenantDTO = tenantMapper.toDto(tenant);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tenant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        // set the field null
        tenant.setName(null);

        // Create the Tenant, which fails.
        TenantDTO tenantDTO = tenantMapper.toDto(tenant);

        restTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        // set the field null
        tenant.setCode(null);

        // Create the Tenant, which fails.
        TenantDTO tenantDTO = tenantMapper.toDto(tenant);

        restTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        // set the field null
        tenant.setActive(null);

        // Create the Tenant, which fails.
        TenantDTO tenantDTO = tenantMapper.toDto(tenant);

        restTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTenants() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList
        restTenantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenant.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));
    }

    @Test
    @Transactional
    void getTenant() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get the tenant
        restTenantMockMvc
            .perform(get(ENTITY_API_URL_ID, tenant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tenant.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()));
    }

    @Test
    @Transactional
    void getTenantsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        Long id = tenant.getId();

        defaultTenantFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTenantFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTenantFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTenantsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where name equals to
        defaultTenantFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTenantsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where name in
        defaultTenantFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTenantsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where name is not null
        defaultTenantFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where name contains
        defaultTenantFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTenantsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where name does not contain
        defaultTenantFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllTenantsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where code equals to
        defaultTenantFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTenantsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where code in
        defaultTenantFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTenantsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where code is not null
        defaultTenantFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where code contains
        defaultTenantFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTenantsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where code does not contain
        defaultTenantFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllTenantsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where active equals to
        defaultTenantFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTenantsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where active in
        defaultTenantFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTenantsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where active is not null
        defaultTenantFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where deletedAt equals to
        defaultTenantFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllTenantsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where deletedAt in
        defaultTenantFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllTenantsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList where deletedAt is not null
        defaultTenantFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTenantsByCustomersIsEqualToSomething() throws Exception {
        Customer customers;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            tenantRepository.saveAndFlush(tenant);
            customers = CustomerResourceIT.createEntity(em);
        } else {
            customers = TestUtil.findAll(em, Customer.class).get(0);
        }
        em.persist(customers);
        em.flush();
        tenant.setCustomers(customers);
        tenantRepository.saveAndFlush(tenant);
        Long customersId = customers.getId();
        // Get all the tenantList where customers equals to customersId
        defaultTenantShouldBeFound("customersId.equals=" + customersId);

        // Get all the tenantList where customers equals to (customersId + 1)
        defaultTenantShouldNotBeFound("customersId.equals=" + (customersId + 1));
    }

    @Test
    @Transactional
    void getAllTenantsBySuppliersIsEqualToSomething() throws Exception {
        Supplier suppliers;
        if (TestUtil.findAll(em, Supplier.class).isEmpty()) {
            tenantRepository.saveAndFlush(tenant);
            suppliers = SupplierResourceIT.createEntity(em);
        } else {
            suppliers = TestUtil.findAll(em, Supplier.class).get(0);
        }
        em.persist(suppliers);
        em.flush();
        tenant.setSuppliers(suppliers);
        tenantRepository.saveAndFlush(tenant);
        Long suppliersId = suppliers.getId();
        // Get all the tenantList where suppliers equals to suppliersId
        defaultTenantShouldBeFound("suppliersId.equals=" + suppliersId);

        // Get all the tenantList where suppliers equals to (suppliersId + 1)
        defaultTenantShouldNotBeFound("suppliersId.equals=" + (suppliersId + 1));
    }

    @Test
    @Transactional
    void getAllTenantsByPeopleIsEqualToSomething() throws Exception {
        Person people;
        if (TestUtil.findAll(em, Person.class).isEmpty()) {
            tenantRepository.saveAndFlush(tenant);
            people = PersonResourceIT.createEntity(em);
        } else {
            people = TestUtil.findAll(em, Person.class).get(0);
        }
        em.persist(people);
        em.flush();
        tenant.setPeople(people);
        tenantRepository.saveAndFlush(tenant);
        Long peopleId = people.getId();
        // Get all the tenantList where people equals to peopleId
        defaultTenantShouldBeFound("peopleId.equals=" + peopleId);

        // Get all the tenantList where people equals to (peopleId + 1)
        defaultTenantShouldNotBeFound("peopleId.equals=" + (peopleId + 1));
    }

    @Test
    @Transactional
    void getAllTenantsByCompaniesIsEqualToSomething() throws Exception {
        Company companies;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            tenantRepository.saveAndFlush(tenant);
            companies = CompanyResourceIT.createEntity(em);
        } else {
            companies = TestUtil.findAll(em, Company.class).get(0);
        }
        em.persist(companies);
        em.flush();
        tenant.setCompanies(companies);
        tenantRepository.saveAndFlush(tenant);
        Long companiesId = companies.getId();
        // Get all the tenantList where companies equals to companiesId
        defaultTenantShouldBeFound("companiesId.equals=" + companiesId);

        // Get all the tenantList where companies equals to (companiesId + 1)
        defaultTenantShouldNotBeFound("companiesId.equals=" + (companiesId + 1));
    }

    @Test
    @Transactional
    void getAllTenantsByProductsIsEqualToSomething() throws Exception {
        Product products;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            tenantRepository.saveAndFlush(tenant);
            products = ProductResourceIT.createEntity(em);
        } else {
            products = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(products);
        em.flush();
        tenant.setProducts(products);
        tenantRepository.saveAndFlush(tenant);
        Long productsId = products.getId();
        // Get all the tenantList where products equals to productsId
        defaultTenantShouldBeFound("productsId.equals=" + productsId);

        // Get all the tenantList where products equals to (productsId + 1)
        defaultTenantShouldNotBeFound("productsId.equals=" + (productsId + 1));
    }

    @Test
    @Transactional
    void getAllTenantsByRawMaterialsIsEqualToSomething() throws Exception {
        RawMaterial rawMaterials;
        if (TestUtil.findAll(em, RawMaterial.class).isEmpty()) {
            tenantRepository.saveAndFlush(tenant);
            rawMaterials = RawMaterialResourceIT.createEntity(em);
        } else {
            rawMaterials = TestUtil.findAll(em, RawMaterial.class).get(0);
        }
        em.persist(rawMaterials);
        em.flush();
        tenant.setRawMaterials(rawMaterials);
        tenantRepository.saveAndFlush(tenant);
        Long rawMaterialsId = rawMaterials.getId();
        // Get all the tenantList where rawMaterials equals to rawMaterialsId
        defaultTenantShouldBeFound("rawMaterialsId.equals=" + rawMaterialsId);

        // Get all the tenantList where rawMaterials equals to (rawMaterialsId + 1)
        defaultTenantShouldNotBeFound("rawMaterialsId.equals=" + (rawMaterialsId + 1));
    }

    @Test
    @Transactional
    void getAllTenantsByWarehousesIsEqualToSomething() throws Exception {
        Warehouse warehouses;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            tenantRepository.saveAndFlush(tenant);
            warehouses = WarehouseResourceIT.createEntity(em);
        } else {
            warehouses = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        em.persist(warehouses);
        em.flush();
        tenant.setWarehouses(warehouses);
        tenantRepository.saveAndFlush(tenant);
        Long warehousesId = warehouses.getId();
        // Get all the tenantList where warehouses equals to warehousesId
        defaultTenantShouldBeFound("warehousesId.equals=" + warehousesId);

        // Get all the tenantList where warehouses equals to (warehousesId + 1)
        defaultTenantShouldNotBeFound("warehousesId.equals=" + (warehousesId + 1));
    }

    @Test
    @Transactional
    void getAllTenantsBySalesIsEqualToSomething() throws Exception {
        Sale sales;
        if (TestUtil.findAll(em, Sale.class).isEmpty()) {
            tenantRepository.saveAndFlush(tenant);
            sales = SaleResourceIT.createEntity(em);
        } else {
            sales = TestUtil.findAll(em, Sale.class).get(0);
        }
        em.persist(sales);
        em.flush();
        tenant.setSales(sales);
        tenantRepository.saveAndFlush(tenant);
        Long salesId = sales.getId();
        // Get all the tenantList where sales equals to salesId
        defaultTenantShouldBeFound("salesId.equals=" + salesId);

        // Get all the tenantList where sales equals to (salesId + 1)
        defaultTenantShouldNotBeFound("salesId.equals=" + (salesId + 1));
    }

    @Test
    @Transactional
    void getAllTenantsBySaleItemsIsEqualToSomething() throws Exception {
        SaleItem saleItems;
        if (TestUtil.findAll(em, SaleItem.class).isEmpty()) {
            tenantRepository.saveAndFlush(tenant);
            saleItems = SaleItemResourceIT.createEntity(em);
        } else {
            saleItems = TestUtil.findAll(em, SaleItem.class).get(0);
        }
        em.persist(saleItems);
        em.flush();
        tenant.setSaleItems(saleItems);
        tenantRepository.saveAndFlush(tenant);
        Long saleItemsId = saleItems.getId();
        // Get all the tenantList where saleItems equals to saleItemsId
        defaultTenantShouldBeFound("saleItemsId.equals=" + saleItemsId);

        // Get all the tenantList where saleItems equals to (saleItemsId + 1)
        defaultTenantShouldNotBeFound("saleItemsId.equals=" + (saleItemsId + 1));
    }

    @Test
    @Transactional
    void getAllTenantsByStockMovementsIsEqualToSomething() throws Exception {
        StockMovement stockMovements;
        if (TestUtil.findAll(em, StockMovement.class).isEmpty()) {
            tenantRepository.saveAndFlush(tenant);
            stockMovements = StockMovementResourceIT.createEntity(em);
        } else {
            stockMovements = TestUtil.findAll(em, StockMovement.class).get(0);
        }
        em.persist(stockMovements);
        em.flush();
        tenant.setStockMovements(stockMovements);
        tenantRepository.saveAndFlush(tenant);
        Long stockMovementsId = stockMovements.getId();
        // Get all the tenantList where stockMovements equals to stockMovementsId
        defaultTenantShouldBeFound("stockMovementsId.equals=" + stockMovementsId);

        // Get all the tenantList where stockMovements equals to (stockMovementsId + 1)
        defaultTenantShouldNotBeFound("stockMovementsId.equals=" + (stockMovementsId + 1));
    }

    private void defaultTenantFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTenantShouldBeFound(shouldBeFound);
        defaultTenantShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTenantShouldBeFound(String filter) throws Exception {
        restTenantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenant.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));

        // Check, that the count call also returns 1
        restTenantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTenantShouldNotBeFound(String filter) throws Exception {
        restTenantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTenantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTenant() throws Exception {
        // Get the tenant
        restTenantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTenant() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        tenantSearchRepository.save(tenant);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());

        // Update the tenant
        Tenant updatedTenant = tenantRepository.findById(tenant.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTenant are not directly saved in db
        em.detach(updatedTenant);
        updatedTenant.name(UPDATED_NAME).code(UPDATED_CODE).active(UPDATED_ACTIVE).deletedAt(UPDATED_DELETED_AT);
        TenantDTO tenantDTO = tenantMapper.toDto(updatedTenant);

        restTenantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tenantDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDTO))
            )
            .andExpect(status().isOk());

        // Validate the Tenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTenantToMatchAllProperties(updatedTenant);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Tenant> tenantSearchList = Streamable.of(tenantSearchRepository.findAll()).toList();
                Tenant testTenantSearch = tenantSearchList.get(searchDatabaseSizeAfter - 1);

                assertTenantAllPropertiesEquals(testTenantSearch, updatedTenant);
            });
    }

    @Test
    @Transactional
    void putNonExistingTenant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        tenant.setId(longCount.incrementAndGet());

        // Create the Tenant
        TenantDTO tenantDTO = tenantMapper.toDto(tenant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tenantDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTenant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        tenant.setId(longCount.incrementAndGet());

        // Create the Tenant
        TenantDTO tenantDTO = tenantMapper.toDto(tenant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tenantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTenant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        tenant.setId(longCount.incrementAndGet());

        // Create the Tenant
        TenantDTO tenantDTO = tenantMapper.toDto(tenant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tenantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTenantWithPatch() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenant using partial update
        Tenant partialUpdatedTenant = new Tenant();
        partialUpdatedTenant.setId(tenant.getId());

        partialUpdatedTenant.name(UPDATED_NAME).active(UPDATED_ACTIVE).deletedAt(UPDATED_DELETED_AT);

        restTenantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTenant))
            )
            .andExpect(status().isOk());

        // Validate the Tenant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTenantUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTenant, tenant), getPersistedTenant(tenant));
    }

    @Test
    @Transactional
    void fullUpdateTenantWithPatch() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tenant using partial update
        Tenant partialUpdatedTenant = new Tenant();
        partialUpdatedTenant.setId(tenant.getId());

        partialUpdatedTenant.name(UPDATED_NAME).code(UPDATED_CODE).active(UPDATED_ACTIVE).deletedAt(UPDATED_DELETED_AT);

        restTenantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTenant))
            )
            .andExpect(status().isOk());

        // Validate the Tenant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTenantUpdatableFieldsEquals(partialUpdatedTenant, getPersistedTenant(partialUpdatedTenant));
    }

    @Test
    @Transactional
    void patchNonExistingTenant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        tenant.setId(longCount.incrementAndGet());

        // Create the Tenant
        TenantDTO tenantDTO = tenantMapper.toDto(tenant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tenantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tenantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTenant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        tenant.setId(longCount.incrementAndGet());

        // Create the Tenant
        TenantDTO tenantDTO = tenantMapper.toDto(tenant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tenantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTenant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        tenant.setId(longCount.incrementAndGet());

        // Create the Tenant
        TenantDTO tenantDTO = tenantMapper.toDto(tenant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tenantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tenant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTenant() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);
        tenantRepository.save(tenant);
        tenantSearchRepository.save(tenant);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the tenant
        restTenantMockMvc
            .perform(delete(ENTITY_API_URL_ID, tenant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tenantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTenant() throws Exception {
        // Initialize the database
        insertedTenant = tenantRepository.saveAndFlush(tenant);
        tenantSearchRepository.save(tenant);

        // Search the tenant
        restTenantMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + tenant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenant.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return tenantRepository.count();
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

    protected Tenant getPersistedTenant(Tenant tenant) {
        return tenantRepository.findById(tenant.getId()).orElseThrow();
    }

    protected void assertPersistedTenantToMatchAllProperties(Tenant expectedTenant) {
        assertTenantAllPropertiesEquals(expectedTenant, getPersistedTenant(expectedTenant));
    }

    protected void assertPersistedTenantToMatchUpdatableProperties(Tenant expectedTenant) {
        assertTenantAllUpdatablePropertiesEquals(expectedTenant, getPersistedTenant(expectedTenant));
    }
}
