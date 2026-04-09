import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import SaleResolve from './route/sale-routing-resolve.service';

const saleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sale').then(m => m.Sale),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/sale-detail').then(m => m.SaleDetail),
    resolve: {
      sale: SaleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sale-update').then(m => m.SaleUpdate),
    resolve: {
      sale: SaleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sale-update').then(m => m.SaleUpdate),
    resolve: {
      sale: SaleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default saleRoute;
