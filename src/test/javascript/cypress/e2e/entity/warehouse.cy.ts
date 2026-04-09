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

describe('Warehouse e2e test', () => {
  const warehousePageUrl = '/warehouse';
  const warehousePageUrlPattern = new RegExp('/warehouse(\\?.*)?$');
  let username: string;
  let password: string;
  const warehouseSample = { name: 'supposing', code: 'afore', active: false };

  let warehouse;
  let tenant;

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
      url: '/api/tenants',
      body: { name: 'who oh', code: 'calor whoa navio', active: true, deletedAt: '2026-04-08T03:33:07.525Z' },
    }).then(({ body }) => {
      tenant = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/warehouses+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/warehouses').as('postEntityRequest');
    cy.intercept('DELETE', '/api/warehouses/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/stock-movements', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/sales', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/tenants', {
      statusCode: 200,
      body: [tenant],
    });

    cy.intercept('GET', '/api/cities', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (warehouse) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/warehouses/${warehouse.id}`,
      }).then(() => {
        warehouse = undefined;
      });
    }
  });

  afterEach(() => {
    if (tenant) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tenants/${tenant.id}`,
      }).then(() => {
        tenant = undefined;
      });
    }
  });

  it('Warehouses menu should load Warehouses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('warehouse');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Warehouse').should('exist');
    cy.url().should('match', warehousePageUrlPattern);
  });

  describe('Warehouse page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(warehousePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Warehouse page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/warehouse/new$'));
        cy.getEntityCreateUpdateHeading('Warehouse');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', warehousePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/warehouses',
          body: {
            ...warehouseSample,
            tenants: [tenant],
          },
        }).then(({ body }) => {
          warehouse = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/warehouses+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/warehouses?page=0&size=20>; rel="last",<http://localhost/api/warehouses?page=0&size=20>; rel="first"',
              },
              body: [warehouse],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(warehousePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Warehouse page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('warehouse');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', warehousePageUrlPattern);
      });

      it('edit button click should load edit Warehouse page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Warehouse');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', warehousePageUrlPattern);
      });

      it('edit button click should load edit Warehouse page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Warehouse');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', warehousePageUrlPattern);
      });

      it('last delete button click should delete instance of Warehouse', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('warehouse').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', warehousePageUrlPattern);

        warehouse = undefined;
      });
    });
  });

  describe('new Warehouse page', () => {
    beforeEach(() => {
      cy.visit(warehousePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Warehouse');
    });

    it('should create an instance of Warehouse', () => {
      cy.get(`[data-cy="name"]`).type('ha');
      cy.get(`[data-cy="name"]`).should('have.value', 'ha');

      cy.get(`[data-cy="code"]`).type('er ouch internar');
      cy.get(`[data-cy="code"]`).should('have.value', 'er ouch internar');

      cy.get(`[data-cy="active"]`).should('not.be.checked');
      cy.get(`[data-cy="active"]`).click();
      cy.get(`[data-cy="active"]`).should('be.checked');

      cy.get(`[data-cy="deletedAt"]`).type('2026-04-08T19:15');
      cy.get(`[data-cy="deletedAt"]`).blur();
      cy.get(`[data-cy="deletedAt"]`).should('have.value', '2026-04-08T19:15');

      cy.get(`[data-cy="tenant"]`).select([0]);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        warehouse = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', warehousePageUrlPattern);
    });
  });
});
