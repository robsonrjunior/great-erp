import dayjs from 'dayjs/esm';

import { ISale, NewSale } from './sale.model';

export const sampleWithRequiredData: ISale = {
  id: 2433,
  saleDate: dayjs('2026-04-08T10:32'),
  saleNumber: 'beijar',
  status: 'CANCELED',
  grossAmount: 15411.41,
  netAmount: 16489.89,
};

export const sampleWithPartialData: ISale = {
  id: 10443,
  saleDate: dayjs('2026-04-08T16:39'),
  saleNumber: 'cérebro properly apropos',
  status: 'CONFIRMED',
  grossAmount: 28272.78,
  netAmount: 7133.94,
  deletedAt: dayjs('2026-04-08T15:34'),
};

export const sampleWithFullData: ISale = {
  id: 29002,
  saleDate: dayjs('2026-04-08T01:19'),
  saleNumber: 'within ha',
  status: 'CONFIRMED',
  grossAmount: 931.95,
  discountAmount: 10137.11,
  netAmount: 15016.16,
  notes: 'so ensinar yuck',
  deletedAt: dayjs('2026-04-08T11:36'),
};

export const sampleWithNewData: NewSale = {
  saleDate: dayjs('2026-04-08T07:49'),
  saleNumber: 'armário sentar',
  status: 'CANCELED',
  grossAmount: 1744.07,
  netAmount: 9899.96,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
