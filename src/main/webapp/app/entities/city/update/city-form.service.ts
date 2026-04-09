import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICity, NewCity } from '../city.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICity for edit and NewCityFormGroupInput for create.
 */
type CityFormGroupInput = ICity | PartialWithRequiredKeyOf<NewCity>;

type CityFormDefaults = Pick<NewCity, 'id'>;

type CityFormGroupContent = {
  id: FormControl<ICity['id'] | NewCity['id']>;
  name: FormControl<ICity['name']>;
  suppliers: FormControl<ICity['suppliers']>;
  customers: FormControl<ICity['customers']>;
  people: FormControl<ICity['people']>;
  companies: FormControl<ICity['companies']>;
  warehouses: FormControl<ICity['warehouses']>;
  state: FormControl<ICity['state']>;
};

export type CityFormGroup = FormGroup<CityFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CityFormService {
  createCityFormGroup(city?: CityFormGroupInput): CityFormGroup {
    const cityRawValue = {
      ...this.getFormDefaults(),
      ...(city ?? { id: null }),
    };
    return new FormGroup<CityFormGroupContent>({
      id: new FormControl(
        { value: cityRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(cityRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      suppliers: new FormControl(cityRawValue.suppliers),
      customers: new FormControl(cityRawValue.customers),
      people: new FormControl(cityRawValue.people),
      companies: new FormControl(cityRawValue.companies),
      warehouses: new FormControl(cityRawValue.warehouses),
      state: new FormControl(cityRawValue.state, {
        validators: [Validators.required],
      }),
    });
  }

  getCity(form: CityFormGroup): ICity | NewCity {
    return form.getRawValue() as ICity | NewCity;
  }

  resetForm(form: CityFormGroup, city: CityFormGroupInput): void {
    const cityRawValue = { ...this.getFormDefaults(), ...city };
    form.reset({
      ...cityRawValue,
      id: { value: cityRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CityFormDefaults {
    return {
      id: null,
    };
  }
}
