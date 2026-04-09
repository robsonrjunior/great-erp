import { IState, NewState } from './state.model';

export const sampleWithRequiredData: IState = {
  id: 28012,
  name: 'for knavishly following',
  code: 'duh',
};

export const sampleWithPartialData: IState = {
  id: 24310,
  name: 'óculos consequently below',
  code: 'armário',
};

export const sampleWithFullData: IState = {
  id: 31696,
  name: 'gasto',
  code: 'magro',
};

export const sampleWithNewData: NewState = {
  name: 'into',
  code: 'bravely ti',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
