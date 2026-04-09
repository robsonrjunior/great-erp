import dayjs from 'dayjs/esm';

import { ICompany } from 'app/entities/company/company.model';
import { ICustomer } from 'app/entities/customer/customer.model';
import { IPerson } from 'app/entities/person/person.model';
import { IProduct } from 'app/entities/product/product.model';
import { IRawMaterial } from 'app/entities/raw-material/raw-material.model';
import { ISale } from 'app/entities/sale/sale.model';
import { ISaleItem } from 'app/entities/sale-item/sale-item.model';
import { IStockMovement } from 'app/entities/stock-movement/stock-movement.model';
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { IWarehouse } from 'app/entities/warehouse/warehouse.model';

export interface ITenant {
  id: number;
  name?: string | null;
  code?: string | null;
  active?: boolean | null;
  deletedAt?: dayjs.Dayjs | null;
  customers?: Pick<ICustomer, 'id'> | null;
  suppliers?: Pick<ISupplier, 'id'> | null;
  people?: Pick<IPerson, 'id'> | null;
  companies?: Pick<ICompany, 'id'> | null;
  products?: Pick<IProduct, 'id'> | null;
  rawMaterials?: Pick<IRawMaterial, 'id'> | null;
  warehouses?: Pick<IWarehouse, 'id'> | null;
  sales?: Pick<ISale, 'id'> | null;
  saleItems?: Pick<ISaleItem, 'id'> | null;
  stockMovements?: Pick<IStockMovement, 'id'> | null;
}

export type NewTenant = Omit<ITenant, 'id'> & { id: null };
