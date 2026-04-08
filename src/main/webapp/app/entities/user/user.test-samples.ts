import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 24814,
  login: 'MariaHelena36',
};

export const sampleWithPartialData: IUser = {
  id: 966,
  login: 'Helio_Saraiva',
};

export const sampleWithFullData: IUser = {
  id: 5440,
  login: 'Kleber.Carvalho20',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
