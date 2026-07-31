import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ClientService } from '../../services/client.service';
import { Client } from '../../models/client.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-client-list',
  imports: [CommonModule, RouterModule],
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

  deleteClient(id: number): void {
    if (!confirm('Voulez-vous vraiment supprimer ce client ?')) {
      return;
    }
    this.clientService.deleteClient(id).subscribe({
      next: () => {
        alert('Client supprimé avec succès');
        this.loadClients(); // Recharger la liste des clients après la suppression
      },
      error: (err) => {
        console.error('Erreur lors de la suppression du client', err);
      }
    });
  }
}
