import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../warehouse.test-samples';

import { WarehouseFormService } from './warehouse-form.service';

describe('Warehouse Form Service', () => {
  let service: WarehouseFormService;

  beforeEach(() => {
    service = TestBed.inject(WarehouseFormService);
  });

  describe('Service methods', () => {
    describe('createWarehouseFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createWarehouseFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
            active: expect.any(Object),
            deletedAt: expect.any(Object),
            stockMovements: expect.any(Object),
            sales: expect.any(Object),
          }),
        );
      });

      it('passing IWarehouse should create a new form with FormGroup', () => {
        const formGroup = service.createWarehouseFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
            active: expect.any(Object),
            deletedAt: expect.any(Object),
            stockMovements: expect.any(Object),
            sales: expect.any(Object),
          }),
        );
      });
    });

    describe('getWarehouse', () => {
      it('should return NewWarehouse for default Warehouse initial value', () => {
        const formGroup = service.createWarehouseFormGroup(sampleWithNewData);

        const warehouse = service.getWarehouse(formGroup);

        expect(warehouse).toMatchObject(sampleWithNewData);
      });

      it('should return NewWarehouse for empty Warehouse initial value', () => {
        const formGroup = service.createWarehouseFormGroup();

        const warehouse = service.getWarehouse(formGroup);

        expect(warehouse).toMatchObject({});
      });

      it('should return IWarehouse', () => {
        const formGroup = service.createWarehouseFormGroup(sampleWithRequiredData);

        const warehouse = service.getWarehouse(formGroup);

        expect(warehouse).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IWarehouse should not enable id FormControl', () => {
        const formGroup = service.createWarehouseFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewWarehouse should disable id FormControl', () => {
        const formGroup = service.createWarehouseFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
