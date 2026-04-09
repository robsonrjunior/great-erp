import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRawMaterial, NewRawMaterial } from '../raw-material.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRawMaterial for edit and NewRawMaterialFormGroupInput for create.
 */
type RawMaterialFormGroupInput = IRawMaterial | PartialWithRequiredKeyOf<NewRawMaterial>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRawMaterial | NewRawMaterial> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

type RawMaterialFormRawValue = FormValueOf<IRawMaterial>;

type NewRawMaterialFormRawValue = FormValueOf<NewRawMaterial>;

type RawMaterialFormDefaults = Pick<NewRawMaterial, 'id' | 'active' | 'deletedAt'>;

type RawMaterialFormGroupContent = {
  id: FormControl<RawMaterialFormRawValue['id'] | NewRawMaterial['id']>;
  name: FormControl<RawMaterialFormRawValue['name']>;
  sku: FormControl<RawMaterialFormRawValue['sku']>;
  unitOfMeasure: FormControl<RawMaterialFormRawValue['unitOfMeasure']>;
  unitDecimalPlaces: FormControl<RawMaterialFormRawValue['unitDecimalPlaces']>;
  unitCost: FormControl<RawMaterialFormRawValue['unitCost']>;
  minStock: FormControl<RawMaterialFormRawValue['minStock']>;
  active: FormControl<RawMaterialFormRawValue['active']>;
  deletedAt: FormControl<RawMaterialFormRawValue['deletedAt']>;
  stockMovements: FormControl<RawMaterialFormRawValue['stockMovements']>;
};

export type RawMaterialFormGroup = FormGroup<RawMaterialFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RawMaterialFormService {
  createRawMaterialFormGroup(rawMaterial?: RawMaterialFormGroupInput): RawMaterialFormGroup {
    const rawMaterialRawValue = this.convertRawMaterialToRawMaterialRawValue({
      ...this.getFormDefaults(),
      ...(rawMaterial ?? { id: null }),
    });
    return new FormGroup<RawMaterialFormGroupContent>({
      id: new FormControl(
        { value: rawMaterialRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(rawMaterialRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(120)],
      }),
      sku: new FormControl(rawMaterialRawValue.sku, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(40)],
      }),
      unitOfMeasure: new FormControl(rawMaterialRawValue.unitOfMeasure, {
        validators: [Validators.required],
      }),
      unitDecimalPlaces: new FormControl(rawMaterialRawValue.unitDecimalPlaces, {
        validators: [Validators.required, Validators.min(0), Validators.max(6)],
      }),
      unitCost: new FormControl(rawMaterialRawValue.unitCost, {
        validators: [Validators.min(0)],
      }),
      minStock: new FormControl(rawMaterialRawValue.minStock, {
        validators: [Validators.min(0)],
      }),
      active: new FormControl(rawMaterialRawValue.active, {
        validators: [Validators.required],
      }),
      deletedAt: new FormControl(rawMaterialRawValue.deletedAt),
      stockMovements: new FormControl(rawMaterialRawValue.stockMovements),
    });
  }

  getRawMaterial(form: RawMaterialFormGroup): IRawMaterial | NewRawMaterial {
    return this.convertRawMaterialRawValueToRawMaterial(form.getRawValue() as RawMaterialFormRawValue | NewRawMaterialFormRawValue);
  }

  resetForm(form: RawMaterialFormGroup, rawMaterial: RawMaterialFormGroupInput): void {
    const rawMaterialRawValue = this.convertRawMaterialToRawMaterialRawValue({ ...this.getFormDefaults(), ...rawMaterial });
    form.reset({
      ...rawMaterialRawValue,
      id: { value: rawMaterialRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): RawMaterialFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      deletedAt: currentTime,
    };
  }

  private convertRawMaterialRawValueToRawMaterial(
    rawRawMaterial: RawMaterialFormRawValue | NewRawMaterialFormRawValue,
  ): IRawMaterial | NewRawMaterial {
    return {
      ...rawRawMaterial,
      deletedAt: dayjs(rawRawMaterial.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertRawMaterialToRawMaterialRawValue(
    rawMaterial: IRawMaterial | (Partial<NewRawMaterial> & RawMaterialFormDefaults),
  ): RawMaterialFormRawValue | PartialWithRequiredKeyOf<NewRawMaterialFormRawValue> {
    return {
      ...rawMaterial,
      deletedAt: rawMaterial.deletedAt ? rawMaterial.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
