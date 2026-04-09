import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISale, NewSale } from '../sale.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISale for edit and NewSaleFormGroupInput for create.
 */
type SaleFormGroupInput = ISale | PartialWithRequiredKeyOf<NewSale>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISale | NewSale> = Omit<T, 'saleDate' | 'deletedAt'> & {
  saleDate?: string | null;
  deletedAt?: string | null;
};

type SaleFormRawValue = FormValueOf<ISale>;

type NewSaleFormRawValue = FormValueOf<NewSale>;

type SaleFormDefaults = Pick<NewSale, 'id' | 'saleDate' | 'deletedAt'>;

type SaleFormGroupContent = {
  id: FormControl<SaleFormRawValue['id'] | NewSale['id']>;
  saleDate: FormControl<SaleFormRawValue['saleDate']>;
  saleNumber: FormControl<SaleFormRawValue['saleNumber']>;
  status: FormControl<SaleFormRawValue['status']>;
  grossAmount: FormControl<SaleFormRawValue['grossAmount']>;
  discountAmount: FormControl<SaleFormRawValue['discountAmount']>;
  netAmount: FormControl<SaleFormRawValue['netAmount']>;
  notes: FormControl<SaleFormRawValue['notes']>;
  deletedAt: FormControl<SaleFormRawValue['deletedAt']>;
  items: FormControl<SaleFormRawValue['items']>;
};

export type SaleFormGroup = FormGroup<SaleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SaleFormService {
  createSaleFormGroup(sale?: SaleFormGroupInput): SaleFormGroup {
    const saleRawValue = this.convertSaleToSaleRawValue({
      ...this.getFormDefaults(),
      ...(sale ?? { id: null }),
    });
    return new FormGroup<SaleFormGroupContent>({
      id: new FormControl(
        { value: saleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      saleDate: new FormControl(saleRawValue.saleDate, {
        validators: [Validators.required],
      }),
      saleNumber: new FormControl(saleRawValue.saleNumber, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(40)],
      }),
      status: new FormControl(saleRawValue.status, {
        validators: [Validators.required],
      }),
      grossAmount: new FormControl(saleRawValue.grossAmount, {
        validators: [Validators.required, Validators.min(0)],
      }),
      discountAmount: new FormControl(saleRawValue.discountAmount, {
        validators: [Validators.min(0)],
      }),
      netAmount: new FormControl(saleRawValue.netAmount, {
        validators: [Validators.required, Validators.min(0)],
      }),
      notes: new FormControl(saleRawValue.notes, {
        validators: [Validators.maxLength(500)],
      }),
      deletedAt: new FormControl(saleRawValue.deletedAt),
      items: new FormControl(saleRawValue.items),
    });
  }

  getSale(form: SaleFormGroup): ISale | NewSale {
    return this.convertSaleRawValueToSale(form.getRawValue() as SaleFormRawValue | NewSaleFormRawValue);
  }

  resetForm(form: SaleFormGroup, sale: SaleFormGroupInput): void {
    const saleRawValue = this.convertSaleToSaleRawValue({ ...this.getFormDefaults(), ...sale });
    form.reset({
      ...saleRawValue,
      id: { value: saleRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SaleFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      saleDate: currentTime,
      deletedAt: currentTime,
    };
  }

  private convertSaleRawValueToSale(rawSale: SaleFormRawValue | NewSaleFormRawValue): ISale | NewSale {
    return {
      ...rawSale,
      saleDate: dayjs(rawSale.saleDate, DATE_TIME_FORMAT),
      deletedAt: dayjs(rawSale.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertSaleToSaleRawValue(
    sale: ISale | (Partial<NewSale> & SaleFormDefaults),
  ): SaleFormRawValue | PartialWithRequiredKeyOf<NewSaleFormRawValue> {
    return {
      ...sale,
      saleDate: sale.saleDate ? sale.saleDate.format(DATE_TIME_FORMAT) : undefined,
      deletedAt: sale.deletedAt ? sale.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
