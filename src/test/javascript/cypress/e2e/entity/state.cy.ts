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

describe('State e2e test', () => {
  const statePageUrl = '/state';
  const statePageUrlPattern = new RegExp('/state(\\?.*)?$');
  let username: string;
  let password: string;
  const stateSample = { name: 'than mmm', code: 'minus digi' };

  let state;
  let country;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/countries',
      body: { name: 'calmo', isoCode: 'upo' },
    }).then(({ body }) => {
      country = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/states+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/states').as('postEntityRequest');
    cy.intercept('DELETE', '/api/states/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/cities', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/countries', {
      statusCode: 200,
      body: [country],
    });
  });

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

  afterEach(() => {
    if (country) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/countries/${country.id}`,
      }).then(() => {
        country = undefined;
      });
    }
  });

  it('States menu should load States page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('state');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('State').should('exist');
    cy.url().should('match', statePageUrlPattern);
  });

  describe('State page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(statePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create State page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/state/new$'));
        cy.getEntityCreateUpdateHeading('State');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/states',
          body: {
            ...stateSample,
            country,
          },
        }).then(({ body }) => {
          state = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/states+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/states?page=0&size=20>; rel="last",<http://localhost/api/states?page=0&size=20>; rel="first"',
              },
              body: [state],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(statePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details State page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('state');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statePageUrlPattern);
      });

      it('edit button click should load edit State page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('State');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statePageUrlPattern);
      });

      it('edit button click should load edit State page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('State');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statePageUrlPattern);
      });

      it('last delete button click should delete instance of State', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('state').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statePageUrlPattern);

        state = undefined;
      });
    });
  });

  describe('new State page', () => {
    beforeEach(() => {
      cy.visit(statePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('State');
    });

    it('should create an instance of State', () => {
      cy.get(`[data-cy="name"]`).type('unlike gosh');
      cy.get(`[data-cy="name"]`).should('have.value', 'unlike gosh');

      cy.get(`[data-cy="code"]`).type('joyously i');
      cy.get(`[data-cy="code"]`).should('have.value', 'joyously i');

      cy.get(`[data-cy="country"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        state = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', statePageUrlPattern);
    });
  });
});
