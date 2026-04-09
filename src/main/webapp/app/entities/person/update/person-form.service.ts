import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPerson, NewPerson } from '../person.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPerson for edit and NewPersonFormGroupInput for create.
 */
type PersonFormGroupInput = IPerson | PartialWithRequiredKeyOf<NewPerson>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPerson | NewPerson> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

type PersonFormRawValue = FormValueOf<IPerson>;

type NewPersonFormRawValue = FormValueOf<NewPerson>;

type PersonFormDefaults = Pick<NewPerson, 'id' | 'active' | 'deletedAt'>;

type PersonFormGroupContent = {
  id: FormControl<PersonFormRawValue['id'] | NewPerson['id']>;
  fullName: FormControl<PersonFormRawValue['fullName']>;
  cpf: FormControl<PersonFormRawValue['cpf']>;
  birthDate: FormControl<PersonFormRawValue['birthDate']>;
  email: FormControl<PersonFormRawValue['email']>;
  phone: FormControl<PersonFormRawValue['phone']>;
  active: FormControl<PersonFormRawValue['active']>;
  deletedAt: FormControl<PersonFormRawValue['deletedAt']>;
};

export type PersonFormGroup = FormGroup<PersonFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PersonFormService {
  createPersonFormGroup(person?: PersonFormGroupInput): PersonFormGroup {
    const personRawValue = this.convertPersonToPersonRawValue({
      ...this.getFormDefaults(),
      ...(person ?? { id: null }),
    });
    return new FormGroup<PersonFormGroupContent>({
      id: new FormControl(
        { value: personRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fullName: new FormControl(personRawValue.fullName, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(120)],
      }),
      cpf: new FormControl(personRawValue.cpf, {
        validators: [Validators.required, Validators.minLength(11), Validators.maxLength(14)],
      }),
      birthDate: new FormControl(personRawValue.birthDate),
      email: new FormControl(personRawValue.email, {
        validators: [
          Validators.maxLength(120),
          Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$'), // NOSONAR
        ],
      }),
      phone: new FormControl(personRawValue.phone, {
        validators: [Validators.maxLength(30)],
      }),
      active: new FormControl(personRawValue.active, {
        validators: [Validators.required],
      }),
      deletedAt: new FormControl(personRawValue.deletedAt),
    });
  }

  getPerson(form: PersonFormGroup): IPerson | NewPerson {
    return this.convertPersonRawValueToPerson(form.getRawValue() as PersonFormRawValue | NewPersonFormRawValue);
  }

  resetForm(form: PersonFormGroup, person: PersonFormGroupInput): void {
    const personRawValue = this.convertPersonToPersonRawValue({ ...this.getFormDefaults(), ...person });
    form.reset({
      ...personRawValue,
      id: { value: personRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PersonFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      deletedAt: currentTime,
    };
  }

  private convertPersonRawValueToPerson(rawPerson: PersonFormRawValue | NewPersonFormRawValue): IPerson | NewPerson {
    return {
      ...rawPerson,
      deletedAt: dayjs(rawPerson.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertPersonToPersonRawValue(
    person: IPerson | (Partial<NewPerson> & PersonFormDefaults),
  ): PersonFormRawValue | PartialWithRequiredKeyOf<NewPersonFormRawValue> {
    return {
      ...person,
      deletedAt: person.deletedAt ? person.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
