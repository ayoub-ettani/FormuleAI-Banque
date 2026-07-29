import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DemandeCredit } from '../models/demande.model';
import { CreateDemandeRequest } from '../models/create-demande.model';
import { UpdateDemandeRequest } from '../models/update-demande.model';
import { DecisionRequest } from '../models/decision.model';
import { HistoriqueDecision } from '../models/historique.model';
import { SimulationResponse } from '../models/simulation.model';
import {environment} from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DemandeService {

  private baseUrl = `${environment.apiUrl}/demandes`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<DemandeCredit[]> {
    return this.http.get<DemandeCredit[]>(this.baseUrl);
  }

  getById(id: number): Observable<DemandeCredit> {
    return this.http.get<DemandeCredit>(`${this.baseUrl}/${id}`);
  }

  create(request: CreateDemandeRequest): Observable<DemandeCredit> {
    return this.http.post<DemandeCredit>(this.baseUrl, request);
  }

  update(id: number, request: UpdateDemandeRequest): Observable<DemandeCredit> {
    return this.http.put<DemandeCredit>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  simuler(request: CreateDemandeRequest): Observable<SimulationResponse> {
    return this.http.post<SimulationResponse>(`${this.baseUrl}/simulation`, request);
  }

  soumettre(id: number): Observable<DemandeCredit> {
    return this.http.post<DemandeCredit>(`${this.baseUrl}/${id}/soumettre`, {});
  }

  passerEnAnalyse(id: number): Observable<DemandeCredit> {
    return this.http.post<DemandeCredit>(`${this.baseUrl}/${id}/analyser`, {});
  }

  decider(id: number, request: DecisionRequest): Observable<DemandeCredit> {
    return this.http.post<DemandeCredit>(`${this.baseUrl}/${id}/decision`, request);
  }

  annuler(id: number): Observable<DemandeCredit> {
    return this.http.post<DemandeCredit>(`${this.baseUrl}/${id}/annuler`, {});
  }

  getHistorique(id: number): Observable<HistoriqueDecision[]> {
    return this.http.get<HistoriqueDecision[]>(`${this.baseUrl}/${id}/historique`);
  }
}
