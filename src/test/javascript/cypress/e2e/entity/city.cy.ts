import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('City e2e test', () => {
  const cityPageUrl = '/city';
  const cityPageUrlPattern = new RegExp('/city(\\?.*)?$');
  let username: string;
  let password: string;
  // const citySample = {"name":"subir"};

  let city;
  // let state;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/states',
      body: {"name":"considering greatly","code":"whoa whene"},
    }).then(({ body }) => {
      state = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/cities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/cities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/cities/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/suppliers', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/customers', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/people', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/companies', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/warehouses', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/states', {
      statusCode: 200,
      body: [state],
    });

  });
   */

  afterEach(() => {
    if (city) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/cities/${city.id}`,
      }).then(() => {
        city = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (state) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/states/${state.id}`,
      }).then(() => {
        state = undefined;
      });
    }
  });
   */

  it('Cities menu should load Cities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('city');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('City').should('exist');
    cy.url().should('match', cityPageUrlPattern);
  });

  describe('City page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(cityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create City page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/city/new$'));
        cy.getEntityCreateUpdateHeading('City');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/cities',
          body: {
            ...citySample,
            state: state,
          },
        }).then(({ body }) => {
          city = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/cities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/cities?page=0&size=20>; rel="last",<http://localhost/api/cities?page=0&size=20>; rel="first"',
              },
              body: [city],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(cityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(cityPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details City page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('city');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);
      });

      it('edit button click should load edit City page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('City');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);
      });

      it('edit button click should load edit City page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('City');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of City', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('city').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);

        city = undefined;
      });
    });
  });

  describe('new City page', () => {
    beforeEach(() => {
      cy.visit(cityPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('City');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of City', () => {
      cy.get(`[data-cy="name"]`).type('ah rico slowly');
      cy.get(`[data-cy="name"]`).should('have.value', 'ah rico slowly');

      cy.get(`[data-cy="state"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        city = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', cityPageUrlPattern);
    });
  });
});
