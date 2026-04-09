import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProduct, NewProduct } from '../product.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProduct for edit and NewProductFormGroupInput for create.
 */
type ProductFormGroupInput = IProduct | PartialWithRequiredKeyOf<NewProduct>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProduct | NewProduct> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

type ProductFormRawValue = FormValueOf<IProduct>;

type NewProductFormRawValue = FormValueOf<NewProduct>;

type ProductFormDefaults = Pick<NewProduct, 'id' | 'active' | 'deletedAt'>;

type ProductFormGroupContent = {
  id: FormControl<ProductFormRawValue['id'] | NewProduct['id']>;
  name: FormControl<ProductFormRawValue['name']>;
  sku: FormControl<ProductFormRawValue['sku']>;
  unitOfMeasure: FormControl<ProductFormRawValue['unitOfMeasure']>;
  unitDecimalPlaces: FormControl<ProductFormRawValue['unitDecimalPlaces']>;
  salePrice: FormControl<ProductFormRawValue['salePrice']>;
  costPrice: FormControl<ProductFormRawValue['costPrice']>;
  minStock: FormControl<ProductFormRawValue['minStock']>;
  active: FormControl<ProductFormRawValue['active']>;
  deletedAt: FormControl<ProductFormRawValue['deletedAt']>;
  saleItems: FormControl<ProductFormRawValue['saleItems']>;
  stockMovements: FormControl<ProductFormRawValue['stockMovements']>;
};

export type ProductFormGroup = FormGroup<ProductFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductFormService {
  createProductFormGroup(product?: ProductFormGroupInput): ProductFormGroup {
    const productRawValue = this.convertProductToProductRawValue({
      ...this.getFormDefaults(),
      ...(product ?? { id: null }),
    });
    return new FormGroup<ProductFormGroupContent>({
      id: new FormControl(
        { value: productRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(productRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(120)],
      }),
      sku: new FormControl(productRawValue.sku, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(40)],
      }),
      unitOfMeasure: new FormControl(productRawValue.unitOfMeasure, {
        validators: [Validators.required],
      }),
      unitDecimalPlaces: new FormControl(productRawValue.unitDecimalPlaces, {
        validators: [Validators.required, Validators.min(0), Validators.max(6)],
      }),
      salePrice: new FormControl(productRawValue.salePrice, {
        validators: [Validators.required, Validators.min(0)],
      }),
      costPrice: new FormControl(productRawValue.costPrice, {
        validators: [Validators.min(0)],
      }),
      minStock: new FormControl(productRawValue.minStock, {
        validators: [Validators.min(0)],
      }),
      active: new FormControl(productRawValue.active, {
        validators: [Validators.required],
      }),
      deletedAt: new FormControl(productRawValue.deletedAt),
      saleItems: new FormControl(productRawValue.saleItems),
      stockMovements: new FormControl(productRawValue.stockMovements),
    });
  }

  getProduct(form: ProductFormGroup): IProduct | NewProduct {
    return this.convertProductRawValueToProduct(form.getRawValue() as ProductFormRawValue | NewProductFormRawValue);
  }

  resetForm(form: ProductFormGroup, product: ProductFormGroupInput): void {
    const productRawValue = this.convertProductToProductRawValue({ ...this.getFormDefaults(), ...product });
    form.reset({
      ...productRawValue,
      id: { value: productRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProductFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      deletedAt: currentTime,
    };
  }

  private convertProductRawValueToProduct(rawProduct: ProductFormRawValue | NewProductFormRawValue): IProduct | NewProduct {
    return {
      ...rawProduct,
      deletedAt: dayjs(rawProduct.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertProductToProductRawValue(
    product: IProduct | (Partial<NewProduct> & ProductFormDefaults),
  ): ProductFormRawValue | PartialWithRequiredKeyOf<NewProductFormRawValue> {
    return {
      ...product,
      deletedAt: product.deletedAt ? product.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
