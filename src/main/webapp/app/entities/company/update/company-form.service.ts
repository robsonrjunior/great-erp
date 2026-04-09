import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICompany, NewCompany } from '../company.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICompany for edit and NewCompanyFormGroupInput for create.
 */
type CompanyFormGroupInput = ICompany | PartialWithRequiredKeyOf<NewCompany>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICompany | NewCompany> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

type CompanyFormRawValue = FormValueOf<ICompany>;

type NewCompanyFormRawValue = FormValueOf<NewCompany>;

type CompanyFormDefaults = Pick<NewCompany, 'id' | 'active' | 'deletedAt'>;

type CompanyFormGroupContent = {
  id: FormControl<CompanyFormRawValue['id'] | NewCompany['id']>;
  legalName: FormControl<CompanyFormRawValue['legalName']>;
  tradeName: FormControl<CompanyFormRawValue['tradeName']>;
  cnpj: FormControl<CompanyFormRawValue['cnpj']>;
  stateRegistration: FormControl<CompanyFormRawValue['stateRegistration']>;
  email: FormControl<CompanyFormRawValue['email']>;
  phone: FormControl<CompanyFormRawValue['phone']>;
  active: FormControl<CompanyFormRawValue['active']>;
  deletedAt: FormControl<CompanyFormRawValue['deletedAt']>;
};

export type CompanyFormGroup = FormGroup<CompanyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CompanyFormService {
  createCompanyFormGroup(company?: CompanyFormGroupInput): CompanyFormGroup {
    const companyRawValue = this.convertCompanyToCompanyRawValue({
      ...this.getFormDefaults(),
      ...(company ?? { id: null }),
    });
    return new FormGroup<CompanyFormGroupContent>({
      id: new FormControl(
        { value: companyRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      legalName: new FormControl(companyRawValue.legalName, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(120)],
      }),
      tradeName: new FormControl(companyRawValue.tradeName, {
        validators: [Validators.maxLength(120)],
      }),
      cnpj: new FormControl(companyRawValue.cnpj, {
        validators: [Validators.required, Validators.minLength(14), Validators.maxLength(18)],
      }),
      stateRegistration: new FormControl(companyRawValue.stateRegistration, {
        validators: [Validators.maxLength(30)],
      }),
      email: new FormControl(companyRawValue.email, {
        validators: [
          Validators.maxLength(120),
          Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$'), // NOSONAR
        ],
      }),
      phone: new FormControl(companyRawValue.phone, {
        validators: [Validators.maxLength(30)],
      }),
      active: new FormControl(companyRawValue.active, {
        validators: [Validators.required],
      }),
      deletedAt: new FormControl(companyRawValue.deletedAt),
    });
  }

  getCompany(form: CompanyFormGroup): ICompany | NewCompany {
    return this.convertCompanyRawValueToCompany(form.getRawValue() as CompanyFormRawValue | NewCompanyFormRawValue);
  }

  resetForm(form: CompanyFormGroup, company: CompanyFormGroupInput): void {
    const companyRawValue = this.convertCompanyToCompanyRawValue({ ...this.getFormDefaults(), ...company });
    form.reset({
      ...companyRawValue,
      id: { value: companyRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CompanyFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      deletedAt: currentTime,
    };
  }

  private convertCompanyRawValueToCompany(rawCompany: CompanyFormRawValue | NewCompanyFormRawValue): ICompany | NewCompany {
    return {
      ...rawCompany,
      deletedAt: dayjs(rawCompany.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertCompanyToCompanyRawValue(
    company: ICompany | (Partial<NewCompany> & CompanyFormDefaults),
  ): CompanyFormRawValue | PartialWithRequiredKeyOf<NewCompanyFormRawValue> {
    return {
      ...company,
      deletedAt: company.deletedAt ? company.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
