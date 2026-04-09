import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TenantResolve from './route/tenant-routing-resolve.service';

const tenantRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/tenant').then(m => m.Tenant),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/tenant-detail').then(m => m.TenantDetail),
    resolve: {
      tenant: TenantResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/tenant-update').then(m => m.TenantUpdate),
    resolve: {
      tenant: TenantResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/tenant-update').then(m => m.TenantUpdate),
    resolve: {
      tenant: TenantResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default tenantRoute;
