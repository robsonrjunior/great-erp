import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import WarehouseResolve from './route/warehouse-routing-resolve.service';

const warehouseRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/warehouse').then(m => m.Warehouse),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/warehouse-detail').then(m => m.WarehouseDetail),
    resolve: {
      warehouse: WarehouseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/warehouse-update').then(m => m.WarehouseUpdate),
    resolve: {
      warehouse: WarehouseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/warehouse-update').then(m => m.WarehouseUpdate),
    resolve: {
      warehouse: WarehouseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default warehouseRoute;
