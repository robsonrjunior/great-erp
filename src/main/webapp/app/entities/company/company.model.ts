import dayjs from 'dayjs/esm';

export interface ICompany {
  id: number;
  legalName?: string | null;
  tradeName?: string | null;
  cnpj?: string | null;
  stateRegistration?: string | null;
  email?: string | null;
  phone?: string | null;
  active?: boolean | null;
  deletedAt?: dayjs.Dayjs | null;
}

export type NewCompany = Omit<ICompany, 'id'> & { id: null };
