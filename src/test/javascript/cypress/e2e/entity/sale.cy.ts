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

describe('Sale e2e test', () => {
  const salePageUrl = '/sale';
  const salePageUrlPattern = new RegExp('/sale(\\?.*)?$');
  let username: string;
  let password: string;
  // const saleSample = {"saleDate":"2026-04-08T01:51:08.348Z","saleNumber":"azedo for inasmuch","status":"OPEN","grossAmount":26218.91,"netAmount":449.63};

  let sale;
  // let tenant;
  // let customer;

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
      body: {"name":"varrer off","code":"médico investigar even","active":true,"deletedAt":"2026-04-08T06:36:14.454Z"},
    }).then(({ body }) => {
      tenant = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/customers',
      body: {"legalName":"happily otimista","tradeName":"till aberto","taxId":"afirmar grama","partyType":"PERSON","email":"3;kp@f.L7^;{P","phone":"(63) 19472-8684","active":false,"deletedAt":"2026-04-08T21:28:49.342Z"},
    }).then(({ body }) => {
      customer = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/sales+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/sales').as('postEntityRequest');
    cy.intercept('DELETE', '/api/sales/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/sale-items', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/tenants', {
      statusCode: 200,
      body: [tenant],
    });

    cy.intercept('GET', '/api/warehouses', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/customers', {
      statusCode: 200,
      body: [customer],
    });

  });
   */

  afterEach(() => {
    if (sale) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/sales/${sale.id}`,
      }).then(() => {
        sale = undefined;
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
    if (customer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/customers/${customer.id}`,
      }).then(() => {
        customer = undefined;
      });
    }
  });
   */

  it('Sales menu should load Sales page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('sale');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Sale').should('exist');
    cy.url().should('match', salePageUrlPattern);
  });

  describe('Sale page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(salePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Sale page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/sale/new$'));
        cy.getEntityCreateUpdateHeading('Sale');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/sales',
          body: {
            ...saleSample,
            tenants: [tenant],
            customers: [customer],
          },
        }).then(({ body }) => {
          sale = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/sales+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/sales?page=0&size=20>; rel="last",<http://localhost/api/sales?page=0&size=20>; rel="first"',
              },
              body: [sale],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(salePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(salePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Sale page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('sale');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salePageUrlPattern);
      });

      it('edit button click should load edit Sale page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Sale');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salePageUrlPattern);
      });

      it('edit button click should load edit Sale page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Sale');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of Sale', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('sale').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salePageUrlPattern);

        sale = undefined;
      });
    });
  });

  describe('new Sale page', () => {
    beforeEach(() => {
      cy.visit(salePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Sale');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of Sale', () => {
      cy.get(`[data-cy="saleDate"]`).type('2026-04-08T11:54');
      cy.get(`[data-cy="saleDate"]`).blur();
      cy.get(`[data-cy="saleDate"]`).should('have.value', '2026-04-08T11:54');

      cy.get(`[data-cy="saleNumber"]`).type('into juros');
      cy.get(`[data-cy="saleNumber"]`).should('have.value', 'into juros');

      cy.get(`[data-cy="status"]`).select('CONFIRMED');

      cy.get(`[data-cy="grossAmount"]`).type('32261.26');
      cy.get(`[data-cy="grossAmount"]`).should('have.value', '32261.26');

      cy.get(`[data-cy="discountAmount"]`).type('8134.45');
      cy.get(`[data-cy="discountAmount"]`).should('have.value', '8134.45');

      cy.get(`[data-cy="netAmount"]`).type('29635.95');
      cy.get(`[data-cy="netAmount"]`).should('have.value', '29635.95');

      cy.get(`[data-cy="notes"]`).type('what complexo');
      cy.get(`[data-cy="notes"]`).should('have.value', 'what complexo');

      cy.get(`[data-cy="deletedAt"]`).type('2026-04-08T20:54');
      cy.get(`[data-cy="deletedAt"]`).blur();
      cy.get(`[data-cy="deletedAt"]`).should('have.value', '2026-04-08T20:54');

      cy.get(`[data-cy="tenant"]`).select([0]);
      cy.get(`[data-cy="customer"]`).select([0]);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        sale = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', salePageUrlPattern);
    });
  });
});
