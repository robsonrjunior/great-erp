import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICustomer, NewCustomer } from '../customer.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICustomer for edit and NewCustomerFormGroupInput for create.
 */
type CustomerFormGroupInput = ICustomer | PartialWithRequiredKeyOf<NewCustomer>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICustomer | NewCustomer> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

type CustomerFormRawValue = FormValueOf<ICustomer>;

type NewCustomerFormRawValue = FormValueOf<NewCustomer>;

type CustomerFormDefaults = Pick<NewCustomer, 'id' | 'active' | 'deletedAt'>;

type CustomerFormGroupContent = {
  id: FormControl<CustomerFormRawValue['id'] | NewCustomer['id']>;
  legalName: FormControl<CustomerFormRawValue['legalName']>;
  tradeName: FormControl<CustomerFormRawValue['tradeName']>;
  taxId: FormControl<CustomerFormRawValue['taxId']>;
  partyType: FormControl<CustomerFormRawValue['partyType']>;
  email: FormControl<CustomerFormRawValue['email']>;
  phone: FormControl<CustomerFormRawValue['phone']>;
  active: FormControl<CustomerFormRawValue['active']>;
  deletedAt: FormControl<CustomerFormRawValue['deletedAt']>;
  person: FormControl<CustomerFormRawValue['person']>;
  company: FormControl<CustomerFormRawValue['company']>;
  sales: FormControl<CustomerFormRawValue['sales']>;
};

export type CustomerFormGroup = FormGroup<CustomerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CustomerFormService {
  createCustomerFormGroup(customer?: CustomerFormGroupInput): CustomerFormGroup {
    const customerRawValue = this.convertCustomerToCustomerRawValue({
      ...this.getFormDefaults(),
      ...(customer ?? { id: null }),
    });
    return new FormGroup<CustomerFormGroupContent>({
      id: new FormControl(
        { value: customerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      legalName: new FormControl(customerRawValue.legalName, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(120)],
      }),
      tradeName: new FormControl(customerRawValue.tradeName, {
        validators: [Validators.maxLength(120)],
      }),
      taxId: new FormControl(customerRawValue.taxId, {
        validators: [Validators.required, Validators.minLength(11), Validators.maxLength(20)],
      }),
      partyType: new FormControl(customerRawValue.partyType, {
        validators: [Validators.required],
      }),
      email: new FormControl(customerRawValue.email, {
        validators: [
          Validators.maxLength(120),
          Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$'), // NOSONAR
        ],
      }),
      phone: new FormControl(customerRawValue.phone, {
        validators: [Validators.maxLength(30)],
      }),
      active: new FormControl(customerRawValue.active, {
        validators: [Validators.required],
      }),
      deletedAt: new FormControl(customerRawValue.deletedAt),
      person: new FormControl(customerRawValue.person),
      company: new FormControl(customerRawValue.company),
      sales: new FormControl(customerRawValue.sales),
    });
  }

  getCustomer(form: CustomerFormGroup): ICustomer | NewCustomer {
    return this.convertCustomerRawValueToCustomer(form.getRawValue() as CustomerFormRawValue | NewCustomerFormRawValue);
  }

  resetForm(form: CustomerFormGroup, customer: CustomerFormGroupInput): void {
    const customerRawValue = this.convertCustomerToCustomerRawValue({ ...this.getFormDefaults(), ...customer });
    form.reset({
      ...customerRawValue,
      id: { value: customerRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CustomerFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      deletedAt: currentTime,
    };
  }

  private convertCustomerRawValueToCustomer(rawCustomer: CustomerFormRawValue | NewCustomerFormRawValue): ICustomer | NewCustomer {
    return {
      ...rawCustomer,
      deletedAt: dayjs(rawCustomer.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertCustomerToCustomerRawValue(
    customer: ICustomer | (Partial<NewCustomer> & CustomerFormDefaults),
  ): CustomerFormRawValue | PartialWithRequiredKeyOf<NewCustomerFormRawValue> {
    return {
      ...customer,
      deletedAt: customer.deletedAt ? customer.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
