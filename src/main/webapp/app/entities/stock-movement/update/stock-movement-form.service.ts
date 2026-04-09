import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IStockMovement, NewStockMovement } from '../stock-movement.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStockMovement for edit and NewStockMovementFormGroupInput for create.
 */
type StockMovementFormGroupInput = IStockMovement | PartialWithRequiredKeyOf<NewStockMovement>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IStockMovement | NewStockMovement> = Omit<T, 'movementDate' | 'deletedAt'> & {
  movementDate?: string | null;
  deletedAt?: string | null;
};

type StockMovementFormRawValue = FormValueOf<IStockMovement>;

type NewStockMovementFormRawValue = FormValueOf<NewStockMovement>;

type StockMovementFormDefaults = Pick<NewStockMovement, 'id' | 'movementDate' | 'deletedAt'>;

type StockMovementFormGroupContent = {
  id: FormControl<StockMovementFormRawValue['id'] | NewStockMovement['id']>;
  movementDate: FormControl<StockMovementFormRawValue['movementDate']>;
  movementType: FormControl<StockMovementFormRawValue['movementType']>;
  quantity: FormControl<StockMovementFormRawValue['quantity']>;
  unitCost: FormControl<StockMovementFormRawValue['unitCost']>;
  referenceNumber: FormControl<StockMovementFormRawValue['referenceNumber']>;
  notes: FormControl<StockMovementFormRawValue['notes']>;
  deletedAt: FormControl<StockMovementFormRawValue['deletedAt']>;
};

export type StockMovementFormGroup = FormGroup<StockMovementFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StockMovementFormService {
  createStockMovementFormGroup(stockMovement?: StockMovementFormGroupInput): StockMovementFormGroup {
    const stockMovementRawValue = this.convertStockMovementToStockMovementRawValue({
      ...this.getFormDefaults(),
      ...(stockMovement ?? { id: null }),
    });
    return new FormGroup<StockMovementFormGroupContent>({
      id: new FormControl(
        { value: stockMovementRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      movementDate: new FormControl(stockMovementRawValue.movementDate, {
        validators: [Validators.required],
      }),
      movementType: new FormControl(stockMovementRawValue.movementType, {
        validators: [Validators.required],
      }),
      quantity: new FormControl(stockMovementRawValue.quantity, {
        validators: [Validators.required, Validators.min(0)],
      }),
      unitCost: new FormControl(stockMovementRawValue.unitCost, {
        validators: [Validators.min(0)],
      }),
      referenceNumber: new FormControl(stockMovementRawValue.referenceNumber, {
        validators: [Validators.maxLength(60)],
      }),
      notes: new FormControl(stockMovementRawValue.notes, {
        validators: [Validators.maxLength(500)],
      }),
      deletedAt: new FormControl(stockMovementRawValue.deletedAt),
    });
  }

  getStockMovement(form: StockMovementFormGroup): IStockMovement | NewStockMovement {
    return this.convertStockMovementRawValueToStockMovement(form.getRawValue() as StockMovementFormRawValue | NewStockMovementFormRawValue);
  }

  resetForm(form: StockMovementFormGroup, stockMovement: StockMovementFormGroupInput): void {
    const stockMovementRawValue = this.convertStockMovementToStockMovementRawValue({ ...this.getFormDefaults(), ...stockMovement });
    form.reset({
      ...stockMovementRawValue,
      id: { value: stockMovementRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): StockMovementFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      movementDate: currentTime,
      deletedAt: currentTime,
    };
  }

  private convertStockMovementRawValueToStockMovement(
    rawStockMovement: StockMovementFormRawValue | NewStockMovementFormRawValue,
  ): IStockMovement | NewStockMovement {
    return {
      ...rawStockMovement,
      movementDate: dayjs(rawStockMovement.movementDate, DATE_TIME_FORMAT),
      deletedAt: dayjs(rawStockMovement.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertStockMovementToStockMovementRawValue(
    stockMovement: IStockMovement | (Partial<NewStockMovement> & StockMovementFormDefaults),
  ): StockMovementFormRawValue | PartialWithRequiredKeyOf<NewStockMovementFormRawValue> {
    return {
      ...stockMovement,
      movementDate: stockMovement.movementDate ? stockMovement.movementDate.format(DATE_TIME_FORMAT) : undefined,
      deletedAt: stockMovement.deletedAt ? stockMovement.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
