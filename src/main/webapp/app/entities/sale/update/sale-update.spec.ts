import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ISaleItem } from 'app/entities/sale-item/sale-item.model';
import { SaleItemService } from 'app/entities/sale-item/service/sale-item.service';
import { ISale } from '../sale.model';
import { SaleService } from '../service/sale.service';

import { SaleFormService } from './sale-form.service';
import { SaleUpdate } from './sale-update';

describe('Sale Management Update Component', () => {
  let comp: SaleUpdate;
  let fixture: ComponentFixture<SaleUpdate>;
  let activatedRoute: ActivatedRoute;
  let saleFormService: SaleFormService;
  let saleService: SaleService;
  let saleItemService: SaleItemService;

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

    fixture = TestBed.createComponent(SaleUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    saleFormService = TestBed.inject(SaleFormService);
    saleService = TestBed.inject(SaleService);
    saleItemService = TestBed.inject(SaleItemService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call SaleItem query and add missing value', () => {
      const sale: ISale = { id: 10270 };
      const items: ISaleItem = { id: 25187 };
      sale.items = items;

      const saleItemCollection: ISaleItem[] = [{ id: 25187 }];
      vitest.spyOn(saleItemService, 'query').mockReturnValue(of(new HttpResponse({ body: saleItemCollection })));
      const additionalSaleItems = [items];
      const expectedCollection: ISaleItem[] = [...additionalSaleItems, ...saleItemCollection];
      vitest.spyOn(saleItemService, 'addSaleItemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sale });
      comp.ngOnInit();

      expect(saleItemService.query).toHaveBeenCalled();
      expect(saleItemService.addSaleItemToCollectionIfMissing).toHaveBeenCalledWith(
        saleItemCollection,
        ...additionalSaleItems.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.saleItemsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const sale: ISale = { id: 10270 };
      const items: ISaleItem = { id: 25187 };
      sale.items = items;

      activatedRoute.data = of({ sale });
      comp.ngOnInit();

      expect(comp.saleItemsSharedCollection()).toContainEqual(items);
      expect(comp.sale).toEqual(sale);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISale>();
      const sale = { id: 2908 };
      vitest.spyOn(saleFormService, 'getSale').mockReturnValue(sale);
      vitest.spyOn(saleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sale });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(sale);
      saveSubject.complete();

      // THEN
      expect(saleFormService.getSale).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(saleService.update).toHaveBeenCalledWith(expect.objectContaining(sale));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISale>();
      const sale = { id: 2908 };
      vitest.spyOn(saleFormService, 'getSale').mockReturnValue({ id: null });
      vitest.spyOn(saleService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sale: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(sale);
      saveSubject.complete();

      // THEN
      expect(saleFormService.getSale).toHaveBeenCalled();
      expect(saleService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ISale>();
      const sale = { id: 2908 };
      vitest.spyOn(saleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sale });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(saleService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSaleItem', () => {
      it('should forward to saleItemService', () => {
        const entity = { id: 25187 };
        const entity2 = { id: 21071 };
        vitest.spyOn(saleItemService, 'compareSaleItem');
        comp.compareSaleItem(entity, entity2);
        expect(saleItemService.compareSaleItem).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
