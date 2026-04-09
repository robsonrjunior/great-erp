import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISupplier, NewSupplier } from '../supplier.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISupplier for edit and NewSupplierFormGroupInput for create.
 */
type SupplierFormGroupInput = ISupplier | PartialWithRequiredKeyOf<NewSupplier>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISupplier | NewSupplier> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

type SupplierFormRawValue = FormValueOf<ISupplier>;

type NewSupplierFormRawValue = FormValueOf<NewSupplier>;

type SupplierFormDefaults = Pick<NewSupplier, 'id' | 'active' | 'deletedAt'>;

type SupplierFormGroupContent = {
  id: FormControl<SupplierFormRawValue['id'] | NewSupplier['id']>;
  legalName: FormControl<SupplierFormRawValue['legalName']>;
  tradeName: FormControl<SupplierFormRawValue['tradeName']>;
  taxId: FormControl<SupplierFormRawValue['taxId']>;
  partyType: FormControl<SupplierFormRawValue['partyType']>;
  email: FormControl<SupplierFormRawValue['email']>;
  phone: FormControl<SupplierFormRawValue['phone']>;
  active: FormControl<SupplierFormRawValue['active']>;
  deletedAt: FormControl<SupplierFormRawValue['deletedAt']>;
  person: FormControl<SupplierFormRawValue['person']>;
  company: FormControl<SupplierFormRawValue['company']>;
  rawMaterials: FormControl<SupplierFormRawValue['rawMaterials']>;
};

export type SupplierFormGroup = FormGroup<SupplierFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SupplierFormService {
  createSupplierFormGroup(supplier?: SupplierFormGroupInput): SupplierFormGroup {
    const supplierRawValue = this.convertSupplierToSupplierRawValue({
      ...this.getFormDefaults(),
      ...(supplier ?? { id: null }),
    });
    return new FormGroup<SupplierFormGroupContent>({
      id: new FormControl(
        { value: supplierRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      legalName: new FormControl(supplierRawValue.legalName, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(120)],
      }),
      tradeName: new FormControl(supplierRawValue.tradeName, {
        validators: [Validators.maxLength(120)],
      }),
      taxId: new FormControl(supplierRawValue.taxId, {
        validators: [Validators.required, Validators.minLength(11), Validators.maxLength(20)],
      }),
      partyType: new FormControl(supplierRawValue.partyType, {
        validators: [Validators.required],
      }),
      email: new FormControl(supplierRawValue.email, {
        validators: [
          Validators.maxLength(120),
          Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$'), // NOSONAR
        ],
      }),
      phone: new FormControl(supplierRawValue.phone, {
        validators: [Validators.maxLength(30)],
      }),
      active: new FormControl(supplierRawValue.active, {
        validators: [Validators.required],
      }),
      deletedAt: new FormControl(supplierRawValue.deletedAt),
      person: new FormControl(supplierRawValue.person),
      company: new FormControl(supplierRawValue.company),
      rawMaterials: new FormControl(supplierRawValue.rawMaterials),
    });
  }

  getSupplier(form: SupplierFormGroup): ISupplier | NewSupplier {
    return this.convertSupplierRawValueToSupplier(form.getRawValue() as SupplierFormRawValue | NewSupplierFormRawValue);
  }

  resetForm(form: SupplierFormGroup, supplier: SupplierFormGroupInput): void {
    const supplierRawValue = this.convertSupplierToSupplierRawValue({ ...this.getFormDefaults(), ...supplier });
    form.reset({
      ...supplierRawValue,
      id: { value: supplierRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SupplierFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      deletedAt: currentTime,
    };
  }

  private convertSupplierRawValueToSupplier(rawSupplier: SupplierFormRawValue | NewSupplierFormRawValue): ISupplier | NewSupplier {
    return {
      ...rawSupplier,
      deletedAt: dayjs(rawSupplier.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertSupplierToSupplierRawValue(
    supplier: ISupplier | (Partial<NewSupplier> & SupplierFormDefaults),
  ): SupplierFormRawValue | PartialWithRequiredKeyOf<NewSupplierFormRawValue> {
    return {
      ...supplier,
      deletedAt: supplier.deletedAt ? supplier.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
