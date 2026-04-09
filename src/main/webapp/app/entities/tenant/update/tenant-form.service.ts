import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITenant, NewTenant } from '../tenant.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITenant for edit and NewTenantFormGroupInput for create.
 */
type TenantFormGroupInput = ITenant | PartialWithRequiredKeyOf<NewTenant>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITenant | NewTenant> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

type TenantFormRawValue = FormValueOf<ITenant>;

type NewTenantFormRawValue = FormValueOf<NewTenant>;

type TenantFormDefaults = Pick<NewTenant, 'id' | 'active' | 'deletedAt'>;

type TenantFormGroupContent = {
  id: FormControl<TenantFormRawValue['id'] | NewTenant['id']>;
  name: FormControl<TenantFormRawValue['name']>;
  code: FormControl<TenantFormRawValue['code']>;
  active: FormControl<TenantFormRawValue['active']>;
  deletedAt: FormControl<TenantFormRawValue['deletedAt']>;
  customers: FormControl<TenantFormRawValue['customers']>;
  suppliers: FormControl<TenantFormRawValue['suppliers']>;
  people: FormControl<TenantFormRawValue['people']>;
  companies: FormControl<TenantFormRawValue['companies']>;
  products: FormControl<TenantFormRawValue['products']>;
  rawMaterials: FormControl<TenantFormRawValue['rawMaterials']>;
  warehouses: FormControl<TenantFormRawValue['warehouses']>;
  sales: FormControl<TenantFormRawValue['sales']>;
  saleItems: FormControl<TenantFormRawValue['saleItems']>;
  stockMovements: FormControl<TenantFormRawValue['stockMovements']>;
};

export type TenantFormGroup = FormGroup<TenantFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TenantFormService {
  createTenantFormGroup(tenant?: TenantFormGroupInput): TenantFormGroup {
    const tenantRawValue = this.convertTenantToTenantRawValue({
      ...this.getFormDefaults(),
      ...(tenant ?? { id: null }),
    });
    return new FormGroup<TenantFormGroupContent>({
      id: new FormControl(
        { value: tenantRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(tenantRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      code: new FormControl(tenantRawValue.code, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(30)],
      }),
      active: new FormControl(tenantRawValue.active, {
        validators: [Validators.required],
      }),
      deletedAt: new FormControl(tenantRawValue.deletedAt),
      customers: new FormControl(tenantRawValue.customers),
      suppliers: new FormControl(tenantRawValue.suppliers),
      people: new FormControl(tenantRawValue.people),
      companies: new FormControl(tenantRawValue.companies),
      products: new FormControl(tenantRawValue.products),
      rawMaterials: new FormControl(tenantRawValue.rawMaterials),
      warehouses: new FormControl(tenantRawValue.warehouses),
      sales: new FormControl(tenantRawValue.sales),
      saleItems: new FormControl(tenantRawValue.saleItems),
      stockMovements: new FormControl(tenantRawValue.stockMovements),
    });
  }

  getTenant(form: TenantFormGroup): ITenant | NewTenant {
    return this.convertTenantRawValueToTenant(form.getRawValue() as TenantFormRawValue | NewTenantFormRawValue);
  }

  resetForm(form: TenantFormGroup, tenant: TenantFormGroupInput): void {
    const tenantRawValue = this.convertTenantToTenantRawValue({ ...this.getFormDefaults(), ...tenant });
    form.reset({
      ...tenantRawValue,
      id: { value: tenantRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TenantFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      deletedAt: currentTime,
    };
  }

  private convertTenantRawValueToTenant(rawTenant: TenantFormRawValue | NewTenantFormRawValue): ITenant | NewTenant {
    return {
      ...rawTenant,
      deletedAt: dayjs(rawTenant.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertTenantToTenantRawValue(
    tenant: ITenant | (Partial<NewTenant> & TenantFormDefaults),
  ): TenantFormRawValue | PartialWithRequiredKeyOf<NewTenantFormRawValue> {
    return {
      ...tenant,
      deletedAt: tenant.deletedAt ? tenant.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
