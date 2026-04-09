import dayjs from 'dayjs/esm';

import { SaleStatus } from 'app/entities/enumerations/sale-status.model';
import { ISaleItem } from 'app/entities/sale-item/sale-item.model';

export interface ISale {
  id: number;
  saleDate?: dayjs.Dayjs | null;
  saleNumber?: string | null;
  status?: keyof typeof SaleStatus | null;
  grossAmount?: number | null;
  discountAmount?: number | null;
  netAmount?: number | null;
  notes?: string | null;
  deletedAt?: dayjs.Dayjs | null;
  items?: Pick<ISaleItem, 'id'> | null;
}

export type NewSale = Omit<ISale, 'id'> & { id: null };
