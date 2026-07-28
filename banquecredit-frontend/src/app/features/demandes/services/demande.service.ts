import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Demande } from '../models/demande.model';
import { CreateDemandeRequest } from '../models/create-demande.model';

@Injectable({
  providedIn: 'root',
})
export class DemandeService {
  private readonly api = 'http://localhost:8080/api/demande';
  constructor(private http : HttpClient){}

  getAll() : Observable<Demande[]> {
    return this.http.get<Demande[]>(this.api);
  }

  getById(id : number) : Observable<Demande>{
    return this.http.get<Demande>(`${this.api}/${id}`);
  } 

  createDemande(request : CreateDemandeRequest) : Observable<Demande>
  {
    return this.http.post<Demande>(this.api, request);
  }
  
}
