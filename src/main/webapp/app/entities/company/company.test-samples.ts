import dayjs from 'dayjs/esm';

import { ICompany, NewCompany } from './company.model';

export const sampleWithRequiredData: ICompany = {
  id: 6800,
  legalName: 'perguntar ah',
  cnpj: 'brr elegerXXXX',
  active: false,
};

export const sampleWithPartialData: ICompany = {
  id: 28110,
  legalName: 'er',
  tradeName: 'midst',
  cnpj: 'when onceXXXXX',
  email: 'BMq@/3./yQovE',
  active: true,
  deletedAt: dayjs('2026-04-08T07:09'),
};

export const sampleWithFullData: ICompany = {
  id: 14111,
  legalName: 'calmo',
  tradeName: 'considering',
  cnpj: 'for janela among',
  stateRegistration: 'once parque',
  email: '/G@A5.R#M/7',
  phone: '(61) 89434-0454',
  active: false,
  deletedAt: dayjs('2026-04-08T04:38'),
};

export const sampleWithNewData: NewCompany = {
  legalName: 'forenenst emprestar',
  cnpj: 'qua pish brinquedo',
  active: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
