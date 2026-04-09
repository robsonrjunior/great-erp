import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import SupplierResolve from './route/supplier-routing-resolve.service';

const supplierRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/supplier').then(m => m.Supplier),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/supplier-detail').then(m => m.SupplierDetail),
    resolve: {
      supplier: SupplierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/supplier-update').then(m => m.SupplierUpdate),
    resolve: {
      supplier: SupplierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/supplier-update').then(m => m.SupplierUpdate),
    resolve: {
      supplier: SupplierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default supplierRoute;
