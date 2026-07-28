import { Routes } from '@angular/router';
import {LoginComponent} from './features/auth/login/login/login-component/login-component';
import {authGuard} from './core/guards/auth-guard';
import {DashboardComponent} from './features/dashboard/dashboard';
import {ProfileComponent} from './features/profil/profil';
import {LayoutComponent} from './shared/layout/layout.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'profil', component: ProfileComponent }
    ]
  },

  { path: '**', redirectTo: 'login' }
];
