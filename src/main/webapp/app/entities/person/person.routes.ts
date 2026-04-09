import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PersonResolve from './route/person-routing-resolve.service';

const personRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/person').then(m => m.Person),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/person-detail').then(m => m.PersonDetail),
    resolve: {
      person: PersonResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/person-update').then(m => m.PersonUpdate),
    resolve: {
      person: PersonResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/person-update').then(m => m.PersonUpdate),
    resolve: {
      person: PersonResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default personRoute;
