import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../person.test-samples';

import { PersonFormService } from './person-form.service';

describe('Person Form Service', () => {
  let service: PersonFormService;

  beforeEach(() => {
    service = TestBed.inject(PersonFormService);
  });

  describe('Service methods', () => {
    describe('createPersonFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPersonFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fullName: expect.any(Object),
            cpf: expect.any(Object),
            birthDate: expect.any(Object),
            email: expect.any(Object),
            phone: expect.any(Object),
            active: expect.any(Object),
            deletedAt: expect.any(Object),
          }),
        );
      });

      it('passing IPerson should create a new form with FormGroup', () => {
        const formGroup = service.createPersonFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fullName: expect.any(Object),
            cpf: expect.any(Object),
            birthDate: expect.any(Object),
            email: expect.any(Object),
            phone: expect.any(Object),
            active: expect.any(Object),
            deletedAt: expect.any(Object),
          }),
        );
      });
    });

    describe('getPerson', () => {
      it('should return NewPerson for default Person initial value', () => {
        const formGroup = service.createPersonFormGroup(sampleWithNewData);

        const person = service.getPerson(formGroup);

        expect(person).toMatchObject(sampleWithNewData);
      });

      it('should return NewPerson for empty Person initial value', () => {
        const formGroup = service.createPersonFormGroup();

        const person = service.getPerson(formGroup);

        expect(person).toMatchObject({});
      });

      it('should return IPerson', () => {
        const formGroup = service.createPersonFormGroup(sampleWithRequiredData);

        const person = service.getPerson(formGroup);

        expect(person).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPerson should not enable id FormControl', () => {
        const formGroup = service.createPersonFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPerson should disable id FormControl', () => {
        const formGroup = service.createPersonFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
