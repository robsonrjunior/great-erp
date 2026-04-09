import dayjs from 'dayjs/esm';

export interface IPerson {
  id: number;
  fullName?: string | null;
  cpf?: string | null;
  birthDate?: dayjs.Dayjs | null;
  email?: string | null;
  phone?: string | null;
  active?: boolean | null;
  deletedAt?: dayjs.Dayjs | null;
}

export type NewPerson = Omit<IPerson, 'id'> & { id: null };
