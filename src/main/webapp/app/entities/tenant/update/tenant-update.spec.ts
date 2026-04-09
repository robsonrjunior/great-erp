import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { IRawMaterial } from 'app/entities/raw-material/raw-material.model';
import { RawMaterialService } from 'app/entities/raw-material/service/raw-material.service';
import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { ISaleItem } from 'app/entities/sale-item/sale-item.model';
import { SaleItemService } from 'app/entities/sale-item/service/sale-item.service';
import { StockMovementService } from 'app/entities/stock-movement/service/stock-movement.service';
import { IStockMovement } from 'app/entities/stock-movement/stock-movement.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { WarehouseService } from 'app/entities/warehouse/service/warehouse.service';
import { IWarehouse } from 'app/entities/warehouse/warehouse.model';
import { TenantService } from '../service/tenant.service';
import { ITenant } from '../tenant.model';

import { TenantFormService } from './tenant-form.service';
import { TenantUpdate } from './tenant-update';

describe('Tenant Management Update Component', () => {
  let comp: TenantUpdate;
  let fixture: ComponentFixture<TenantUpdate>;
  let activatedRoute: ActivatedRoute;
  let tenantFormService: TenantFormService;
  let tenantService: TenantService;
  let customerService: CustomerService;
  let supplierService: SupplierService;
  let personService: PersonService;
  let companyService: CompanyService;
  let productService: ProductService;
  let rawMaterialService: RawMaterialService;
  let warehouseService: WarehouseService;
  let saleService: SaleService;
  let saleItemService: SaleItemService;
  let stockMovementService: StockMovementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(TenantUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tenantFormService = TestBed.inject(TenantFormService);
    tenantService = TestBed.inject(TenantService);
    customerService = TestBed.inject(CustomerService);
    supplierService = TestBed.inject(SupplierService);
    personService = TestBed.inject(PersonService);
    companyService = TestBed.inject(CompanyService);
    productService = TestBed.inject(ProductService);
    rawMaterialService = TestBed.inject(RawMaterialService);
    warehouseService = TestBed.inject(WarehouseService);
    saleService = TestBed.inject(SaleService);
    saleItemService = TestBed.inject(SaleItemService);
    stockMovementService = TestBed.inject(StockMovementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Customer query and add missing value', () => {
      const tenant: ITenant = { id: 17495 };
      const customers: ICustomer = { id: 26915 };
      tenant.customers = customers;

      const customerCollection: ICustomer[] = [{ id: 26915 }];
      vitest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customers];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      vitest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.customersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Supplier query and add missing value', () => {
      const tenant: ITenant = { id: 17495 };
      const suppliers: ISupplier = { id: 28889 };
      tenant.suppliers = suppliers;

      const supplierCollection: ISupplier[] = [{ id: 28889 }];
      vitest.spyOn(supplierService, 'query').mockReturnValue(of(new HttpResponse({ body: supplierCollection })));
      const additionalSuppliers = [suppliers];
      const expectedCollection: ISupplier[] = [...additionalSuppliers, ...supplierCollection];
      vitest.spyOn(supplierService, 'addSupplierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      expect(supplierService.query).toHaveBeenCalled();
      expect(supplierService.addSupplierToCollectionIfMissing).toHaveBeenCalledWith(
        supplierCollection,
        ...additionalSuppliers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.suppliersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Person query and add missing value', () => {
      const tenant: ITenant = { id: 17495 };
      const people: IPerson = { id: 8101 };
      tenant.people = people;

      const personCollection: IPerson[] = [{ id: 8101 }];
      vitest.spyOn(personService, 'query').mockReturnValue(of(new HttpResponse({ body: personCollection })));
      const additionalPeople = [people];
      const expectedCollection: IPerson[] = [...additionalPeople, ...personCollection];
      vitest.spyOn(personService, 'addPersonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      expect(personService.query).toHaveBeenCalled();
      expect(personService.addPersonToCollectionIfMissing).toHaveBeenCalledWith(
        personCollection,
        ...additionalPeople.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.peopleSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Company query and add missing value', () => {
      const tenant: ITenant = { id: 17495 };
      const companies: ICompany = { id: 29751 };
      tenant.companies = companies;

      const companyCollection: ICompany[] = [{ id: 29751 }];
      vitest.spyOn(companyService, 'query').mockReturnValue(of(new HttpResponse({ body: companyCollection })));
      const additionalCompanies = [companies];
      const expectedCollection: ICompany[] = [...additionalCompanies, ...companyCollection];
      vitest.spyOn(companyService, 'addCompanyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      expect(companyService.query).toHaveBeenCalled();
      expect(companyService.addCompanyToCollectionIfMissing).toHaveBeenCalledWith(
        companyCollection,
        ...additionalCompanies.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.companiesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Product query and add missing value', () => {
      const tenant: ITenant = { id: 17495 };
      const products: IProduct = { id: 21536 };
      tenant.products = products;

      const productCollection: IProduct[] = [{ id: 21536 }];
      vitest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [products];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      vitest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.productsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call RawMaterial query and add missing value', () => {
      const tenant: ITenant = { id: 17495 };
      const rawMaterials: IRawMaterial = { id: 6822 };
      tenant.rawMaterials = rawMaterials;

      const rawMaterialCollection: IRawMaterial[] = [{ id: 6822 }];
      vitest.spyOn(rawMaterialService, 'query').mockReturnValue(of(new HttpResponse({ body: rawMaterialCollection })));
      const additionalRawMaterials = [rawMaterials];
      const expectedCollection: IRawMaterial[] = [...additionalRawMaterials, ...rawMaterialCollection];
      vitest.spyOn(rawMaterialService, 'addRawMaterialToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      expect(rawMaterialService.query).toHaveBeenCalled();
      expect(rawMaterialService.addRawMaterialToCollectionIfMissing).toHaveBeenCalledWith(
        rawMaterialCollection,
        ...additionalRawMaterials.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.rawMaterialsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Warehouse query and add missing value', () => {
      const tenant: ITenant = { id: 17495 };
      const warehouses: IWarehouse = { id: 28652 };
      tenant.warehouses = warehouses;

      const warehouseCollection: IWarehouse[] = [{ id: 28652 }];
      vitest.spyOn(warehouseService, 'query').mockReturnValue(of(new HttpResponse({ body: warehouseCollection })));
      const additionalWarehouses = [warehouses];
      const expectedCollection: IWarehouse[] = [...additionalWarehouses, ...warehouseCollection];
      vitest.spyOn(warehouseService, 'addWarehouseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      expect(warehouseService.query).toHaveBeenCalled();
      expect(warehouseService.addWarehouseToCollectionIfMissing).toHaveBeenCalledWith(
        warehouseCollection,
        ...additionalWarehouses.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.warehousesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Sale query and add missing value', () => {
      const tenant: ITenant = { id: 17495 };
      const sales: ISale = { id: 2908 };
      tenant.sales = sales;

      const saleCollection: ISale[] = [{ id: 2908 }];
      vitest.spyOn(saleService, 'query').mockReturnValue(of(new HttpResponse({ body: saleCollection })));
      const additionalSales = [sales];
      const expectedCollection: ISale[] = [...additionalSales, ...saleCollection];
      vitest.spyOn(saleService, 'addSaleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      expect(saleService.query).toHaveBeenCalled();
      expect(saleService.addSaleToCollectionIfMissing).toHaveBeenCalledWith(
        saleCollection,
        ...additionalSales.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.salesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call SaleItem query and add missing value', () => {
      const tenant: ITenant = { id: 17495 };
      const saleItems: ISaleItem = { id: 25187 };
      tenant.saleItems = saleItems;

      const saleItemCollection: ISaleItem[] = [{ id: 25187 }];
      vitest.spyOn(saleItemService, 'query').mockReturnValue(of(new HttpResponse({ body: saleItemCollection })));
      const additionalSaleItems = [saleItems];
      const expectedCollection: ISaleItem[] = [...additionalSaleItems, ...saleItemCollection];
      vitest.spyOn(saleItemService, 'addSaleItemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      expect(saleItemService.query).toHaveBeenCalled();
      expect(saleItemService.addSaleItemToCollectionIfMissing).toHaveBeenCalledWith(
        saleItemCollection,
        ...additionalSaleItems.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.saleItemsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call StockMovement query and add missing value', () => {
      const tenant: ITenant = { id: 17495 };
      const stockMovements: IStockMovement = { id: 18917 };
      tenant.stockMovements = stockMovements;

      const stockMovementCollection: IStockMovement[] = [{ id: 18917 }];
      vitest.spyOn(stockMovementService, 'query').mockReturnValue(of(new HttpResponse({ body: stockMovementCollection })));
      const additionalStockMovements = [stockMovements];
      const expectedCollection: IStockMovement[] = [...additionalStockMovements, ...stockMovementCollection];
      vitest.spyOn(stockMovementService, 'addStockMovementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      expect(stockMovementService.query).toHaveBeenCalled();
      expect(stockMovementService.addStockMovementToCollectionIfMissing).toHaveBeenCalledWith(
        stockMovementCollection,
        ...additionalStockMovements.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.stockMovementsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const tenant: ITenant = { id: 17495 };
      const customers: ICustomer = { id: 26915 };
      tenant.customers = customers;
      const suppliers: ISupplier = { id: 28889 };
      tenant.suppliers = suppliers;
      const people: IPerson = { id: 8101 };
      tenant.people = people;
      const companies: ICompany = { id: 29751 };
      tenant.companies = companies;
      const products: IProduct = { id: 21536 };
      tenant.products = products;
      const rawMaterials: IRawMaterial = { id: 6822 };
      tenant.rawMaterials = rawMaterials;
      const warehouses: IWarehouse = { id: 28652 };
      tenant.warehouses = warehouses;
      const sales: ISale = { id: 2908 };
      tenant.sales = sales;
      const saleItems: ISaleItem = { id: 25187 };
      tenant.saleItems = saleItems;
      const stockMovements: IStockMovement = { id: 18917 };
      tenant.stockMovements = stockMovements;

      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      expect(comp.customersSharedCollection()).toContainEqual(customers);
      expect(comp.suppliersSharedCollection()).toContainEqual(suppliers);
      expect(comp.peopleSharedCollection()).toContainEqual(people);
      expect(comp.companiesSharedCollection()).toContainEqual(companies);
      expect(comp.productsSharedCollection()).toContainEqual(products);
      expect(comp.rawMaterialsSharedCollection()).toContainEqual(rawMaterials);
      expect(comp.warehousesSharedCollection()).toContainEqual(warehouses);
      expect(comp.salesSharedCollection()).toContainEqual(sales);
      expect(comp.saleItemsSharedCollection()).toContainEqual(saleItems);
      expect(comp.stockMovementsSharedCollection()).toContainEqual(stockMovements);
      expect(comp.tenant).toEqual(tenant);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITenant>();
      const tenant = { id: 2662 };
      vitest.spyOn(tenantFormService, 'getTenant').mockReturnValue(tenant);
      vitest.spyOn(tenantService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tenant);
      saveSubject.complete();

      // THEN
      expect(tenantFormService.getTenant).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tenantService.update).toHaveBeenCalledWith(expect.objectContaining(tenant));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITenant>();
      const tenant = { id: 2662 };
      vitest.spyOn(tenantFormService, 'getTenant').mockReturnValue({ id: null });
      vitest.spyOn(tenantService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenant: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tenant);
      saveSubject.complete();

      // THEN
      expect(tenantFormService.getTenant).toHaveBeenCalled();
      expect(tenantService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITenant>();
      const tenant = { id: 2662 };
      vitest.spyOn(tenantService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tenantService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCustomer', () => {
      it('should forward to customerService', () => {
        const entity = { id: 26915 };
        const entity2 = { id: 21032 };
        vitest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSupplier', () => {
      it('should forward to supplierService', () => {
        const entity = { id: 28889 };
        const entity2 = { id: 5063 };
        vitest.spyOn(supplierService, 'compareSupplier');
        comp.compareSupplier(entity, entity2);
        expect(supplierService.compareSupplier).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePerson', () => {
      it('should forward to personService', () => {
        const entity = { id: 8101 };
        const entity2 = { id: 8051 };
        vitest.spyOn(personService, 'comparePerson');
        comp.comparePerson(entity, entity2);
        expect(personService.comparePerson).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCompany', () => {
      it('should forward to companyService', () => {
        const entity = { id: 29751 };
        const entity2 = { id: 7586 };
        vitest.spyOn(companyService, 'compareCompany');
        comp.compareCompany(entity, entity2);
        expect(companyService.compareCompany).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProduct', () => {
      it('should forward to productService', () => {
        const entity = { id: 21536 };
        const entity2 = { id: 11926 };
        vitest.spyOn(productService, 'compareProduct');
        comp.compareProduct(entity, entity2);
        expect(productService.compareProduct).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareRawMaterial', () => {
      it('should forward to rawMaterialService', () => {
        const entity = { id: 6822 };
        const entity2 = { id: 19276 };
        vitest.spyOn(rawMaterialService, 'compareRawMaterial');
        comp.compareRawMaterial(entity, entity2);
        expect(rawMaterialService.compareRawMaterial).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareWarehouse', () => {
      it('should forward to warehouseService', () => {
        const entity = { id: 28652 };
        const entity2 = { id: 31486 };
        vitest.spyOn(warehouseService, 'compareWarehouse');
        comp.compareWarehouse(entity, entity2);
        expect(warehouseService.compareWarehouse).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSale', () => {
      it('should forward to saleService', () => {
        const entity = { id: 2908 };
        const entity2 = { id: 10270 };
        vitest.spyOn(saleService, 'compareSale');
        comp.compareSale(entity, entity2);
        expect(saleService.compareSale).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSaleItem', () => {
      it('should forward to saleItemService', () => {
        const entity = { id: 25187 };
        const entity2 = { id: 21071 };
        vitest.spyOn(saleItemService, 'compareSaleItem');
        comp.compareSaleItem(entity, entity2);
        expect(saleItemService.compareSaleItem).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareStockMovement', () => {
      it('should forward to stockMovementService', () => {
        const entity = { id: 18917 };
        const entity2 = { id: 1833 };
        vitest.spyOn(stockMovementService, 'compareStockMovement');
        comp.compareStockMovement(entity, entity2);
        expect(stockMovementService.compareStockMovement).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
