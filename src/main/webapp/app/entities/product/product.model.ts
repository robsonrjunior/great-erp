import dayjs from 'dayjs/esm';

import { UnitOfMeasure } from 'app/entities/enumerations/unit-of-measure.model';
import { ISaleItem } from 'app/entities/sale-item/sale-item.model';
import { IStockMovement } from 'app/entities/stock-movement/stock-movement.model';

export interface IProduct {
  id: number;
  name?: string | null;
  sku?: string | null;
  unitOfMeasure?: keyof typeof UnitOfMeasure | null;
  unitDecimalPlaces?: number | null;
  salePrice?: number | null;
  costPrice?: number | null;
  minStock?: number | null;
  active?: boolean | null;
  deletedAt?: dayjs.Dayjs | null;
  saleItems?: Pick<ISaleItem, 'id'> | null;
  stockMovements?: Pick<IStockMovement, 'id'> | null;
}

export type NewProduct = Omit<IProduct, 'id'> & { id: null };
