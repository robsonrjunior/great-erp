import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICountry } from '../country.model';
import { CountryService } from '../service/country.service';

import { CountryFormGroup, CountryFormService } from './country-form.service';

@Component({
  selector: 'jhi-country-update',
  templateUrl: './country-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CountryUpdate implements OnInit {
  readonly isSaving = signal(false);
  country: ICountry | null = null;

  protected countryService = inject(CountryService);
  protected countryFormService = inject(CountryFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CountryFormGroup = this.countryFormService.createCountryFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ country }) => {
      this.country = country;
      if (country) {
        this.updateForm(country);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const country = this.countryFormService.getCountry(this.editForm);
    if (country.id === null) {
      this.subscribeToSaveResponse(this.countryService.create(country));
    } else {
      this.subscribeToSaveResponse(this.countryService.update(country));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICountry | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(country: ICountry): void {
    this.country = country;
    this.countryFormService.resetForm(this.editForm, country);
  }
}
