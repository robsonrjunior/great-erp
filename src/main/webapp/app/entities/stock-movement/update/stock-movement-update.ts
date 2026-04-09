import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { MovementType } from 'app/entities/enumerations/movement-type.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { StockMovementService } from '../service/stock-movement.service';
import { IStockMovement } from '../stock-movement.model';

import { StockMovementFormGroup, StockMovementFormService } from './stock-movement-form.service';

@Component({
  selector: 'jhi-stock-movement-update',
  templateUrl: './stock-movement-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class StockMovementUpdate implements OnInit {
  readonly isSaving = signal(false);
  stockMovement: IStockMovement | null = null;
  movementTypeValues = Object.keys(MovementType);

  protected stockMovementService = inject(StockMovementService);
  protected stockMovementFormService = inject(StockMovementFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StockMovementFormGroup = this.stockMovementFormService.createStockMovementFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stockMovement }) => {
      this.stockMovement = stockMovement;
      if (stockMovement) {
        this.updateForm(stockMovement);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const stockMovement = this.stockMovementFormService.getStockMovement(this.editForm);
    if (stockMovement.id === null) {
      this.subscribeToSaveResponse(this.stockMovementService.create(stockMovement));
    } else {
      this.subscribeToSaveResponse(this.stockMovementService.update(stockMovement));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IStockMovement | null>): void {
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

  protected updateForm(stockMovement: IStockMovement): void {
    this.stockMovement = stockMovement;
    this.stockMovementFormService.resetForm(this.editForm, stockMovement);
  }
}
