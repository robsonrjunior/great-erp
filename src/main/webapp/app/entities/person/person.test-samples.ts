import dayjs from 'dayjs/esm';

import { IPerson, NewPerson } from './person.model';

export const sampleWithRequiredData: IPerson = {
  id: 4769,
  fullName: 'reunir absent yowza',
  cpf: 'whoa butXXX',
  active: true,
};

export const sampleWithPartialData: IPerson = {
  id: 24031,
  fullName: 'financiar forte',
  cpf: 'presidirXXX',
  phone: '(12) 6699-7091',
  active: false,
};

export const sampleWithFullData: IPerson = {
  id: 22588,
  fullName: 'accidentally intensely yahoo',
  cpf: 'joshingly pfft',
  birthDate: dayjs('2026-04-08'),
  email: 'QJ@l.5u-$(B',
  phone: '(98) 8268-5335',
  active: true,
  deletedAt: dayjs('2026-04-08T10:40'),
};

export const sampleWithNewData: NewPerson = {
  fullName: 'anti seguro',
  cpf: 'wheneverXXX',
  active: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
