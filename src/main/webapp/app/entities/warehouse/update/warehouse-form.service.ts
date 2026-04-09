import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IWarehouse, NewWarehouse } from '../warehouse.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWarehouse for edit and NewWarehouseFormGroupInput for create.
 */
type WarehouseFormGroupInput = IWarehouse | PartialWithRequiredKeyOf<NewWarehouse>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IWarehouse | NewWarehouse> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

type WarehouseFormRawValue = FormValueOf<IWarehouse>;

type NewWarehouseFormRawValue = FormValueOf<NewWarehouse>;

type WarehouseFormDefaults = Pick<NewWarehouse, 'id' | 'active' | 'deletedAt'>;

type WarehouseFormGroupContent = {
  id: FormControl<WarehouseFormRawValue['id'] | NewWarehouse['id']>;
  name: FormControl<WarehouseFormRawValue['name']>;
  code: FormControl<WarehouseFormRawValue['code']>;
  active: FormControl<WarehouseFormRawValue['active']>;
  deletedAt: FormControl<WarehouseFormRawValue['deletedAt']>;
  stockMovements: FormControl<WarehouseFormRawValue['stockMovements']>;
  sales: FormControl<WarehouseFormRawValue['sales']>;
};

export type WarehouseFormGroup = FormGroup<WarehouseFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class WarehouseFormService {
  createWarehouseFormGroup(warehouse?: WarehouseFormGroupInput): WarehouseFormGroup {
    const warehouseRawValue = this.convertWarehouseToWarehouseRawValue({
      ...this.getFormDefaults(),
      ...(warehouse ?? { id: null }),
    });
    return new FormGroup<WarehouseFormGroupContent>({
      id: new FormControl(
        { value: warehouseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(warehouseRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(120)],
      }),
      code: new FormControl(warehouseRawValue.code, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(30)],
      }),
      active: new FormControl(warehouseRawValue.active, {
        validators: [Validators.required],
      }),
      deletedAt: new FormControl(warehouseRawValue.deletedAt),
      stockMovements: new FormControl(warehouseRawValue.stockMovements),
      sales: new FormControl(warehouseRawValue.sales),
    });
  }

  getWarehouse(form: WarehouseFormGroup): IWarehouse | NewWarehouse {
    return this.convertWarehouseRawValueToWarehouse(form.getRawValue() as WarehouseFormRawValue | NewWarehouseFormRawValue);
  }

  resetForm(form: WarehouseFormGroup, warehouse: WarehouseFormGroupInput): void {
    const warehouseRawValue = this.convertWarehouseToWarehouseRawValue({ ...this.getFormDefaults(), ...warehouse });
    form.reset({
      ...warehouseRawValue,
      id: { value: warehouseRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): WarehouseFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      deletedAt: currentTime,
    };
  }

  private convertWarehouseRawValueToWarehouse(rawWarehouse: WarehouseFormRawValue | NewWarehouseFormRawValue): IWarehouse | NewWarehouse {
    return {
      ...rawWarehouse,
      deletedAt: dayjs(rawWarehouse.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertWarehouseToWarehouseRawValue(
    warehouse: IWarehouse | (Partial<NewWarehouse> & WarehouseFormDefaults),
  ): WarehouseFormRawValue | PartialWithRequiredKeyOf<NewWarehouseFormRawValue> {
    return {
      ...warehouse,
      deletedAt: warehouse.deletedAt ? warehouse.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
