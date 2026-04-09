import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { UnitOfMeasure } from 'app/entities/enumerations/unit-of-measure.model';
import { StockMovementService } from 'app/entities/stock-movement/service/stock-movement.service';
import { IStockMovement } from 'app/entities/stock-movement/stock-movement.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IRawMaterial } from '../raw-material.model';
import { RawMaterialService } from '../service/raw-material.service';

import { RawMaterialFormGroup, RawMaterialFormService } from './raw-material-form.service';

@Component({
  selector: 'jhi-raw-material-update',
  templateUrl: './raw-material-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class RawMaterialUpdate implements OnInit {
  readonly isSaving = signal(false);
  rawMaterial: IRawMaterial | null = null;
  unitOfMeasureValues = Object.keys(UnitOfMeasure);

  stockMovementsSharedCollection = signal<IStockMovement[]>([]);

  protected rawMaterialService = inject(RawMaterialService);
  protected rawMaterialFormService = inject(RawMaterialFormService);
  protected stockMovementService = inject(StockMovementService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RawMaterialFormGroup = this.rawMaterialFormService.createRawMaterialFormGroup();

  compareStockMovement = (o1: IStockMovement | null, o2: IStockMovement | null): boolean =>
    this.stockMovementService.compareStockMovement(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rawMaterial }) => {
      this.rawMaterial = rawMaterial;
      if (rawMaterial) {
        this.updateForm(rawMaterial);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const rawMaterial = this.rawMaterialFormService.getRawMaterial(this.editForm);
    if (rawMaterial.id === null) {
      this.subscribeToSaveResponse(this.rawMaterialService.create(rawMaterial));
    } else {
      this.subscribeToSaveResponse(this.rawMaterialService.update(rawMaterial));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IRawMaterial | null>): void {
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

  protected updateForm(rawMaterial: IRawMaterial): void {
    this.rawMaterial = rawMaterial;
    this.rawMaterialFormService.resetForm(this.editForm, rawMaterial);

    this.stockMovementsSharedCollection.update(stockMovements =>
      this.stockMovementService.addStockMovementToCollectionIfMissing<IStockMovement>(stockMovements, rawMaterial.stockMovements),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.stockMovementService
      .query()
      .pipe(map((res: HttpResponse<IStockMovement[]>) => res.body ?? []))
      .pipe(
        map((stockMovements: IStockMovement[]) =>
          this.stockMovementService.addStockMovementToCollectionIfMissing<IStockMovement>(stockMovements, this.rawMaterial?.stockMovements),
        ),
      )
      .subscribe((stockMovements: IStockMovement[]) => this.stockMovementsSharedCollection.set(stockMovements));
  }
}
