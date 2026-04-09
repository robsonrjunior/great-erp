import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ISaleItem } from '../sale-item.model';
import { SaleItemService } from '../service/sale-item.service';

import { SaleItemFormService } from './sale-item-form.service';
import { SaleItemUpdate } from './sale-item-update';

describe('SaleItem Management Update Component', () => {
  let comp: SaleItemUpdate;
  let fixture: ComponentFixture<SaleItemUpdate>;
  let activatedRoute: ActivatedRoute;
  let saleItemFormService: SaleItemFormService;
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

    fixture = TestBed.createComponent(SaleItemUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    saleItemFormService = TestBed.inject(SaleItemFormService);
    saleItemService = TestBed.inject(SaleItemService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const saleItem: ISaleItem = { id: 21071 };

      activatedRoute.data = of({ saleItem });
      comp.ngOnInit();

      expect(comp.saleItem).toEqual(saleItem);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISaleItem>();
      const saleItem = { id: 25187 };
      vitest.spyOn(saleItemFormService, 'getSaleItem').mockReturnValue(saleItem);
      vitest.spyOn(saleItemService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ saleItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(saleItem);
      saveSubject.complete();

      // THEN
      expect(saleItemFormService.getSaleItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(saleItemService.update).toHaveBeenCalledWith(expect.objectContaining(saleItem));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISaleItem>();
      const saleItem = { id: 25187 };
      vitest.spyOn(saleItemFormService, 'getSaleItem').mockReturnValue({ id: null });
      vitest.spyOn(saleItemService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ saleItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(saleItem);
      saveSubject.complete();

      // THEN
      expect(saleItemFormService.getSaleItem).toHaveBeenCalled();
      expect(saleItemService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ISaleItem>();
      const saleItem = { id: 25187 };
      vitest.spyOn(saleItemService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ saleItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(saleItemService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
