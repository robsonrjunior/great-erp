import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../tenant.test-samples';

import { TenantFormService } from './tenant-form.service';

describe('Tenant Form Service', () => {
  let service: TenantFormService;

  beforeEach(() => {
    service = TestBed.inject(TenantFormService);
  });

  describe('Service methods', () => {
    describe('createTenantFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTenantFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
            active: expect.any(Object),
            deletedAt: expect.any(Object),
            customers: expect.any(Object),
            suppliers: expect.any(Object),
            people: expect.any(Object),
            companies: expect.any(Object),
            products: expect.any(Object),
            rawMaterials: expect.any(Object),
            warehouses: expect.any(Object),
            sales: expect.any(Object),
            saleItems: expect.any(Object),
            stockMovements: expect.any(Object),
          }),
        );
      });

      it('passing ITenant should create a new form with FormGroup', () => {
        const formGroup = service.createTenantFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
            active: expect.any(Object),
            deletedAt: expect.any(Object),
            customers: expect.any(Object),
            suppliers: expect.any(Object),
            people: expect.any(Object),
            companies: expect.any(Object),
            products: expect.any(Object),
            rawMaterials: expect.any(Object),
            warehouses: expect.any(Object),
            sales: expect.any(Object),
            saleItems: expect.any(Object),
            stockMovements: expect.any(Object),
          }),
        );
      });
    });

    describe('getTenant', () => {
      it('should return NewTenant for default Tenant initial value', () => {
        const formGroup = service.createTenantFormGroup(sampleWithNewData);

        const tenant = service.getTenant(formGroup);

        expect(tenant).toMatchObject(sampleWithNewData);
      });

      it('should return NewTenant for empty Tenant initial value', () => {
        const formGroup = service.createTenantFormGroup();

        const tenant = service.getTenant(formGroup);

        expect(tenant).toMatchObject({});
      });

      it('should return ITenant', () => {
        const formGroup = service.createTenantFormGroup(sampleWithRequiredData);

        const tenant = service.getTenant(formGroup);

        expect(tenant).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITenant should not enable id FormControl', () => {
        const formGroup = service.createTenantFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTenant should disable id FormControl', () => {
        const formGroup = service.createTenantFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
