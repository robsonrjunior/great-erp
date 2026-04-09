import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { StockMovementService } from 'app/entities/stock-movement/service/stock-movement.service';
import { IStockMovement } from 'app/entities/stock-movement/stock-movement.model';
import { WarehouseService } from '../service/warehouse.service';
import { IWarehouse } from '../warehouse.model';

import { WarehouseFormService } from './warehouse-form.service';
import { WarehouseUpdate } from './warehouse-update';

describe('Warehouse Management Update Component', () => {
  let comp: WarehouseUpdate;
  let fixture: ComponentFixture<WarehouseUpdate>;
  let activatedRoute: ActivatedRoute;
  let warehouseFormService: WarehouseFormService;
  let warehouseService: WarehouseService;
  let stockMovementService: StockMovementService;
  let saleService: SaleService;

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

    fixture = TestBed.createComponent(WarehouseUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    warehouseFormService = TestBed.inject(WarehouseFormService);
    warehouseService = TestBed.inject(WarehouseService);
    stockMovementService = TestBed.inject(StockMovementService);
    saleService = TestBed.inject(SaleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call StockMovement query and add missing value', () => {
      const warehouse: IWarehouse = { id: 31486 };
      const stockMovements: IStockMovement = { id: 18917 };
      warehouse.stockMovements = stockMovements;

      const stockMovementCollection: IStockMovement[] = [{ id: 18917 }];
      vitest.spyOn(stockMovementService, 'query').mockReturnValue(of(new HttpResponse({ body: stockMovementCollection })));
      const additionalStockMovements = [stockMovements];
      const expectedCollection: IStockMovement[] = [...additionalStockMovements, ...stockMovementCollection];
      vitest.spyOn(stockMovementService, 'addStockMovementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ warehouse });
      comp.ngOnInit();

      expect(stockMovementService.query).toHaveBeenCalled();
      expect(stockMovementService.addStockMovementToCollectionIfMissing).toHaveBeenCalledWith(
        stockMovementCollection,
        ...additionalStockMovements.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.stockMovementsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Sale query and add missing value', () => {
      const warehouse: IWarehouse = { id: 31486 };
      const sales: ISale = { id: 2908 };
      warehouse.sales = sales;

      const saleCollection: ISale[] = [{ id: 2908 }];
      vitest.spyOn(saleService, 'query').mockReturnValue(of(new HttpResponse({ body: saleCollection })));
      const additionalSales = [sales];
      const expectedCollection: ISale[] = [...additionalSales, ...saleCollection];
      vitest.spyOn(saleService, 'addSaleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ warehouse });
      comp.ngOnInit();

      expect(saleService.query).toHaveBeenCalled();
      expect(saleService.addSaleToCollectionIfMissing).toHaveBeenCalledWith(
        saleCollection,
        ...additionalSales.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.salesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const warehouse: IWarehouse = { id: 31486 };
      const stockMovements: IStockMovement = { id: 18917 };
      warehouse.stockMovements = stockMovements;
      const sales: ISale = { id: 2908 };
      warehouse.sales = sales;

      activatedRoute.data = of({ warehouse });
      comp.ngOnInit();

      expect(comp.stockMovementsSharedCollection()).toContainEqual(stockMovements);
      expect(comp.salesSharedCollection()).toContainEqual(sales);
      expect(comp.warehouse).toEqual(warehouse);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IWarehouse>();
      const warehouse = { id: 28652 };
      vitest.spyOn(warehouseFormService, 'getWarehouse').mockReturnValue(warehouse);
      vitest.spyOn(warehouseService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ warehouse });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(warehouse);
      saveSubject.complete();

      // THEN
      expect(warehouseFormService.getWarehouse).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(warehouseService.update).toHaveBeenCalledWith(expect.objectContaining(warehouse));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IWarehouse>();
      const warehouse = { id: 28652 };
      vitest.spyOn(warehouseFormService, 'getWarehouse').mockReturnValue({ id: null });
      vitest.spyOn(warehouseService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ warehouse: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(warehouse);
      saveSubject.complete();

      // THEN
      expect(warehouseFormService.getWarehouse).toHaveBeenCalled();
      expect(warehouseService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IWarehouse>();
      const warehouse = { id: 28652 };
      vitest.spyOn(warehouseService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ warehouse });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(warehouseService.update).toHaveBeenCalled();
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

    describe('compareSale', () => {
      it('should forward to saleService', () => {
        const entity = { id: 2908 };
        const entity2 = { id: 10270 };
        vitest.spyOn(saleService, 'compareSale');
        comp.compareSale(entity, entity2);
        expect(saleService.compareSale).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
