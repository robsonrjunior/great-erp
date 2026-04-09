import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import SaleItemResolve from './route/sale-item-routing-resolve.service';

const saleItemRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sale-item').then(m => m.SaleItem),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/sale-item-detail').then(m => m.SaleItemDetail),
    resolve: {
      saleItem: SaleItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sale-item-update').then(m => m.SaleItemUpdate),
    resolve: {
      saleItem: SaleItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sale-item-update').then(m => m.SaleItemUpdate),
    resolve: {
      saleItem: SaleItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default saleItemRoute;
