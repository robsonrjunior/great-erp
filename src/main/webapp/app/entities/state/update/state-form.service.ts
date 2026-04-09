import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IState, NewState } from '../state.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IState for edit and NewStateFormGroupInput for create.
 */
type StateFormGroupInput = IState | PartialWithRequiredKeyOf<NewState>;

type StateFormDefaults = Pick<NewState, 'id'>;

type StateFormGroupContent = {
  id: FormControl<IState['id'] | NewState['id']>;
  name: FormControl<IState['name']>;
  code: FormControl<IState['code']>;
  country: FormControl<IState['country']>;
};

export type StateFormGroup = FormGroup<StateFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StateFormService {
  createStateFormGroup(state?: StateFormGroupInput): StateFormGroup {
    const stateRawValue = {
      ...this.getFormDefaults(),
      ...(state ?? { id: null }),
    };
    return new FormGroup<StateFormGroupContent>({
      id: new FormControl(
        { value: stateRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(stateRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      code: new FormControl(stateRawValue.code, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(10)],
      }),
      country: new FormControl(stateRawValue.country, {
        validators: [Validators.required],
      }),
    });
  }

  getState(form: StateFormGroup): IState | NewState {
    return form.getRawValue() as IState | NewState;
  }

  resetForm(form: StateFormGroup, state: StateFormGroupInput): void {
    const stateRawValue = { ...this.getFormDefaults(), ...state };
    form.reset({
      ...stateRawValue,
      id: { value: stateRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): StateFormDefaults {
    return {
      id: null,
    };
  }
}
