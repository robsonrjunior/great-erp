import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import RawMaterialResolve from './route/raw-material-routing-resolve.service';

const rawMaterialRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/raw-material').then(m => m.RawMaterial),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/raw-material-detail').then(m => m.RawMaterialDetail),
    resolve: {
      rawMaterial: RawMaterialResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/raw-material-update').then(m => m.RawMaterialUpdate),
    resolve: {
      rawMaterial: RawMaterialResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/raw-material-update').then(m => m.RawMaterialUpdate),
    resolve: {
      rawMaterial: RawMaterialResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default rawMaterialRoute;
