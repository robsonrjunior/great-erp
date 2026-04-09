import dayjs from 'dayjs/esm';

import { ITenant, NewTenant } from './tenant.model';

export const sampleWithRequiredData: ITenant = {
  id: 32514,
  name: 'for hourly gasto',
  code: 'cérebro visível',
  active: true,
};

export const sampleWithPartialData: ITenant = {
  id: 12978,
  name: 'cruelly blog',
  code: 'finally',
  active: false,
};

export const sampleWithFullData: ITenant = {
  id: 4480,
  name: 'zowie',
  code: 'minus',
  active: true,
  deletedAt: dayjs('2026-04-08T03:21'),
};

export const sampleWithNewData: NewTenant = {
  name: 'zowie',
  code: 'unha brotar yowza',
  active: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
