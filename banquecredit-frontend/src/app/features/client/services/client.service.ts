import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { createClientRequest } from '../models/create-client.model';
import { updateClientRequest } from '../models/update-client.model';
import { Client } from '../models/client.model';

@Injectable({
  providedIn: 'root',
})
export class ClientService {
  private readonly api = 'http://localhost:8080/api/clients';
 
  constructor(private http : HttpClient){}
 

  createClient(client : createClientRequest) : Observable<Client>
  {
    return this.http.post<Client>(this.api, client);
  }
 
  getAll() : Observable<Client[]>{
    return this.http.get<Client[]>(this.api);
  }
 
  getById(id : number): Observable<Client>
  {
    return this.http.get<Client>('${this.api}/${id}');
  }

  updateClient(id : number, client : updateClientRequest) : Observable<Client>
  {
    return this.http.put<Client>('${this.api}/${id}', client)
  }
  
}
