import { ICity, NewCity } from './city.model';

export const sampleWithRequiredData: ICity = {
  id: 3502,
  name: 'respirar gah',
};

export const sampleWithPartialData: ICity = {
  id: 29935,
  name: 'amigo',
};

export const sampleWithFullData: ICity = {
  id: 23718,
  name: 'beneath sincero',
};

export const sampleWithNewData: NewCity = {
  name: 'regarding paredes',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
