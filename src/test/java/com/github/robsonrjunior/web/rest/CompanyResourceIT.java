package com.github.robsonrjunior.web.rest;

import static com.github.robsonrjunior.domain.CompanyAsserts.*;
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
import com.github.robsonrjunior.repository.CompanyRepository;
import com.github.robsonrjunior.repository.search.CompanySearchRepository;
import com.github.robsonrjunior.service.dto.CompanyDTO;
import com.github.robsonrjunior.service.mapper.CompanyMapper;
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
 * Integration tests for the {@link CompanyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CompanyResourceIT {

    private static final String DEFAULT_LEGAL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LEGAL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TRADE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TRADE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CNPJ = "AAAAAAAAAAAAAA";
    private static final String UPDATED_CNPJ = "BBBBBBBBBBBBBB";

    private static final String DEFAULT_STATE_REGISTRATION = "AAAAAAAAAA";
    private static final String UPDATED_STATE_REGISTRATION = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "cR@k|\"q$O.}z|(F-";
    private static final String UPDATED_EMAIL = "*83$.@3#}a'2.B";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_DELETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/companies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/companies/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private CompanySearchRepository companySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompanyMockMvc;

    private Company company;

    private Company insertedCompany;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Company createEntity(EntityManager em) {
        Company company = new Company()
            .legalName(DEFAULT_LEGAL_NAME)
            .tradeName(DEFAULT_TRADE_NAME)
            .cnpj(DEFAULT_CNPJ)
            .stateRegistration(DEFAULT_STATE_REGISTRATION)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .active(DEFAULT_ACTIVE)
            .deletedAt(DEFAULT_DELETED_AT);
        return company;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Company createUpdatedEntity(EntityManager em) {
        Company updatedCompany = new Company()
            .legalName(UPDATED_LEGAL_NAME)
            .tradeName(UPDATED_TRADE_NAME)
            .cnpj(UPDATED_CNPJ)
            .stateRegistration(UPDATED_STATE_REGISTRATION)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .active(UPDATED_ACTIVE)
            .deletedAt(UPDATED_DELETED_AT);
        return updatedCompany;
    }

    @BeforeEach
    void initTest() {
        company = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCompany != null) {
            companyRepository.delete(insertedCompany);
            companySearchRepository.delete(insertedCompany);
            insertedCompany = null;
        }
    }

    @Test
    @Transactional
    void createCompany() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());
        // Create the Company
        CompanyDTO companyDTO = companyMapper.toDto(company);
        var returnedCompanyDTO = om.readValue(
            restCompanyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CompanyDTO.class
        );

        // Validate the Company in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCompany = companyMapper.toEntity(returnedCompanyDTO);
        assertCompanyUpdatableFieldsEquals(returnedCompany, getPersistedCompany(returnedCompany));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCompany = returnedCompany;
    }

    @Test
    @Transactional
    void createCompanyWithExistingId() throws Exception {
        // Create the Company with an existing ID
        company.setId(1L);
        CompanyDTO companyDTO = companyMapper.toDto(company);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompanyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLegalNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());
        // set the field null
        company.setLegalName(null);

        // Create the Company, which fails.
        CompanyDTO companyDTO = companyMapper.toDto(company);

        restCompanyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCnpjIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());
        // set the field null
        company.setCnpj(null);

        // Create the Company, which fails.
        CompanyDTO companyDTO = companyMapper.toDto(company);

        restCompanyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());
        // set the field null
        company.setActive(null);

        // Create the Company, which fails.
        CompanyDTO companyDTO = companyMapper.toDto(company);

        restCompanyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCompanies() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList
        restCompanyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(company.getId().intValue())))
            .andExpect(jsonPath("$.[*].legalName").value(hasItem(DEFAULT_LEGAL_NAME)))
            .andExpect(jsonPath("$.[*].tradeName").value(hasItem(DEFAULT_TRADE_NAME)))
            .andExpect(jsonPath("$.[*].cnpj").value(hasItem(DEFAULT_CNPJ)))
            .andExpect(jsonPath("$.[*].stateRegistration").value(hasItem(DEFAULT_STATE_REGISTRATION)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));
    }

    @Test
    @Transactional
    void getCompany() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get the company
        restCompanyMockMvc
            .perform(get(ENTITY_API_URL_ID, company.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(company.getId().intValue()))
            .andExpect(jsonPath("$.legalName").value(DEFAULT_LEGAL_NAME))
            .andExpect(jsonPath("$.tradeName").value(DEFAULT_TRADE_NAME))
            .andExpect(jsonPath("$.cnpj").value(DEFAULT_CNPJ))
            .andExpect(jsonPath("$.stateRegistration").value(DEFAULT_STATE_REGISTRATION))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()));
    }

    @Test
    @Transactional
    void getCompaniesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        Long id = company.getId();

        defaultCompanyFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCompanyFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCompanyFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCompaniesByLegalNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where legalName equals to
        defaultCompanyFiltering("legalName.equals=" + DEFAULT_LEGAL_NAME, "legalName.equals=" + UPDATED_LEGAL_NAME);
    }

    @Test
    @Transactional
    void getAllCompaniesByLegalNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where legalName in
        defaultCompanyFiltering("legalName.in=" + DEFAULT_LEGAL_NAME + "," + UPDATED_LEGAL_NAME, "legalName.in=" + UPDATED_LEGAL_NAME);
    }

    @Test
    @Transactional
    void getAllCompaniesByLegalNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where legalName is not null
        defaultCompanyFiltering("legalName.specified=true", "legalName.specified=false");
    }

    @Test
    @Transactional
    void getAllCompaniesByLegalNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where legalName contains
        defaultCompanyFiltering("legalName.contains=" + DEFAULT_LEGAL_NAME, "legalName.contains=" + UPDATED_LEGAL_NAME);
    }

    @Test
    @Transactional
    void getAllCompaniesByLegalNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where legalName does not contain
        defaultCompanyFiltering("legalName.doesNotContain=" + UPDATED_LEGAL_NAME, "legalName.doesNotContain=" + DEFAULT_LEGAL_NAME);
    }

    @Test
    @Transactional
    void getAllCompaniesByTradeNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where tradeName equals to
        defaultCompanyFiltering("tradeName.equals=" + DEFAULT_TRADE_NAME, "tradeName.equals=" + UPDATED_TRADE_NAME);
    }

    @Test
    @Transactional
    void getAllCompaniesByTradeNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where tradeName in
        defaultCompanyFiltering("tradeName.in=" + DEFAULT_TRADE_NAME + "," + UPDATED_TRADE_NAME, "tradeName.in=" + UPDATED_TRADE_NAME);
    }

    @Test
    @Transactional
    void getAllCompaniesByTradeNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where tradeName is not null
        defaultCompanyFiltering("tradeName.specified=true", "tradeName.specified=false");
    }

    @Test
    @Transactional
    void getAllCompaniesByTradeNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where tradeName contains
        defaultCompanyFiltering("tradeName.contains=" + DEFAULT_TRADE_NAME, "tradeName.contains=" + UPDATED_TRADE_NAME);
    }

    @Test
    @Transactional
    void getAllCompaniesByTradeNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where tradeName does not contain
        defaultCompanyFiltering("tradeName.doesNotContain=" + UPDATED_TRADE_NAME, "tradeName.doesNotContain=" + DEFAULT_TRADE_NAME);
    }

    @Test
    @Transactional
    void getAllCompaniesByCnpjIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where cnpj equals to
        defaultCompanyFiltering("cnpj.equals=" + DEFAULT_CNPJ, "cnpj.equals=" + UPDATED_CNPJ);
    }

    @Test
    @Transactional
    void getAllCompaniesByCnpjIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where cnpj in
        defaultCompanyFiltering("cnpj.in=" + DEFAULT_CNPJ + "," + UPDATED_CNPJ, "cnpj.in=" + UPDATED_CNPJ);
    }

    @Test
    @Transactional
    void getAllCompaniesByCnpjIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where cnpj is not null
        defaultCompanyFiltering("cnpj.specified=true", "cnpj.specified=false");
    }

    @Test
    @Transactional
    void getAllCompaniesByCnpjContainsSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where cnpj contains
        defaultCompanyFiltering("cnpj.contains=" + DEFAULT_CNPJ, "cnpj.contains=" + UPDATED_CNPJ);
    }

    @Test
    @Transactional
    void getAllCompaniesByCnpjNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where cnpj does not contain
        defaultCompanyFiltering("cnpj.doesNotContain=" + UPDATED_CNPJ, "cnpj.doesNotContain=" + DEFAULT_CNPJ);
    }

    @Test
    @Transactional
    void getAllCompaniesByStateRegistrationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where stateRegistration equals to
        defaultCompanyFiltering(
            "stateRegistration.equals=" + DEFAULT_STATE_REGISTRATION,
            "stateRegistration.equals=" + UPDATED_STATE_REGISTRATION
        );
    }

    @Test
    @Transactional
    void getAllCompaniesByStateRegistrationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where stateRegistration in
        defaultCompanyFiltering(
            "stateRegistration.in=" + DEFAULT_STATE_REGISTRATION + "," + UPDATED_STATE_REGISTRATION,
            "stateRegistration.in=" + UPDATED_STATE_REGISTRATION
        );
    }

    @Test
    @Transactional
    void getAllCompaniesByStateRegistrationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where stateRegistration is not null
        defaultCompanyFiltering("stateRegistration.specified=true", "stateRegistration.specified=false");
    }

    @Test
    @Transactional
    void getAllCompaniesByStateRegistrationContainsSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where stateRegistration contains
        defaultCompanyFiltering(
            "stateRegistration.contains=" + DEFAULT_STATE_REGISTRATION,
            "stateRegistration.contains=" + UPDATED_STATE_REGISTRATION
        );
    }

    @Test
    @Transactional
    void getAllCompaniesByStateRegistrationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where stateRegistration does not contain
        defaultCompanyFiltering(
            "stateRegistration.doesNotContain=" + UPDATED_STATE_REGISTRATION,
            "stateRegistration.doesNotContain=" + DEFAULT_STATE_REGISTRATION
        );
    }

    @Test
    @Transactional
    void getAllCompaniesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where email equals to
        defaultCompanyFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCompaniesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where email in
        defaultCompanyFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCompaniesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where email is not null
        defaultCompanyFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllCompaniesByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where email contains
        defaultCompanyFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCompaniesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where email does not contain
        defaultCompanyFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllCompaniesByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where phone equals to
        defaultCompanyFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllCompaniesByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where phone in
        defaultCompanyFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllCompaniesByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where phone is not null
        defaultCompanyFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllCompaniesByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where phone contains
        defaultCompanyFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllCompaniesByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where phone does not contain
        defaultCompanyFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllCompaniesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where active equals to
        defaultCompanyFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCompaniesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where active in
        defaultCompanyFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCompaniesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where active is not null
        defaultCompanyFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllCompaniesByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where deletedAt equals to
        defaultCompanyFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllCompaniesByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where deletedAt in
        defaultCompanyFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllCompaniesByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        // Get all the companyList where deletedAt is not null
        defaultCompanyFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    private void defaultCompanyFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCompanyShouldBeFound(shouldBeFound);
        defaultCompanyShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCompanyShouldBeFound(String filter) throws Exception {
        restCompanyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(company.getId().intValue())))
            .andExpect(jsonPath("$.[*].legalName").value(hasItem(DEFAULT_LEGAL_NAME)))
            .andExpect(jsonPath("$.[*].tradeName").value(hasItem(DEFAULT_TRADE_NAME)))
            .andExpect(jsonPath("$.[*].cnpj").value(hasItem(DEFAULT_CNPJ)))
            .andExpect(jsonPath("$.[*].stateRegistration").value(hasItem(DEFAULT_STATE_REGISTRATION)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));

        // Check, that the count call also returns 1
        restCompanyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCompanyShouldNotBeFound(String filter) throws Exception {
        restCompanyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCompanyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCompany() throws Exception {
        // Get the company
        restCompanyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCompany() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        companySearchRepository.save(company);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());

        // Update the company
        Company updatedCompany = companyRepository.findById(company.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCompany are not directly saved in db
        em.detach(updatedCompany);
        updatedCompany
            .legalName(UPDATED_LEGAL_NAME)
            .tradeName(UPDATED_TRADE_NAME)
            .cnpj(UPDATED_CNPJ)
            .stateRegistration(UPDATED_STATE_REGISTRATION)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .active(UPDATED_ACTIVE)
            .deletedAt(UPDATED_DELETED_AT);
        CompanyDTO companyDTO = companyMapper.toDto(updatedCompany);

        restCompanyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, companyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Company in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCompanyToMatchAllProperties(updatedCompany);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Company> companySearchList = Streamable.of(companySearchRepository.findAll()).toList();
                Company testCompanySearch = companySearchList.get(searchDatabaseSizeAfter - 1);

                assertCompanyAllPropertiesEquals(testCompanySearch, updatedCompany);
            });
    }

    @Test
    @Transactional
    void putNonExistingCompany() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());
        company.setId(longCount.incrementAndGet());

        // Create the Company
        CompanyDTO companyDTO = companyMapper.toDto(company);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, companyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCompany() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());
        company.setId(longCount.incrementAndGet());

        // Create the Company
        CompanyDTO companyDTO = companyMapper.toDto(company);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(companyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCompany() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());
        company.setId(longCount.incrementAndGet());

        // Create the Company
        CompanyDTO companyDTO = companyMapper.toDto(company);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Company in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCompanyWithPatch() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the company using partial update
        Company partialUpdatedCompany = new Company();
        partialUpdatedCompany.setId(company.getId());

        partialUpdatedCompany
            .cnpj(UPDATED_CNPJ)
            .stateRegistration(UPDATED_STATE_REGISTRATION)
            .email(UPDATED_EMAIL)
            .deletedAt(UPDATED_DELETED_AT);

        restCompanyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompany.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCompany))
            )
            .andExpect(status().isOk());

        // Validate the Company in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCompanyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCompany, company), getPersistedCompany(company));
    }

    @Test
    @Transactional
    void fullUpdateCompanyWithPatch() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the company using partial update
        Company partialUpdatedCompany = new Company();
        partialUpdatedCompany.setId(company.getId());

        partialUpdatedCompany
            .legalName(UPDATED_LEGAL_NAME)
            .tradeName(UPDATED_TRADE_NAME)
            .cnpj(UPDATED_CNPJ)
            .stateRegistration(UPDATED_STATE_REGISTRATION)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .active(UPDATED_ACTIVE)
            .deletedAt(UPDATED_DELETED_AT);

        restCompanyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompany.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCompany))
            )
            .andExpect(status().isOk());

        // Validate the Company in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCompanyUpdatableFieldsEquals(partialUpdatedCompany, getPersistedCompany(partialUpdatedCompany));
    }

    @Test
    @Transactional
    void patchNonExistingCompany() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());
        company.setId(longCount.incrementAndGet());

        // Create the Company
        CompanyDTO companyDTO = companyMapper.toDto(company);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, companyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(companyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCompany() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());
        company.setId(longCount.incrementAndGet());

        // Create the Company
        CompanyDTO companyDTO = companyMapper.toDto(company);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(companyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCompany() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());
        company.setId(longCount.incrementAndGet());

        // Create the Company
        CompanyDTO companyDTO = companyMapper.toDto(company);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(companyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Company in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCompany() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);
        companyRepository.save(company);
        companySearchRepository.save(company);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(companySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the company
        restCompanyMockMvc
            .perform(delete(ENTITY_API_URL_ID, company.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(companySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCompany() throws Exception {
        // Initialize the database
        insertedCompany = companyRepository.saveAndFlush(company);
        companySearchRepository.save(company);

        // Search the company
        restCompanyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + company.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(company.getId().intValue())))
            .andExpect(jsonPath("$.[*].legalName").value(hasItem(DEFAULT_LEGAL_NAME)))
            .andExpect(jsonPath("$.[*].tradeName").value(hasItem(DEFAULT_TRADE_NAME)))
            .andExpect(jsonPath("$.[*].cnpj").value(hasItem(DEFAULT_CNPJ)))
            .andExpect(jsonPath("$.[*].stateRegistration").value(hasItem(DEFAULT_STATE_REGISTRATION)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return companyRepository.count();
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

    protected Company getPersistedCompany(Company company) {
        return companyRepository.findById(company.getId()).orElseThrow();
    }

    protected void assertPersistedCompanyToMatchAllProperties(Company expectedCompany) {
        assertCompanyAllPropertiesEquals(expectedCompany, getPersistedCompany(expectedCompany));
    }

    protected void assertPersistedCompanyToMatchUpdatableProperties(Company expectedCompany) {
        assertCompanyAllUpdatablePropertiesEquals(expectedCompany, getPersistedCompany(expectedCompany));
    }
}
