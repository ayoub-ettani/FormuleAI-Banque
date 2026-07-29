import { Routes } from '@angular/router';

import { LoginComponent } from './features/auth/login/login/login-component/login-component';
import { LayoutComponent } from './shared/layout/layout.component';
import { authGuard } from './core/guards/auth-guard';
import { DashboardComponent } from './features/dashboard/dashboard';
import { ProfileComponent } from './features/profil/profil';

import { demandesRoutes } from './features/demandes/demandes.routes';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

      { path: 'dashboard', component: DashboardComponent },

      // Clients
      {
        path: 'clients',
        loadComponent: () =>
          import('./features/client/pages/client-list/client-list')
            .then(m => m.ClientList)
      },
      {
        path: 'clients/new',
        loadComponent: () =>
          import('./features/client/pages/client-form/client-form')
            .then(m => m.ClientForm)
      },
      {
        path: 'clients/update/:id',
        loadComponent: () =>
          import('./features/client/pages/client-update-data/client-update-data')
            .then(m => m.ClientUpdateData)
      },

      // Demandes
      {
        path: 'demandes',
        children: demandesRoutes
      },

      { path: 'profil', component: ProfileComponent }
    ]
  },

  { path: '**', redirectTo: 'login' }
];
