import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICountry } from 'app/entities/country/country.model';
import { CountryService } from 'app/entities/country/service/country.service';
import { StateService } from '../service/state.service';
import { IState } from '../state.model';

import { StateFormService } from './state-form.service';
import { StateUpdate } from './state-update';

describe('State Management Update Component', () => {
  let comp: StateUpdate;
  let fixture: ComponentFixture<StateUpdate>;
  let activatedRoute: ActivatedRoute;
  let stateFormService: StateFormService;
  let stateService: StateService;
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

    fixture = TestBed.createComponent(StateUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    stateFormService = TestBed.inject(StateFormService);
    stateService = TestBed.inject(StateService);
    countryService = TestBed.inject(CountryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Country query and add missing value', () => {
      const state: IState = { id: 6174 };
      const country: ICountry = { id: 21165 };
      state.country = country;

      const countryCollection: ICountry[] = [{ id: 21165 }];
      vitest.spyOn(countryService, 'query').mockReturnValue(of(new HttpResponse({ body: countryCollection })));
      const additionalCountries = [country];
      const expectedCollection: ICountry[] = [...additionalCountries, ...countryCollection];
      vitest.spyOn(countryService, 'addCountryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ state });
      comp.ngOnInit();

      expect(countryService.query).toHaveBeenCalled();
      expect(countryService.addCountryToCollectionIfMissing).toHaveBeenCalledWith(
        countryCollection,
        ...additionalCountries.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.countriesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const state: IState = { id: 6174 };
      const country: ICountry = { id: 21165 };
      state.country = country;

      activatedRoute.data = of({ state });
      comp.ngOnInit();

      expect(comp.countriesSharedCollection()).toContainEqual(country);
      expect(comp.state).toEqual(state);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IState>();
      const state = { id: 31448 };
      vitest.spyOn(stateFormService, 'getState').mockReturnValue(state);
      vitest.spyOn(stateService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ state });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(state);
      saveSubject.complete();

      // THEN
      expect(stateFormService.getState).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(stateService.update).toHaveBeenCalledWith(expect.objectContaining(state));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IState>();
      const state = { id: 31448 };
      vitest.spyOn(stateFormService, 'getState').mockReturnValue({ id: null });
      vitest.spyOn(stateService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ state: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(state);
      saveSubject.complete();

      // THEN
      expect(stateFormService.getState).toHaveBeenCalled();
      expect(stateService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IState>();
      const state = { id: 31448 };
      vitest.spyOn(stateService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ state });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(stateService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCountry', () => {
      it('should forward to countryService', () => {
        const entity = { id: 21165 };
        const entity2 = { id: 2258 };
        vitest.spyOn(countryService, 'compareCountry');
        comp.compareCountry(entity, entity2);
        expect(countryService.compareCountry).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
