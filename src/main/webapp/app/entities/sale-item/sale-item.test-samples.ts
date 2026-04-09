import dayjs from 'dayjs/esm';

import { ISaleItem, NewSaleItem } from './sale-item.model';

export const sampleWithRequiredData: ISaleItem = {
  id: 2051,
  quantity: 19224.65,
  unitPrice: 30285.97,
  lineTotal: 25438.97,
};

export const sampleWithPartialData: ISaleItem = {
  id: 24096,
  quantity: 1233.99,
  unitPrice: 22224.98,
  discountAmount: 1464.92,
  lineTotal: 30963.41,
  deletedAt: dayjs('2026-04-08T22:48'),
};

export const sampleWithFullData: ISaleItem = {
  id: 30915,
  quantity: 4218.07,
  unitPrice: 18714.94,
  discountAmount: 7828.73,
  lineTotal: 21399.74,
  deletedAt: dayjs('2026-04-08T23:17'),
};

export const sampleWithNewData: NewSaleItem = {
  quantity: 16774.62,
  unitPrice: 23304.28,
  lineTotal: 7858.38,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
