import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../sale.test-samples';

import { SaleFormService } from './sale-form.service';

describe('Sale Form Service', () => {
  let service: SaleFormService;

  beforeEach(() => {
    service = TestBed.inject(SaleFormService);
  });

  describe('Service methods', () => {
    describe('createSaleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSaleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            saleDate: expect.any(Object),
            saleNumber: expect.any(Object),
            status: expect.any(Object),
            grossAmount: expect.any(Object),
            discountAmount: expect.any(Object),
            netAmount: expect.any(Object),
            notes: expect.any(Object),
            deletedAt: expect.any(Object),
            items: expect.any(Object),
          }),
        );
      });

      it('passing ISale should create a new form with FormGroup', () => {
        const formGroup = service.createSaleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            saleDate: expect.any(Object),
            saleNumber: expect.any(Object),
            status: expect.any(Object),
            grossAmount: expect.any(Object),
            discountAmount: expect.any(Object),
            netAmount: expect.any(Object),
            notes: expect.any(Object),
            deletedAt: expect.any(Object),
            items: expect.any(Object),
          }),
        );
      });
    });

    describe('getSale', () => {
      it('should return NewSale for default Sale initial value', () => {
        const formGroup = service.createSaleFormGroup(sampleWithNewData);

        const sale = service.getSale(formGroup);

        expect(sale).toMatchObject(sampleWithNewData);
      });

      it('should return NewSale for empty Sale initial value', () => {
        const formGroup = service.createSaleFormGroup();

        const sale = service.getSale(formGroup);

        expect(sale).toMatchObject({});
      });

      it('should return ISale', () => {
        const formGroup = service.createSaleFormGroup(sampleWithRequiredData);

        const sale = service.getSale(formGroup);

        expect(sale).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISale should not enable id FormControl', () => {
        const formGroup = service.createSaleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSale should disable id FormControl', () => {
        const formGroup = service.createSaleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
