import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { UnitOfMeasure } from 'app/entities/enumerations/unit-of-measure.model';
import { ISaleItem } from 'app/entities/sale-item/sale-item.model';
import { SaleItemService } from 'app/entities/sale-item/service/sale-item.service';
import { StockMovementService } from 'app/entities/stock-movement/service/stock-movement.service';
import { IStockMovement } from 'app/entities/stock-movement/stock-movement.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IProduct } from '../product.model';
import { ProductService } from '../service/product.service';

import { ProductFormGroup, ProductFormService } from './product-form.service';

@Component({
  selector: 'jhi-product-update',
  templateUrl: './product-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProductUpdate implements OnInit {
  readonly isSaving = signal(false);
  product: IProduct | null = null;
  unitOfMeasureValues = Object.keys(UnitOfMeasure);

  saleItemsSharedCollection = signal<ISaleItem[]>([]);
  stockMovementsSharedCollection = signal<IStockMovement[]>([]);

  protected productService = inject(ProductService);
  protected productFormService = inject(ProductFormService);
  protected saleItemService = inject(SaleItemService);
  protected stockMovementService = inject(StockMovementService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProductFormGroup = this.productFormService.createProductFormGroup();

  compareSaleItem = (o1: ISaleItem | null, o2: ISaleItem | null): boolean => this.saleItemService.compareSaleItem(o1, o2);

  compareStockMovement = (o1: IStockMovement | null, o2: IStockMovement | null): boolean =>
    this.stockMovementService.compareStockMovement(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ product }) => {
      this.product = product;
      if (product) {
        this.updateForm(product);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const product = this.productFormService.getProduct(this.editForm);
    if (product.id === null) {
      this.subscribeToSaveResponse(this.productService.create(product));
    } else {
      this.subscribeToSaveResponse(this.productService.update(product));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProduct | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(product: IProduct): void {
    this.product = product;
    this.productFormService.resetForm(this.editForm, product);

    this.saleItemsSharedCollection.update(saleItems =>
      this.saleItemService.addSaleItemToCollectionIfMissing<ISaleItem>(saleItems, product.saleItems),
    );
    this.stockMovementsSharedCollection.update(stockMovements =>
      this.stockMovementService.addStockMovementToCollectionIfMissing<IStockMovement>(stockMovements, product.stockMovements),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.saleItemService
      .query()
      .pipe(map((res: HttpResponse<ISaleItem[]>) => res.body ?? []))
      .pipe(
        map((saleItems: ISaleItem[]) =>
          this.saleItemService.addSaleItemToCollectionIfMissing<ISaleItem>(saleItems, this.product?.saleItems),
        ),
      )
      .subscribe((saleItems: ISaleItem[]) => this.saleItemsSharedCollection.set(saleItems));

    this.stockMovementService
      .query()
      .pipe(map((res: HttpResponse<IStockMovement[]>) => res.body ?? []))
      .pipe(
        map((stockMovements: IStockMovement[]) =>
          this.stockMovementService.addStockMovementToCollectionIfMissing<IStockMovement>(stockMovements, this.product?.stockMovements),
        ),
      )
      .subscribe((stockMovements: IStockMovement[]) => this.stockMovementsSharedCollection.set(stockMovements));
  }
}
