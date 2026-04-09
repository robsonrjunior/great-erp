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

describe('StockMovement e2e test', () => {
  const stockMovementPageUrl = '/stock-movement';
  const stockMovementPageUrlPattern = new RegExp('/stock-movement(\\?.*)?$');
  let username: string;
  let password: string;
  // const stockMovementSample = {"movementDate":"2026-04-08T02:53:38.852Z","movementType":"TRANSFER","quantity":22173.93};

  let stockMovement;
  // let tenant;
  // let warehouse;

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
      url: '/api/tenants',
      body: {"name":"thoroughly meaningfully oco","code":"sentir","active":true,"deletedAt":"2026-04-08T20:57:50.712Z"},
    }).then(({ body }) => {
      tenant = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/warehouses',
      body: {"name":"unless","code":"frio","active":true,"deletedAt":"2026-04-08T14:33:00.989Z"},
    }).then(({ body }) => {
      warehouse = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/stock-movements+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/stock-movements').as('postEntityRequest');
    cy.intercept('DELETE', '/api/stock-movements/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/tenants', {
      statusCode: 200,
      body: [tenant],
    });

    cy.intercept('GET', '/api/warehouses', {
      statusCode: 200,
      body: [warehouse],
    });

    cy.intercept('GET', '/api/products', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/raw-materials', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (stockMovement) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/stock-movements/${stockMovement.id}`,
      }).then(() => {
        stockMovement = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (tenant) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tenants/${tenant.id}`,
      }).then(() => {
        tenant = undefined;
      });
    }
    if (warehouse) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/warehouses/${warehouse.id}`,
      }).then(() => {
        warehouse = undefined;
      });
    }
  });
   */

  it('StockMovements menu should load StockMovements page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('stock-movement');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StockMovement').should('exist');
    cy.url().should('match', stockMovementPageUrlPattern);
  });

  describe('StockMovement page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(stockMovementPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StockMovement page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/stock-movement/new$'));
        cy.getEntityCreateUpdateHeading('StockMovement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMovementPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/stock-movements',
          body: {
            ...stockMovementSample,
            tenants: [tenant],
            warehouses: [warehouse],
          },
        }).then(({ body }) => {
          stockMovement = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/stock-movements+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/stock-movements?page=0&size=20>; rel="last",<http://localhost/api/stock-movements?page=0&size=20>; rel="first"',
              },
              body: [stockMovement],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(stockMovementPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(stockMovementPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details StockMovement page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('stockMovement');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMovementPageUrlPattern);
      });

      it('edit button click should load edit StockMovement page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StockMovement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMovementPageUrlPattern);
      });

      it('edit button click should load edit StockMovement page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StockMovement');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMovementPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of StockMovement', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('stockMovement').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMovementPageUrlPattern);

        stockMovement = undefined;
      });
    });
  });

  describe('new StockMovement page', () => {
    beforeEach(() => {
      cy.visit(stockMovementPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StockMovement');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of StockMovement', () => {
      cy.get(`[data-cy="movementDate"]`).type('2026-04-08T11:16');
      cy.get(`[data-cy="movementDate"]`).blur();
      cy.get(`[data-cy="movementDate"]`).should('have.value', '2026-04-08T11:16');

      cy.get(`[data-cy="movementType"]`).select('INBOUND');

      cy.get(`[data-cy="quantity"]`).type('4023.73');
      cy.get(`[data-cy="quantity"]`).should('have.value', '4023.73');

      cy.get(`[data-cy="unitCost"]`).type('14214.01');
      cy.get(`[data-cy="unitCost"]`).should('have.value', '14214.01');

      cy.get(`[data-cy="referenceNumber"]`).type('eek');
      cy.get(`[data-cy="referenceNumber"]`).should('have.value', 'eek');

      cy.get(`[data-cy="notes"]`).type('zowie data');
      cy.get(`[data-cy="notes"]`).should('have.value', 'zowie data');

      cy.get(`[data-cy="deletedAt"]`).type('2026-04-08T15:46');
      cy.get(`[data-cy="deletedAt"]`).blur();
      cy.get(`[data-cy="deletedAt"]`).should('have.value', '2026-04-08T15:46');

      cy.get(`[data-cy="tenant"]`).select([0]);
      cy.get(`[data-cy="warehouse"]`).select([0]);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        stockMovement = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', stockMovementPageUrlPattern);
    });
  });
});
