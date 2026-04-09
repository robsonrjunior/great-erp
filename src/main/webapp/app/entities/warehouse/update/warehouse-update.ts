import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { StockMovementService } from 'app/entities/stock-movement/service/stock-movement.service';
import { IStockMovement } from 'app/entities/stock-movement/stock-movement.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { WarehouseService } from '../service/warehouse.service';
import { IWarehouse } from '../warehouse.model';

import { WarehouseFormGroup, WarehouseFormService } from './warehouse-form.service';

@Component({
  selector: 'jhi-warehouse-update',
  templateUrl: './warehouse-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class WarehouseUpdate implements OnInit {
  readonly isSaving = signal(false);
  warehouse: IWarehouse | null = null;

  stockMovementsSharedCollection = signal<IStockMovement[]>([]);
  salesSharedCollection = signal<ISale[]>([]);

  protected warehouseService = inject(WarehouseService);
  protected warehouseFormService = inject(WarehouseFormService);
  protected stockMovementService = inject(StockMovementService);
  protected saleService = inject(SaleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: WarehouseFormGroup = this.warehouseFormService.createWarehouseFormGroup();

  compareStockMovement = (o1: IStockMovement | null, o2: IStockMovement | null): boolean =>
    this.stockMovementService.compareStockMovement(o1, o2);

  compareSale = (o1: ISale | null, o2: ISale | null): boolean => this.saleService.compareSale(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ warehouse }) => {
      this.warehouse = warehouse;
      if (warehouse) {
        this.updateForm(warehouse);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const warehouse = this.warehouseFormService.getWarehouse(this.editForm);
    if (warehouse.id === null) {
      this.subscribeToSaveResponse(this.warehouseService.create(warehouse));
    } else {
      this.subscribeToSaveResponse(this.warehouseService.update(warehouse));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IWarehouse | null>): void {
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

  protected updateForm(warehouse: IWarehouse): void {
    this.warehouse = warehouse;
    this.warehouseFormService.resetForm(this.editForm, warehouse);

    this.stockMovementsSharedCollection.update(stockMovements =>
      this.stockMovementService.addStockMovementToCollectionIfMissing<IStockMovement>(stockMovements, warehouse.stockMovements),
    );
    this.salesSharedCollection.update(sales => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, warehouse.sales));
  }

  protected loadRelationshipsOptions(): void {
    this.stockMovementService
      .query()
      .pipe(map((res: HttpResponse<IStockMovement[]>) => res.body ?? []))
      .pipe(
        map((stockMovements: IStockMovement[]) =>
          this.stockMovementService.addStockMovementToCollectionIfMissing<IStockMovement>(stockMovements, this.warehouse?.stockMovements),
        ),
      )
      .subscribe((stockMovements: IStockMovement[]) => this.stockMovementsSharedCollection.set(stockMovements));

    this.saleService
      .query()
      .pipe(map((res: HttpResponse<ISale[]>) => res.body ?? []))
      .pipe(map((sales: ISale[]) => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, this.warehouse?.sales)))
      .subscribe((sales: ISale[]) => this.salesSharedCollection.set(sales));
  }
}
