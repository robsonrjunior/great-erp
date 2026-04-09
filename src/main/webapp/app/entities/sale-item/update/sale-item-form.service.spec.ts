import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../sale-item.test-samples';

import { SaleItemFormService } from './sale-item-form.service';

describe('SaleItem Form Service', () => {
  let service: SaleItemFormService;

  beforeEach(() => {
    service = TestBed.inject(SaleItemFormService);
  });

  describe('Service methods', () => {
    describe('createSaleItemFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSaleItemFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            unitPrice: expect.any(Object),
            discountAmount: expect.any(Object),
            lineTotal: expect.any(Object),
            deletedAt: expect.any(Object),
          }),
        );
      });

      it('passing ISaleItem should create a new form with FormGroup', () => {
        const formGroup = service.createSaleItemFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            unitPrice: expect.any(Object),
            discountAmount: expect.any(Object),
            lineTotal: expect.any(Object),
            deletedAt: expect.any(Object),
          }),
        );
      });
    });

    describe('getSaleItem', () => {
      it('should return NewSaleItem for default SaleItem initial value', () => {
        const formGroup = service.createSaleItemFormGroup(sampleWithNewData);

        const saleItem = service.getSaleItem(formGroup);

        expect(saleItem).toMatchObject(sampleWithNewData);
      });

      it('should return NewSaleItem for empty SaleItem initial value', () => {
        const formGroup = service.createSaleItemFormGroup();

        const saleItem = service.getSaleItem(formGroup);

        expect(saleItem).toMatchObject({});
      });

      it('should return ISaleItem', () => {
        const formGroup = service.createSaleItemFormGroup(sampleWithRequiredData);

        const saleItem = service.getSaleItem(formGroup);

        expect(saleItem).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISaleItem should not enable id FormControl', () => {
        const formGroup = service.createSaleItemFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSaleItem should disable id FormControl', () => {
        const formGroup = service.createSaleItemFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
