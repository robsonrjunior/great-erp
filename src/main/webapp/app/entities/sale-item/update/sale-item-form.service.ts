import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISaleItem, NewSaleItem } from '../sale-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISaleItem for edit and NewSaleItemFormGroupInput for create.
 */
type SaleItemFormGroupInput = ISaleItem | PartialWithRequiredKeyOf<NewSaleItem>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISaleItem | NewSaleItem> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

type SaleItemFormRawValue = FormValueOf<ISaleItem>;

type NewSaleItemFormRawValue = FormValueOf<NewSaleItem>;

type SaleItemFormDefaults = Pick<NewSaleItem, 'id' | 'deletedAt'>;

type SaleItemFormGroupContent = {
  id: FormControl<SaleItemFormRawValue['id'] | NewSaleItem['id']>;
  quantity: FormControl<SaleItemFormRawValue['quantity']>;
  unitPrice: FormControl<SaleItemFormRawValue['unitPrice']>;
  discountAmount: FormControl<SaleItemFormRawValue['discountAmount']>;
  lineTotal: FormControl<SaleItemFormRawValue['lineTotal']>;
  deletedAt: FormControl<SaleItemFormRawValue['deletedAt']>;
};

export type SaleItemFormGroup = FormGroup<SaleItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SaleItemFormService {
  createSaleItemFormGroup(saleItem?: SaleItemFormGroupInput): SaleItemFormGroup {
    const saleItemRawValue = this.convertSaleItemToSaleItemRawValue({
      ...this.getFormDefaults(),
      ...(saleItem ?? { id: null }),
    });
    return new FormGroup<SaleItemFormGroupContent>({
      id: new FormControl(
        { value: saleItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantity: new FormControl(saleItemRawValue.quantity, {
        validators: [Validators.required, Validators.min(0)],
      }),
      unitPrice: new FormControl(saleItemRawValue.unitPrice, {
        validators: [Validators.required, Validators.min(0)],
      }),
      discountAmount: new FormControl(saleItemRawValue.discountAmount, {
        validators: [Validators.min(0)],
      }),
      lineTotal: new FormControl(saleItemRawValue.lineTotal, {
        validators: [Validators.required, Validators.min(0)],
      }),
      deletedAt: new FormControl(saleItemRawValue.deletedAt),
    });
  }

  getSaleItem(form: SaleItemFormGroup): ISaleItem | NewSaleItem {
    return this.convertSaleItemRawValueToSaleItem(form.getRawValue() as SaleItemFormRawValue | NewSaleItemFormRawValue);
  }

  resetForm(form: SaleItemFormGroup, saleItem: SaleItemFormGroupInput): void {
    const saleItemRawValue = this.convertSaleItemToSaleItemRawValue({ ...this.getFormDefaults(), ...saleItem });
    form.reset({
      ...saleItemRawValue,
      id: { value: saleItemRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SaleItemFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      deletedAt: currentTime,
    };
  }

  private convertSaleItemRawValueToSaleItem(rawSaleItem: SaleItemFormRawValue | NewSaleItemFormRawValue): ISaleItem | NewSaleItem {
    return {
      ...rawSaleItem,
      deletedAt: dayjs(rawSaleItem.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertSaleItemToSaleItemRawValue(
    saleItem: ISaleItem | (Partial<NewSaleItem> & SaleItemFormDefaults),
  ): SaleItemFormRawValue | PartialWithRequiredKeyOf<NewSaleItemFormRawValue> {
    return {
      ...saleItem,
      deletedAt: saleItem.deletedAt ? saleItem.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
