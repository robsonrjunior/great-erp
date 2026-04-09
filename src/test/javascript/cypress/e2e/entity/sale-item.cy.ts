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

describe('SaleItem e2e test', () => {
  const saleItemPageUrl = '/sale-item';
  const saleItemPageUrlPattern = new RegExp('/sale-item(\\?.*)?$');
  let username: string;
  let password: string;
  // const saleItemSample = {"quantity":32724.72,"unitPrice":25292.53,"lineTotal":18123.08};

  let saleItem;
  // let tenant;
  // let sale;
  // let product;

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
      body: {"name":"down physically instead","code":"woot","active":true,"deletedAt":"2026-04-08T22:38:22.766Z"},
    }).then(({ body }) => {
      tenant = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/sales',
      body: {"saleDate":"2026-04-08T18:07:04.729Z","saleNumber":"whereas","status":"CANCELED","grossAmount":8111.76,"discountAmount":30738.97,"netAmount":26673.75,"notes":"who knottily so","deletedAt":"2026-04-08T10:33:38.614Z"},
    }).then(({ body }) => {
      sale = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/products',
      body: {"name":"construir","sku":"yet yet misturar","unitOfMeasure":"KG","unitDecimalPlaces":0,"salePrice":3043.22,"costPrice":25673.83,"minStock":24865.51,"active":true,"deletedAt":"2026-04-09T00:04:12.698Z"},
    }).then(({ body }) => {
      product = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/sale-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/sale-items').as('postEntityRequest');
    cy.intercept('DELETE', '/api/sale-items/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/tenants', {
      statusCode: 200,
      body: [tenant],
    });

    cy.intercept('GET', '/api/sales', {
      statusCode: 200,
      body: [sale],
    });

    cy.intercept('GET', '/api/products', {
      statusCode: 200,
      body: [product],
    });

  });
   */

  afterEach(() => {
    if (saleItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/sale-items/${saleItem.id}`,
      }).then(() => {
        saleItem = undefined;
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
    if (sale) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/sales/${sale.id}`,
      }).then(() => {
        sale = undefined;
      });
    }
    if (product) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/products/${product.id}`,
      }).then(() => {
        product = undefined;
      });
    }
  });
   */

  it('SaleItems menu should load SaleItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('sale-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SaleItem').should('exist');
    cy.url().should('match', saleItemPageUrlPattern);
  });

  describe('SaleItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(saleItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SaleItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/sale-item/new$'));
        cy.getEntityCreateUpdateHeading('SaleItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', saleItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/sale-items',
          body: {
            ...saleItemSample,
            tenants: [tenant],
            sales: [sale],
            products: [product],
          },
        }).then(({ body }) => {
          saleItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/sale-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/sale-items?page=0&size=20>; rel="last",<http://localhost/api/sale-items?page=0&size=20>; rel="first"',
              },
              body: [saleItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(saleItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(saleItemPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details SaleItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('saleItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', saleItemPageUrlPattern);
      });

      it('edit button click should load edit SaleItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SaleItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', saleItemPageUrlPattern);
      });

      it('edit button click should load edit SaleItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SaleItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', saleItemPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of SaleItem', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('saleItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', saleItemPageUrlPattern);

        saleItem = undefined;
      });
    });
  });

  describe('new SaleItem page', () => {
    beforeEach(() => {
      cy.visit(saleItemPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SaleItem');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of SaleItem', () => {
      cy.get(`[data-cy="quantity"]`).type('19141.46');
      cy.get(`[data-cy="quantity"]`).should('have.value', '19141.46');

      cy.get(`[data-cy="unitPrice"]`).type('21718.52');
      cy.get(`[data-cy="unitPrice"]`).should('have.value', '21718.52');

      cy.get(`[data-cy="discountAmount"]`).type('8377.08');
      cy.get(`[data-cy="discountAmount"]`).should('have.value', '8377.08');

      cy.get(`[data-cy="lineTotal"]`).type('9118.38');
      cy.get(`[data-cy="lineTotal"]`).should('have.value', '9118.38');

      cy.get(`[data-cy="deletedAt"]`).type('2026-04-08T19:24');
      cy.get(`[data-cy="deletedAt"]`).blur();
      cy.get(`[data-cy="deletedAt"]`).should('have.value', '2026-04-08T19:24');

      cy.get(`[data-cy="tenant"]`).select([0]);
      cy.get(`[data-cy="sale"]`).select([0]);
      cy.get(`[data-cy="product"]`).select([0]);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        saleItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', saleItemPageUrlPattern);
    });
  });
});
