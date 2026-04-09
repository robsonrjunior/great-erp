import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ISaleItem } from '../sale-item.model';
import { SaleItemService } from '../service/sale-item.service';

import { SaleItemFormGroup, SaleItemFormService } from './sale-item-form.service';

@Component({
  selector: 'jhi-sale-item-update',
  templateUrl: './sale-item-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SaleItemUpdate implements OnInit {
  readonly isSaving = signal(false);
  saleItem: ISaleItem | null = null;

  protected saleItemService = inject(SaleItemService);
  protected saleItemFormService = inject(SaleItemFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SaleItemFormGroup = this.saleItemFormService.createSaleItemFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ saleItem }) => {
      this.saleItem = saleItem;
      if (saleItem) {
        this.updateForm(saleItem);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const saleItem = this.saleItemFormService.getSaleItem(this.editForm);
    if (saleItem.id === null) {
      this.subscribeToSaveResponse(this.saleItemService.create(saleItem));
    } else {
      this.subscribeToSaveResponse(this.saleItemService.update(saleItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISaleItem | null>): void {
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

  protected updateForm(saleItem: ISaleItem): void {
    this.saleItem = saleItem;
    this.saleItemFormService.resetForm(this.editForm, saleItem);
  }
}
