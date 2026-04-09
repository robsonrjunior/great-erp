import dayjs from 'dayjs/esm';

import { ICompany } from 'app/entities/company/company.model';
import { PartyType } from 'app/entities/enumerations/party-type.model';
import { IPerson } from 'app/entities/person/person.model';
import { ISale } from 'app/entities/sale/sale.model';

export interface ICustomer {
  id: number;
  legalName?: string | null;
  tradeName?: string | null;
  taxId?: string | null;
  partyType?: keyof typeof PartyType | null;
  email?: string | null;
  phone?: string | null;
  active?: boolean | null;
  deletedAt?: dayjs.Dayjs | null;
  person?: Pick<IPerson, 'id'> | null;
  company?: Pick<ICompany, 'id'> | null;
  sales?: Pick<ISale, 'id'> | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
