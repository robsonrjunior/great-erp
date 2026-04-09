import dayjs from 'dayjs/esm';

import { UnitOfMeasure } from 'app/entities/enumerations/unit-of-measure.model';
import { IStockMovement } from 'app/entities/stock-movement/stock-movement.model';

export interface IRawMaterial {
  id: number;
  name?: string | null;
  sku?: string | null;
  unitOfMeasure?: keyof typeof UnitOfMeasure | null;
  unitDecimalPlaces?: number | null;
  unitCost?: number | null;
  minStock?: number | null;
  active?: boolean | null;
  deletedAt?: dayjs.Dayjs | null;
  stockMovements?: Pick<IStockMovement, 'id'> | null;
}

export type NewRawMaterial = Omit<IRawMaterial, 'id'> & { id: null };
