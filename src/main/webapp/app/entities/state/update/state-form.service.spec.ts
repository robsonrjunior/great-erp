import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../state.test-samples';

import { StateFormService } from './state-form.service';

describe('State Form Service', () => {
  let service: StateFormService;

  beforeEach(() => {
    service = TestBed.inject(StateFormService);
  });

  describe('Service methods', () => {
    describe('createStateFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStateFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
            country: expect.any(Object),
          }),
        );
      });

      it('passing IState should create a new form with FormGroup', () => {
        const formGroup = service.createStateFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
            country: expect.any(Object),
          }),
        );
      });
    });

    describe('getState', () => {
      it('should return NewState for default State initial value', () => {
        const formGroup = service.createStateFormGroup(sampleWithNewData);

        const state = service.getState(formGroup);

        expect(state).toMatchObject(sampleWithNewData);
      });

      it('should return NewState for empty State initial value', () => {
        const formGroup = service.createStateFormGroup();

        const state = service.getState(formGroup);

        expect(state).toMatchObject({});
      });

      it('should return IState', () => {
        const formGroup = service.createStateFormGroup(sampleWithRequiredData);

        const state = service.getState(formGroup);

        expect(state).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IState should not enable id FormControl', () => {
        const formGroup = service.createStateFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewState should disable id FormControl', () => {
        const formGroup = service.createStateFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
