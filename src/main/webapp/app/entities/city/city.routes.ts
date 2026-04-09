import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CityResolve from './route/city-routing-resolve.service';

const cityRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/city').then(m => m.City),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/city-detail').then(m => m.CityDetail),
    resolve: {
      city: CityResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/city-update').then(m => m.CityUpdate),
    resolve: {
      city: CityResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/city-update').then(m => m.CityUpdate),
    resolve: {
      city: CityResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cityRoute;
