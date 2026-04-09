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
import { StateService } from 'app/entities/state/service/state.service';
import { IState } from 'app/entities/state/state.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { WarehouseService } from 'app/entities/warehouse/service/warehouse.service';
import { IWarehouse } from 'app/entities/warehouse/warehouse.model';
import { ICity } from '../city.model';
import { CityService } from '../service/city.service';

import { CityFormService } from './city-form.service';
import { CityUpdate } from './city-update';

describe('City Management Update Component', () => {
  let comp: CityUpdate;
  let fixture: ComponentFixture<CityUpdate>;
  let activatedRoute: ActivatedRoute;
  let cityFormService: CityFormService;
  let cityService: CityService;
  let supplierService: SupplierService;
  let customerService: CustomerService;
  let personService: PersonService;
  let companyService: CompanyService;
  let warehouseService: WarehouseService;
  let stateService: StateService;

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

    fixture = TestBed.createComponent(CityUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cityFormService = TestBed.inject(CityFormService);
    cityService = TestBed.inject(CityService);
    supplierService = TestBed.inject(SupplierService);
    customerService = TestBed.inject(CustomerService);
    personService = TestBed.inject(PersonService);
    companyService = TestBed.inject(CompanyService);
    warehouseService = TestBed.inject(WarehouseService);
    stateService = TestBed.inject(StateService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Supplier query and add missing value', () => {
      const city: ICity = { id: 8029 };
      const suppliers: ISupplier = { id: 28889 };
      city.suppliers = suppliers;

      const supplierCollection: ISupplier[] = [{ id: 28889 }];
      vitest.spyOn(supplierService, 'query').mockReturnValue(of(new HttpResponse({ body: supplierCollection })));
      const additionalSuppliers = [suppliers];
      const expectedCollection: ISupplier[] = [...additionalSuppliers, ...supplierCollection];
      vitest.spyOn(supplierService, 'addSupplierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ city });
      comp.ngOnInit();

      expect(supplierService.query).toHaveBeenCalled();
      expect(supplierService.addSupplierToCollectionIfMissing).toHaveBeenCalledWith(
        supplierCollection,
        ...additionalSuppliers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.suppliersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Customer query and add missing value', () => {
      const city: ICity = { id: 8029 };
      const customers: ICustomer = { id: 26915 };
      city.customers = customers;

      const customerCollection: ICustomer[] = [{ id: 26915 }];
      vitest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customers];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      vitest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ city });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.customersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Person query and add missing value', () => {
      const city: ICity = { id: 8029 };
      const people: IPerson = { id: 8101 };
      city.people = people;

      const personCollection: IPerson[] = [{ id: 8101 }];
      vitest.spyOn(personService, 'query').mockReturnValue(of(new HttpResponse({ body: personCollection })));
      const additionalPeople = [people];
      const expectedCollection: IPerson[] = [...additionalPeople, ...personCollection];
      vitest.spyOn(personService, 'addPersonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ city });
      comp.ngOnInit();

      expect(personService.query).toHaveBeenCalled();
      expect(personService.addPersonToCollectionIfMissing).toHaveBeenCalledWith(
        personCollection,
        ...additionalPeople.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.peopleSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Company query and add missing value', () => {
      const city: ICity = { id: 8029 };
      const companies: ICompany = { id: 29751 };
      city.companies = companies;

      const companyCollection: ICompany[] = [{ id: 29751 }];
      vitest.spyOn(companyService, 'query').mockReturnValue(of(new HttpResponse({ body: companyCollection })));
      const additionalCompanies = [companies];
      const expectedCollection: ICompany[] = [...additionalCompanies, ...companyCollection];
      vitest.spyOn(companyService, 'addCompanyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ city });
      comp.ngOnInit();

      expect(companyService.query).toHaveBeenCalled();
      expect(companyService.addCompanyToCollectionIfMissing).toHaveBeenCalledWith(
        companyCollection,
        ...additionalCompanies.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.companiesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Warehouse query and add missing value', () => {
      const city: ICity = { id: 8029 };
      const warehouses: IWarehouse = { id: 28652 };
      city.warehouses = warehouses;

      const warehouseCollection: IWarehouse[] = [{ id: 28652 }];
      vitest.spyOn(warehouseService, 'query').mockReturnValue(of(new HttpResponse({ body: warehouseCollection })));
      const additionalWarehouses = [warehouses];
      const expectedCollection: IWarehouse[] = [...additionalWarehouses, ...warehouseCollection];
      vitest.spyOn(warehouseService, 'addWarehouseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ city });
      comp.ngOnInit();

      expect(warehouseService.query).toHaveBeenCalled();
      expect(warehouseService.addWarehouseToCollectionIfMissing).toHaveBeenCalledWith(
        warehouseCollection,
        ...additionalWarehouses.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.warehousesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call State query and add missing value', () => {
      const city: ICity = { id: 8029 };
      const state: IState = { id: 31448 };
      city.state = state;

      const stateCollection: IState[] = [{ id: 31448 }];
      vitest.spyOn(stateService, 'query').mockReturnValue(of(new HttpResponse({ body: stateCollection })));
      const additionalStates = [state];
      const expectedCollection: IState[] = [...additionalStates, ...stateCollection];
      vitest.spyOn(stateService, 'addStateToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ city });
      comp.ngOnInit();

      expect(stateService.query).toHaveBeenCalled();
      expect(stateService.addStateToCollectionIfMissing).toHaveBeenCalledWith(
        stateCollection,
        ...additionalStates.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.statesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const city: ICity = { id: 8029 };
      const suppliers: ISupplier = { id: 28889 };
      city.suppliers = suppliers;
      const customers: ICustomer = { id: 26915 };
      city.customers = customers;
      const people: IPerson = { id: 8101 };
      city.people = people;
      const companies: ICompany = { id: 29751 };
      city.companies = companies;
      const warehouses: IWarehouse = { id: 28652 };
      city.warehouses = warehouses;
      const state: IState = { id: 31448 };
      city.state = state;

      activatedRoute.data = of({ city });
      comp.ngOnInit();

      expect(comp.suppliersSharedCollection()).toContainEqual(suppliers);
      expect(comp.customersSharedCollection()).toContainEqual(customers);
      expect(comp.peopleSharedCollection()).toContainEqual(people);
      expect(comp.companiesSharedCollection()).toContainEqual(companies);
      expect(comp.warehousesSharedCollection()).toContainEqual(warehouses);
      expect(comp.statesSharedCollection()).toContainEqual(state);
      expect(comp.city).toEqual(city);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICity>();
      const city = { id: 4824 };
      vitest.spyOn(cityFormService, 'getCity').mockReturnValue(city);
      vitest.spyOn(cityService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ city });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(city);
      saveSubject.complete();

      // THEN
      expect(cityFormService.getCity).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cityService.update).toHaveBeenCalledWith(expect.objectContaining(city));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICity>();
      const city = { id: 4824 };
      vitest.spyOn(cityFormService, 'getCity').mockReturnValue({ id: null });
      vitest.spyOn(cityService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ city: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(city);
      saveSubject.complete();

      // THEN
      expect(cityFormService.getCity).toHaveBeenCalled();
      expect(cityService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICity>();
      const city = { id: 4824 };
      vitest.spyOn(cityService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ city });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cityService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSupplier', () => {
      it('should forward to supplierService', () => {
        const entity = { id: 28889 };
        const entity2 = { id: 5063 };
        vitest.spyOn(supplierService, 'compareSupplier');
        comp.compareSupplier(entity, entity2);
        expect(supplierService.compareSupplier).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCustomer', () => {
      it('should forward to customerService', () => {
        const entity = { id: 26915 };
        const entity2 = { id: 21032 };
        vitest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
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

    describe('compareWarehouse', () => {
      it('should forward to warehouseService', () => {
        const entity = { id: 28652 };
        const entity2 = { id: 31486 };
        vitest.spyOn(warehouseService, 'compareWarehouse');
        comp.compareWarehouse(entity, entity2);
        expect(warehouseService.compareWarehouse).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareState', () => {
      it('should forward to stateService', () => {
        const entity = { id: 31448 };
        const entity2 = { id: 6174 };
        vitest.spyOn(stateService, 'compareState');
        comp.compareState(entity, entity2);
        expect(stateService.compareState).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
