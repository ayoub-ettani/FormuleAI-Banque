import { Routes } from '@angular/router';
import {LoginComponent} from './features/auth/login/login/login-component/login-component';
import {authGuard} from './core/guards/auth-guard';
import {DashboardComponent} from './features/dashboard/dashboard';
import {ProfileComponent} from './features/profil/profil';
import {LayoutComponent} from './shared/layout/layout.component';




export const routes: Routes = [

    //clients
    {
        path: 'clients',
        loadComponent: () => import('./features/client/pages/client-list/client-list').then(m => m.ClientList)
    },
    {
        path: 'clients/new',
        loadComponent: () => import('./features/client/pages/client-form/client-form').then(m => m.ClientForm)
    },
    {
        path: 'clients/update/:id',
        loadComponent: () => import('./features/client/pages/client-update-data/client-update-data').then(m => m.ClientUpdateData)
    },

    //demandes
    {
        path: 'demandes',
        loadComponent: () => import('./features/demandes/pages/demande-list/demande-list').then(m => m.DemandeList)
    },
    {
        path: 'demandes/new',
        loadComponent: () => import('./features/demandes/pages/demande-form/demande-form').then(m => m.DemandeForm)
    },
    {
        path: 'demandes/details/:id',
        loadComponent: () => import('./features/demandes/pages/demande-details/demande-details').then(m => m.DemandeDetails)
    }

];
