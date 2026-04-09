import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ISaleItem } from 'app/entities/sale-item/sale-item.model';
import { SaleItemService } from 'app/entities/sale-item/service/sale-item.service';
import { StockMovementService } from 'app/entities/stock-movement/service/stock-movement.service';
import { IStockMovement } from 'app/entities/stock-movement/stock-movement.model';
import { IProduct } from '../product.model';
import { ProductService } from '../service/product.service';

import { ProductFormService } from './product-form.service';
import { ProductUpdate } from './product-update';

describe('Product Management Update Component', () => {
  let comp: ProductUpdate;
  let fixture: ComponentFixture<ProductUpdate>;
  let activatedRoute: ActivatedRoute;
  let productFormService: ProductFormService;
  let productService: ProductService;
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

    fixture = TestBed.createComponent(ProductUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productFormService = TestBed.inject(ProductFormService);
    productService = TestBed.inject(ProductService);
    saleItemService = TestBed.inject(SaleItemService);
    stockMovementService = TestBed.inject(StockMovementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call SaleItem query and add missing value', () => {
      const product: IProduct = { id: 11926 };
      const saleItems: ISaleItem = { id: 25187 };
      product.saleItems = saleItems;

      const saleItemCollection: ISaleItem[] = [{ id: 25187 }];
      vitest.spyOn(saleItemService, 'query').mockReturnValue(of(new HttpResponse({ body: saleItemCollection })));
      const additionalSaleItems = [saleItems];
      const expectedCollection: ISaleItem[] = [...additionalSaleItems, ...saleItemCollection];
      vitest.spyOn(saleItemService, 'addSaleItemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ product });
      comp.ngOnInit();

      expect(saleItemService.query).toHaveBeenCalled();
      expect(saleItemService.addSaleItemToCollectionIfMissing).toHaveBeenCalledWith(
        saleItemCollection,
        ...additionalSaleItems.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.saleItemsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call StockMovement query and add missing value', () => {
      const product: IProduct = { id: 11926 };
      const stockMovements: IStockMovement = { id: 18917 };
      product.stockMovements = stockMovements;

      const stockMovementCollection: IStockMovement[] = [{ id: 18917 }];
      vitest.spyOn(stockMovementService, 'query').mockReturnValue(of(new HttpResponse({ body: stockMovementCollection })));
      const additionalStockMovements = [stockMovements];
      const expectedCollection: IStockMovement[] = [...additionalStockMovements, ...stockMovementCollection];
      vitest.spyOn(stockMovementService, 'addStockMovementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ product });
      comp.ngOnInit();

      expect(stockMovementService.query).toHaveBeenCalled();
      expect(stockMovementService.addStockMovementToCollectionIfMissing).toHaveBeenCalledWith(
        stockMovementCollection,
        ...additionalStockMovements.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.stockMovementsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const product: IProduct = { id: 11926 };
      const saleItems: ISaleItem = { id: 25187 };
      product.saleItems = saleItems;
      const stockMovements: IStockMovement = { id: 18917 };
      product.stockMovements = stockMovements;

      activatedRoute.data = of({ product });
      comp.ngOnInit();

      expect(comp.saleItemsSharedCollection()).toContainEqual(saleItems);
      expect(comp.stockMovementsSharedCollection()).toContainEqual(stockMovements);
      expect(comp.product).toEqual(product);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProduct>();
      const product = { id: 21536 };
      vitest.spyOn(productFormService, 'getProduct').mockReturnValue(product);
      vitest.spyOn(productService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ product });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(product);
      saveSubject.complete();

      // THEN
      expect(productFormService.getProduct).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productService.update).toHaveBeenCalledWith(expect.objectContaining(product));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProduct>();
      const product = { id: 21536 };
      vitest.spyOn(productFormService, 'getProduct').mockReturnValue({ id: null });
      vitest.spyOn(productService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ product: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(product);
      saveSubject.complete();

      // THEN
      expect(productFormService.getProduct).toHaveBeenCalled();
      expect(productService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IProduct>();
      const product = { id: 21536 };
      vitest.spyOn(productService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ product });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productService.update).toHaveBeenCalled();
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
