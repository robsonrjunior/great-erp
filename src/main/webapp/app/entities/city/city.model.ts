import { ICompany } from 'app/entities/company/company.model';
import { ICustomer } from 'app/entities/customer/customer.model';
import { IPerson } from 'app/entities/person/person.model';
import { IState } from 'app/entities/state/state.model';
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { IWarehouse } from 'app/entities/warehouse/warehouse.model';

export interface ICity {
  id: number;
  name?: string | null;
  suppliers?: Pick<ISupplier, 'id'> | null;
  customers?: Pick<ICustomer, 'id'> | null;
  people?: Pick<IPerson, 'id'> | null;
  companies?: Pick<ICompany, 'id'> | null;
  warehouses?: Pick<IWarehouse, 'id'> | null;
  state?: Pick<IState, 'id'> | null;
}

export type NewCity = Omit<ICity, 'id'> & { id: null };
