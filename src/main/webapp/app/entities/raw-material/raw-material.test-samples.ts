import dayjs from 'dayjs/esm';

import { IRawMaterial, NewRawMaterial } from './raw-material.model';

export const sampleWithRequiredData: IRawMaterial = {
  id: 2063,
  name: 'whoa',
  sku: 'cobra forte crossly',
  unitOfMeasure: 'UNIT',
  unitDecimalPlaces: 6,
  active: false,
};

export const sampleWithPartialData: IRawMaterial = {
  id: 6916,
  name: 'otimista',
  sku: 'engenheiro',
  unitOfMeasure: 'BOX',
  unitDecimalPlaces: 2,
  unitCost: 4551.64,
  minStock: 23545.89,
  active: true,
};

export const sampleWithFullData: IRawMaterial = {
  id: 1733,
  name: 'complexo wherever',
  sku: 'mechanically patiently neblina',
  unitOfMeasure: 'METER',
  unitDecimalPlaces: 6,
  unitCost: 29997.98,
  minStock: 18798.34,
  active: false,
  deletedAt: dayjs('2026-04-08T14:17'),
};

export const sampleWithNewData: NewRawMaterial = {
  name: 'majestically er lei',
  sku: 'arroz',
  unitOfMeasure: 'BOX',
  unitDecimalPlaces: 1,
  active: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
