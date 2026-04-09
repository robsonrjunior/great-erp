import dayjs from 'dayjs/esm';

import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 3366,
  legalName: 'reivindicar revisar',
  taxId: 'fechado yuck',
  partyType: 'PERSON',
  active: true,
};

export const sampleWithPartialData: ICustomer = {
  id: 8561,
  legalName: 'estômago',
  taxId: 'águia otimista',
  partyType: 'COMPANY',
  active: false,
};

export const sampleWithFullData: ICustomer = {
  id: 4149,
  legalName: 'where generoso',
  tradeName: 'questionably',
  taxId: 'bloquear pace',
  partyType: 'COMPANY',
  email: '4@ZC(?.4tQ"',
  phone: '(56) 7580-1752',
  active: false,
  deletedAt: dayjs('2026-04-08T08:06'),
};

export const sampleWithNewData: NewCustomer = {
  legalName: 'tijolo inasmuch',
  taxId: 'confianteXX',
  partyType: 'COMPANY',
  active: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
