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

describe('RawMaterial e2e test', () => {
  const rawMaterialPageUrl = '/raw-material';
  const rawMaterialPageUrlPattern = new RegExp('/raw-material(\\?.*)?$');
  let username: string;
  let password: string;
  const rawMaterialSample = { name: 'nor', sku: 'hmph mmm', unitOfMeasure: 'KG', unitDecimalPlaces: 2, active: false };

  let rawMaterial;
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
      body: { name: 'supermercado pessimista yowza', code: 'what pish', active: false, deletedAt: '2026-04-08T17:20:48.435Z' },
    }).then(({ body }) => {
      tenant = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/raw-materials+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/raw-materials').as('postEntityRequest');
    cy.intercept('DELETE', '/api/raw-materials/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/stock-movements', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/tenants', {
      statusCode: 200,
      body: [tenant],
    });

    cy.intercept('GET', '/api/suppliers', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (rawMaterial) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/raw-materials/${rawMaterial.id}`,
      }).then(() => {
        rawMaterial = undefined;
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

  it('RawMaterials menu should load RawMaterials page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('raw-material');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('RawMaterial').should('exist');
    cy.url().should('match', rawMaterialPageUrlPattern);
  });

  describe('RawMaterial page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(rawMaterialPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create RawMaterial page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/raw-material/new$'));
        cy.getEntityCreateUpdateHeading('RawMaterial');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rawMaterialPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/raw-materials',
          body: {
            ...rawMaterialSample,
            tenants: [tenant],
          },
        }).then(({ body }) => {
          rawMaterial = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/raw-materials+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/raw-materials?page=0&size=20>; rel="last",<http://localhost/api/raw-materials?page=0&size=20>; rel="first"',
              },
              body: [rawMaterial],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(rawMaterialPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details RawMaterial page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('rawMaterial');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rawMaterialPageUrlPattern);
      });

      it('edit button click should load edit RawMaterial page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RawMaterial');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rawMaterialPageUrlPattern);
      });

      it('edit button click should load edit RawMaterial page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RawMaterial');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rawMaterialPageUrlPattern);
      });

      it('last delete button click should delete instance of RawMaterial', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('rawMaterial').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rawMaterialPageUrlPattern);

        rawMaterial = undefined;
      });
    });
  });

  describe('new RawMaterial page', () => {
    beforeEach(() => {
      cy.visit(rawMaterialPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('RawMaterial');
    });

    it('should create an instance of RawMaterial', () => {
      cy.get(`[data-cy="name"]`).type('participar along');
      cy.get(`[data-cy="name"]`).should('have.value', 'participar along');

      cy.get(`[data-cy="sku"]`).type('near what');
      cy.get(`[data-cy="sku"]`).should('have.value', 'near what');

      cy.get(`[data-cy="unitOfMeasure"]`).select('METER');

      cy.get(`[data-cy="unitDecimalPlaces"]`).type('2');
      cy.get(`[data-cy="unitDecimalPlaces"]`).should('have.value', '2');

      cy.get(`[data-cy="unitCost"]`).type('13969.61');
      cy.get(`[data-cy="unitCost"]`).should('have.value', '13969.61');

      cy.get(`[data-cy="minStock"]`).type('21581.5');
      cy.get(`[data-cy="minStock"]`).should('have.value', '21581.5');

      cy.get(`[data-cy="active"]`).should('not.be.checked');
      cy.get(`[data-cy="active"]`).click();
      cy.get(`[data-cy="active"]`).should('be.checked');

      cy.get(`[data-cy="deletedAt"]`).type('2026-04-08T09:48');
      cy.get(`[data-cy="deletedAt"]`).blur();
      cy.get(`[data-cy="deletedAt"]`).should('have.value', '2026-04-08T09:48');

      cy.get(`[data-cy="tenant"]`).select([0]);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        rawMaterial = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', rawMaterialPageUrlPattern);
    });
  });
});
