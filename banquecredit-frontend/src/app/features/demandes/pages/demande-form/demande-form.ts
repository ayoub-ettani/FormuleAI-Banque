import { Component, inject, OnInit } from '@angular/core';
import { Demande } from '../../models/demande.model';
import { DemandeService } from '../../services/demande.service';
import { Router } from '@angular/router';
import { subscribeOn } from 'rxjs';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClientService } from '../../../client/services/client.service';
import { Client } from '../../../client/models/client.model';
import { CreateDemandeRequest } from '../../models/create-demande.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-demande-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './demande-form.html',
  styleUrl: './demande-form.css',
})
export class DemandeForm implements OnInit{

  private fb = inject(FormBuilder);
  private demandeService =inject(DemandeService);
  private clientService = inject(ClientService);
  private router = inject(Router);
  
  clients: Client[] = [];
  
  form = this.fb.group({
    clientId: [0, Validators.required],
    montantDemande : [1001, [Validators.required, Validators.min(1001), Validators.max(100000)]
    ],
    dureeMois:[12, [Validators.required, Validators.min(12), Validators.max(84)] ],
    tauxFictif: [5, [Validators.required]]
  });


  ngOnInit(): void{
    this.clientService.getAll().subscribe({
      next : clients => this.clients = clients
    });
  }

  enregistrer(): void{
    if (this.form.invalid)
    {
      this.form.markAllAsTouched();
      return;
    }
    this.demandeService.createDemande(this.form.value as CreateDemandeRequest).subscribe({
      next: () =>
      {
        alert("demande créée avec succes!");
        this.router.navigate(['/demandes']);
      },
      error: err => console.error(err)
    });
  }

}
