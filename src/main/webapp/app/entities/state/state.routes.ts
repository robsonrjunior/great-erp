import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import StateResolve from './route/state-routing-resolve.service';

const stateRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/state').then(m => m.State),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/state-detail').then(m => m.StateDetail),
    resolve: {
      state: StateResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/state-update').then(m => m.StateUpdate),
    resolve: {
      state: StateResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/state-update').then(m => m.StateUpdate),
    resolve: {
      state: StateResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default stateRoute;
