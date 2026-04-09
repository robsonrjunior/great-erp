import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';
import { IRawMaterial } from 'app/entities/raw-material/raw-material.model';
import { RawMaterialService } from 'app/entities/raw-material/service/raw-material.service';
import { SupplierService } from '../service/supplier.service';
import { ISupplier } from '../supplier.model';

import { SupplierFormService } from './supplier-form.service';
import { SupplierUpdate } from './supplier-update';

describe('Supplier Management Update Component', () => {
  let comp: SupplierUpdate;
  let fixture: ComponentFixture<SupplierUpdate>;
  let activatedRoute: ActivatedRoute;
  let supplierFormService: SupplierFormService;
  let supplierService: SupplierService;
  let personService: PersonService;
  let companyService: CompanyService;
  let rawMaterialService: RawMaterialService;

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

    fixture = TestBed.createComponent(SupplierUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    supplierFormService = TestBed.inject(SupplierFormService);
    supplierService = TestBed.inject(SupplierService);
    personService = TestBed.inject(PersonService);
    companyService = TestBed.inject(CompanyService);
    rawMaterialService = TestBed.inject(RawMaterialService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call person query and add missing value', () => {
      const supplier: ISupplier = { id: 5063 };
      const person: IPerson = { id: 8101 };
      supplier.person = person;

      const personCollection: IPerson[] = [{ id: 8101 }];
      vitest.spyOn(personService, 'query').mockReturnValue(of(new HttpResponse({ body: personCollection })));
      const expectedCollection: IPerson[] = [person, ...personCollection];
      vitest.spyOn(personService, 'addPersonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ supplier });
      comp.ngOnInit();

      expect(personService.query).toHaveBeenCalled();
      expect(personService.addPersonToCollectionIfMissing).toHaveBeenCalledWith(personCollection, person);
      expect(comp.peopleCollection()).toEqual(expectedCollection);
    });

    it('should call company query and add missing value', () => {
      const supplier: ISupplier = { id: 5063 };
      const company: ICompany = { id: 29751 };
      supplier.company = company;

      const companyCollection: ICompany[] = [{ id: 29751 }];
      vitest.spyOn(companyService, 'query').mockReturnValue(of(new HttpResponse({ body: companyCollection })));
      const expectedCollection: ICompany[] = [company, ...companyCollection];
      vitest.spyOn(companyService, 'addCompanyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ supplier });
      comp.ngOnInit();

      expect(companyService.query).toHaveBeenCalled();
      expect(companyService.addCompanyToCollectionIfMissing).toHaveBeenCalledWith(companyCollection, company);
      expect(comp.companiesCollection()).toEqual(expectedCollection);
    });

    it('should call RawMaterial query and add missing value', () => {
      const supplier: ISupplier = { id: 5063 };
      const rawMaterials: IRawMaterial = { id: 6822 };
      supplier.rawMaterials = rawMaterials;

      const rawMaterialCollection: IRawMaterial[] = [{ id: 6822 }];
      vitest.spyOn(rawMaterialService, 'query').mockReturnValue(of(new HttpResponse({ body: rawMaterialCollection })));
      const additionalRawMaterials = [rawMaterials];
      const expectedCollection: IRawMaterial[] = [...additionalRawMaterials, ...rawMaterialCollection];
      vitest.spyOn(rawMaterialService, 'addRawMaterialToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ supplier });
      comp.ngOnInit();

      expect(rawMaterialService.query).toHaveBeenCalled();
      expect(rawMaterialService.addRawMaterialToCollectionIfMissing).toHaveBeenCalledWith(
        rawMaterialCollection,
        ...additionalRawMaterials.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.rawMaterialsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const supplier: ISupplier = { id: 5063 };
      const person: IPerson = { id: 8101 };
      supplier.person = person;
      const company: ICompany = { id: 29751 };
      supplier.company = company;
      const rawMaterials: IRawMaterial = { id: 6822 };
      supplier.rawMaterials = rawMaterials;

      activatedRoute.data = of({ supplier });
      comp.ngOnInit();

      expect(comp.peopleCollection()).toContainEqual(person);
      expect(comp.companiesCollection()).toContainEqual(company);
      expect(comp.rawMaterialsSharedCollection()).toContainEqual(rawMaterials);
      expect(comp.supplier).toEqual(supplier);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISupplier>();
      const supplier = { id: 28889 };
      vitest.spyOn(supplierFormService, 'getSupplier').mockReturnValue(supplier);
      vitest.spyOn(supplierService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ supplier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(supplier);
      saveSubject.complete();

      // THEN
      expect(supplierFormService.getSupplier).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(supplierService.update).toHaveBeenCalledWith(expect.objectContaining(supplier));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISupplier>();
      const supplier = { id: 28889 };
      vitest.spyOn(supplierFormService, 'getSupplier').mockReturnValue({ id: null });
      vitest.spyOn(supplierService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ supplier: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(supplier);
      saveSubject.complete();

      // THEN
      expect(supplierFormService.getSupplier).toHaveBeenCalled();
      expect(supplierService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ISupplier>();
      const supplier = { id: 28889 };
      vitest.spyOn(supplierService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ supplier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(supplierService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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

    describe('compareRawMaterial', () => {
      it('should forward to rawMaterialService', () => {
        const entity = { id: 6822 };
        const entity2 = { id: 19276 };
        vitest.spyOn(rawMaterialService, 'compareRawMaterial');
        comp.compareRawMaterial(entity, entity2);
        expect(rawMaterialService.compareRawMaterial).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
