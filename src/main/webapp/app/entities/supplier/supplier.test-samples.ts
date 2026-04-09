import dayjs from 'dayjs/esm';

import { ISupplier, NewSupplier } from './supplier.model';

export const sampleWithRequiredData: ISupplier = {
  id: 22717,
  legalName: 'indiferente',
  taxId: 'alongside below',
  partyType: 'COMPANY',
  active: true,
};

export const sampleWithPartialData: ISupplier = {
  id: 9114,
  legalName: 'oceano',
  taxId: 'cozinheiro papel',
  partyType: 'PERSON',
  phone: '+55 (22) 4147-3387',
  active: false,
};

export const sampleWithFullData: ISupplier = {
  id: 3840,
  legalName: 'cheerfully near',
  tradeName: 'pizza ick refrigerante',
  taxId: 'fecharXXXXX',
  partyType: 'PERSON',
  email: "Rhw{@8-.75'",
  phone: '+55 (26) 6264-0642',
  active: true,
  deletedAt: dayjs('2026-04-08T18:40'),
};

export const sampleWithNewData: NewSupplier = {
  legalName: 'representar',
  taxId: 'arrumar about',
  partyType: 'PERSON',
  active: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
