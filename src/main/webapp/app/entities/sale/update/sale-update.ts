import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { SaleStatus } from 'app/entities/enumerations/sale-status.model';
import { ISaleItem } from 'app/entities/sale-item/sale-item.model';
import { SaleItemService } from 'app/entities/sale-item/service/sale-item.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ISale } from '../sale.model';
import { SaleService } from '../service/sale.service';

import { SaleFormGroup, SaleFormService } from './sale-form.service';

@Component({
  selector: 'jhi-sale-update',
  templateUrl: './sale-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SaleUpdate implements OnInit {
  readonly isSaving = signal(false);
  sale: ISale | null = null;
  saleStatusValues = Object.keys(SaleStatus);

  saleItemsSharedCollection = signal<ISaleItem[]>([]);

  protected saleService = inject(SaleService);
  protected saleFormService = inject(SaleFormService);
  protected saleItemService = inject(SaleItemService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SaleFormGroup = this.saleFormService.createSaleFormGroup();

  compareSaleItem = (o1: ISaleItem | null, o2: ISaleItem | null): boolean => this.saleItemService.compareSaleItem(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sale }) => {
      this.sale = sale;
      if (sale) {
        this.updateForm(sale);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const sale = this.saleFormService.getSale(this.editForm);
    if (sale.id === null) {
      this.subscribeToSaveResponse(this.saleService.create(sale));
    } else {
      this.subscribeToSaveResponse(this.saleService.update(sale));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISale | null>): void {
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

  protected updateForm(sale: ISale): void {
    this.sale = sale;
    this.saleFormService.resetForm(this.editForm, sale);

    this.saleItemsSharedCollection.update(saleItems =>
      this.saleItemService.addSaleItemToCollectionIfMissing<ISaleItem>(saleItems, sale.items),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.saleItemService
      .query()
      .pipe(map((res: HttpResponse<ISaleItem[]>) => res.body ?? []))
      .pipe(map((saleItems: ISaleItem[]) => this.saleItemService.addSaleItemToCollectionIfMissing<ISaleItem>(saleItems, this.sale?.items)))
      .subscribe((saleItems: ISaleItem[]) => this.saleItemsSharedCollection.set(saleItems));
  }
}
