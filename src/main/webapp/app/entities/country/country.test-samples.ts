import { ICountry, NewCountry } from './country.model';

export const sampleWithRequiredData: ICountry = {
  id: 30729,
  name: 'claro since rígido',
  isoCode: 'opa',
};

export const sampleWithPartialData: ICountry = {
  id: 14020,
  name: 'boohoo when',
  isoCode: 'tow',
};

export const sampleWithFullData: ICountry = {
  id: 28151,
  name: 'fooey fooey carro',
  isoCode: 'lâm',
};

export const sampleWithNewData: NewCountry = {
  name: 'abaft famously what',
  isoCode: 'des',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
