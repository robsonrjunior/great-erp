import dayjs from 'dayjs/esm';

import { IProduct, NewProduct } from './product.model';

export const sampleWithRequiredData: IProduct = {
  id: 11737,
  name: 'hm sleepily',
  sku: 'yippee whoever',
  unitOfMeasure: 'LITER',
  unitDecimalPlaces: 0,
  salePrice: 30230.54,
  active: false,
};

export const sampleWithPartialData: IProduct = {
  id: 23011,
  name: 'colorido rebater contente',
  sku: 'how',
  unitOfMeasure: 'BOX',
  unitDecimalPlaces: 2,
  salePrice: 23497.41,
  minStock: 14081.32,
  active: true,
};

export const sampleWithFullData: IProduct = {
  id: 4403,
  name: 'brinquedo',
  sku: 'along amongst',
  unitOfMeasure: 'KG',
  unitDecimalPlaces: 0,
  salePrice: 32035.31,
  costPrice: 15010.71,
  minStock: 16387.1,
  active: false,
  deletedAt: dayjs('2026-04-08T09:25'),
};

export const sampleWithNewData: NewProduct = {
  name: 'anti esconder why',
  sku: 'anenst invisível apropos',
  unitOfMeasure: 'KG',
  unitDecimalPlaces: 6,
  salePrice: 22971.06,
  active: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
