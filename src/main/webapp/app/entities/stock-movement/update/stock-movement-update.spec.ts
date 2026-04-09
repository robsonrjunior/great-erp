import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { StockMovementService } from '../service/stock-movement.service';
import { IStockMovement } from '../stock-movement.model';

import { StockMovementFormService } from './stock-movement-form.service';
import { StockMovementUpdate } from './stock-movement-update';

describe('StockMovement Management Update Component', () => {
  let comp: StockMovementUpdate;
  let fixture: ComponentFixture<StockMovementUpdate>;
  let activatedRoute: ActivatedRoute;
  let stockMovementFormService: StockMovementFormService;
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

    fixture = TestBed.createComponent(StockMovementUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    stockMovementFormService = TestBed.inject(StockMovementFormService);
    stockMovementService = TestBed.inject(StockMovementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const stockMovement: IStockMovement = { id: 1833 };

      activatedRoute.data = of({ stockMovement });
      comp.ngOnInit();

      expect(comp.stockMovement).toEqual(stockMovement);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IStockMovement>();
      const stockMovement = { id: 18917 };
      vitest.spyOn(stockMovementFormService, 'getStockMovement').mockReturnValue(stockMovement);
      vitest.spyOn(stockMovementService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockMovement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(stockMovement);
      saveSubject.complete();

      // THEN
      expect(stockMovementFormService.getStockMovement).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(stockMovementService.update).toHaveBeenCalledWith(expect.objectContaining(stockMovement));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IStockMovement>();
      const stockMovement = { id: 18917 };
      vitest.spyOn(stockMovementFormService, 'getStockMovement').mockReturnValue({ id: null });
      vitest.spyOn(stockMovementService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockMovement: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(stockMovement);
      saveSubject.complete();

      // THEN
      expect(stockMovementFormService.getStockMovement).toHaveBeenCalled();
      expect(stockMovementService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IStockMovement>();
      const stockMovement = { id: 18917 };
      vitest.spyOn(stockMovementService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockMovement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(stockMovementService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
