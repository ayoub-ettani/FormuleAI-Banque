import { Routes } from '@angular/router';
import {DemandeForm} from './pages/demande-form/demande-form';
import {DemandeDetail} from './pages/demande-details/demande-details';
import { DemandeSearchComponent } from './pages/demande-search/demande-search';


export const demandesRoutes: Routes = [
  { path: '', component: DemandeSearchComponent },
  { path: 'nouvelle', component: DemandeForm },
  { path: 'recherche', redirectTo: '', pathMatch: 'full' },
  { path: ':id', component: DemandeDetail }
];

