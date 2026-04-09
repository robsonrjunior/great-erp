import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICountry } from '../country.model';
import { CountryService } from '../service/country.service';

import { CountryFormService } from './country-form.service';
import { CountryUpdate } from './country-update';

describe('Country Management Update Component', () => {
  let comp: CountryUpdate;
  let fixture: ComponentFixture<CountryUpdate>;
  let activatedRoute: ActivatedRoute;
  let countryFormService: CountryFormService;
  let countryService: CountryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(CountryUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    countryFormService = TestBed.inject(CountryFormService);
    countryService = TestBed.inject(CountryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const country: ICountry = { id: 2258 };

      activatedRoute.data = of({ country });
      comp.ngOnInit();

      expect(comp.country).toEqual(country);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICountry>();
      const country = { id: 21165 };
      vitest.spyOn(countryFormService, 'getCountry').mockReturnValue(country);
      vitest.spyOn(countryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ country });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(country);
      saveSubject.complete();

      // THEN
      expect(countryFormService.getCountry).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(countryService.update).toHaveBeenCalledWith(expect.objectContaining(country));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICountry>();
      const country = { id: 21165 };
      vitest.spyOn(countryFormService, 'getCountry').mockReturnValue({ id: null });
      vitest.spyOn(countryService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ country: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(country);
      saveSubject.complete();

      // THEN
      expect(countryFormService.getCountry).toHaveBeenCalled();
      expect(countryService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICountry>();
      const country = { id: 21165 };
      vitest.spyOn(countryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ country });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(countryService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
