import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { StockMovementService } from 'app/entities/stock-movement/service/stock-movement.service';
import { IStockMovement } from 'app/entities/stock-movement/stock-movement.model';
import { IRawMaterial } from '../raw-material.model';
import { RawMaterialService } from '../service/raw-material.service';

import { RawMaterialFormService } from './raw-material-form.service';
import { RawMaterialUpdate } from './raw-material-update';

describe('RawMaterial Management Update Component', () => {
  let comp: RawMaterialUpdate;
  let fixture: ComponentFixture<RawMaterialUpdate>;
  let activatedRoute: ActivatedRoute;
  let rawMaterialFormService: RawMaterialFormService;
  let rawMaterialService: RawMaterialService;
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

    fixture = TestBed.createComponent(RawMaterialUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    rawMaterialFormService = TestBed.inject(RawMaterialFormService);
    rawMaterialService = TestBed.inject(RawMaterialService);
    stockMovementService = TestBed.inject(StockMovementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call StockMovement query and add missing value', () => {
      const rawMaterial: IRawMaterial = { id: 19276 };
      const stockMovements: IStockMovement = { id: 18917 };
      rawMaterial.stockMovements = stockMovements;

      const stockMovementCollection: IStockMovement[] = [{ id: 18917 }];
      vitest.spyOn(stockMovementService, 'query').mockReturnValue(of(new HttpResponse({ body: stockMovementCollection })));
      const additionalStockMovements = [stockMovements];
      const expectedCollection: IStockMovement[] = [...additionalStockMovements, ...stockMovementCollection];
      vitest.spyOn(stockMovementService, 'addStockMovementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rawMaterial });
      comp.ngOnInit();

      expect(stockMovementService.query).toHaveBeenCalled();
      expect(stockMovementService.addStockMovementToCollectionIfMissing).toHaveBeenCalledWith(
        stockMovementCollection,
        ...additionalStockMovements.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.stockMovementsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const rawMaterial: IRawMaterial = { id: 19276 };
      const stockMovements: IStockMovement = { id: 18917 };
      rawMaterial.stockMovements = stockMovements;

      activatedRoute.data = of({ rawMaterial });
      comp.ngOnInit();

      expect(comp.stockMovementsSharedCollection()).toContainEqual(stockMovements);
      expect(comp.rawMaterial).toEqual(rawMaterial);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRawMaterial>();
      const rawMaterial = { id: 6822 };
      vitest.spyOn(rawMaterialFormService, 'getRawMaterial').mockReturnValue(rawMaterial);
      vitest.spyOn(rawMaterialService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rawMaterial });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(rawMaterial);
      saveSubject.complete();

      // THEN
      expect(rawMaterialFormService.getRawMaterial).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(rawMaterialService.update).toHaveBeenCalledWith(expect.objectContaining(rawMaterial));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRawMaterial>();
      const rawMaterial = { id: 6822 };
      vitest.spyOn(rawMaterialFormService, 'getRawMaterial').mockReturnValue({ id: null });
      vitest.spyOn(rawMaterialService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rawMaterial: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(rawMaterial);
      saveSubject.complete();

      // THEN
      expect(rawMaterialFormService.getRawMaterial).toHaveBeenCalled();
      expect(rawMaterialService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IRawMaterial>();
      const rawMaterial = { id: 6822 };
      vitest.spyOn(rawMaterialService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rawMaterial });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(rawMaterialService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
