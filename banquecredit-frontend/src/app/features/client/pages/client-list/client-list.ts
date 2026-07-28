import { Component, inject } from '@angular/core';
import { ClientService } from '../../services/client.service';
import { Client } from '../../models/client.model';

@Component({
  selector: 'app-client-list',
  imports: [],
  templateUrl: './client-list.html',
  styleUrl: './client-list.css',
})
export class ClientList {
private readonly clientService = inject(ClientService);
 
  clients : Client[]= [];
 
  ngOnInit(): void{
    this.loadClients();
  }




 
  loadClients(): void {
    this.clientService.getAll().subscribe({
      next: (clients) => { this.clients =  clients;
      },
      error: (err) =>{
        console.error('erreur lors du chargement des données', err);
      }
    })
  }
}
