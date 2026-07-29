import { Routes } from '@angular/router';
import {DemandeList} from './pages/demande-list/demande-list';
import {DemandeForm} from './pages/demande-form/demande-form';
import {DemandeDetail} from './pages/demande-details/demande-details';


export const demandesRoutes: Routes = [
  { path: '', component: DemandeList },
  { path: 'nouvelle', component: DemandeForm },
  { path: ':id', component: DemandeDetail }
];

