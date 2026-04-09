import dayjs from 'dayjs/esm';

import { MovementType } from 'app/entities/enumerations/movement-type.model';

export interface IStockMovement {
  id: number;
  movementDate?: dayjs.Dayjs | null;
  movementType?: keyof typeof MovementType | null;
  quantity?: number | null;
  unitCost?: number | null;
  referenceNumber?: string | null;
  notes?: string | null;
  deletedAt?: dayjs.Dayjs | null;
}

export type NewStockMovement = Omit<IStockMovement, 'id'> & { id: null };
