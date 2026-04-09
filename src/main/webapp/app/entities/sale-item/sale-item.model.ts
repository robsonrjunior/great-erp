import dayjs from 'dayjs/esm';

export interface ISaleItem {
  id: number;
  quantity?: number | null;
  unitPrice?: number | null;
  discountAmount?: number | null;
  lineTotal?: number | null;
  deletedAt?: dayjs.Dayjs | null;
}

export type NewSaleItem = Omit<ISaleItem, 'id'> & { id: null };
