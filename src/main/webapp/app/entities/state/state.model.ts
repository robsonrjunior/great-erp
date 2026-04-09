import { ICountry } from 'app/entities/country/country.model';

export interface IState {
  id: number;
  name?: string | null;
  code?: string | null;
  country?: Pick<ICountry, 'id'> | null;
}

export type NewState = Omit<IState, 'id'> & { id: null };
