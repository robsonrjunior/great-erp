import dayjs from 'dayjs/esm';

import { ISale } from 'app/entities/sale/sale.model';
import { IStockMovement } from 'app/entities/stock-movement/stock-movement.model';

export interface IWarehouse {
  id: number;
  name?: string | null;
  code?: string | null;
  active?: boolean | null;
  deletedAt?: dayjs.Dayjs | null;
  stockMovements?: Pick<IStockMovement, 'id'> | null;
  sales?: Pick<ISale, 'id'> | null;
}

export type NewWarehouse = Omit<IWarehouse, 'id'> & { id: null };
