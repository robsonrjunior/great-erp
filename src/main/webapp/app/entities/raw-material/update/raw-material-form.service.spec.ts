import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../raw-material.test-samples';

import { RawMaterialFormService } from './raw-material-form.service';

describe('RawMaterial Form Service', () => {
  let service: RawMaterialFormService;

  beforeEach(() => {
    service = TestBed.inject(RawMaterialFormService);
  });

  describe('Service methods', () => {
    describe('createRawMaterialFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRawMaterialFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            sku: expect.any(Object),
            unitOfMeasure: expect.any(Object),
            unitDecimalPlaces: expect.any(Object),
            unitCost: expect.any(Object),
            minStock: expect.any(Object),
            active: expect.any(Object),
            deletedAt: expect.any(Object),
            stockMovements: expect.any(Object),
          }),
        );
      });

      it('passing IRawMaterial should create a new form with FormGroup', () => {
        const formGroup = service.createRawMaterialFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            sku: expect.any(Object),
            unitOfMeasure: expect.any(Object),
            unitDecimalPlaces: expect.any(Object),
            unitCost: expect.any(Object),
            minStock: expect.any(Object),
            active: expect.any(Object),
            deletedAt: expect.any(Object),
            stockMovements: expect.any(Object),
          }),
        );
      });
    });

    describe('getRawMaterial', () => {
      it('should return NewRawMaterial for default RawMaterial initial value', () => {
        const formGroup = service.createRawMaterialFormGroup(sampleWithNewData);

        const rawMaterial = service.getRawMaterial(formGroup);

        expect(rawMaterial).toMatchObject(sampleWithNewData);
      });

      it('should return NewRawMaterial for empty RawMaterial initial value', () => {
        const formGroup = service.createRawMaterialFormGroup();

        const rawMaterial = service.getRawMaterial(formGroup);

        expect(rawMaterial).toMatchObject({});
      });

      it('should return IRawMaterial', () => {
        const formGroup = service.createRawMaterialFormGroup(sampleWithRequiredData);

        const rawMaterial = service.getRawMaterial(formGroup);

        expect(rawMaterial).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRawMaterial should not enable id FormControl', () => {
        const formGroup = service.createRawMaterialFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRawMaterial should disable id FormControl', () => {
        const formGroup = service.createRawMaterialFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
