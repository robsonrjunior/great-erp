import dayjs from 'dayjs/esm';

import { IWarehouse, NewWarehouse } from './warehouse.model';

export const sampleWithRequiredData: IWarehouse = {
  id: 14084,
  name: 'consciente plástico',
  code: 'duvidar falso',
  active: true,
};

export const sampleWithPartialData: IWarehouse = {
  id: 5564,
  name: 'construir irritably ha',
  code: 'more meh cobra',
  active: false,
};

export const sampleWithFullData: IWarehouse = {
  id: 2422,
  name: 'assistir motorista fogo',
  code: 'apostar',
  active: false,
  deletedAt: dayjs('2026-04-08T20:01'),
};

export const sampleWithNewData: NewWarehouse = {
  name: 'blah mergulhar once',
  code: 'folha',
  active: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
