import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { DemandeService } from '../../services/demande.service';
import { SimulationResponse } from '../../models/simulation.model';

import { Client } from '../../../client/models/client.model';
import {ClientService} from '../../../client/services/client.service';

@Component({
  selector: 'app-demande-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './demande-form.html',
  styleUrl: './demande-form.css'
})
export class DemandeForm implements OnInit {

  clients: Client[] = [];
  simulation: SimulationResponse | null = null;
  errorMessage = '';
  isSubmitting = false;

  form;

  constructor(
    private fb: FormBuilder,
    private demandeService: DemandeService,
    private clientService: ClientService,
    private router: Router
  ) {this.form = this.fb.nonNullable.group({
    clientId: [null as unknown as number, Validators.required],
    montantDemande: [null as unknown as number, [Validators.required, Validators.min(1001), Validators.max(100000)]],
    dureeMois: [null as unknown as number, [Validators.required, Validators.min(12), Validators.max(84)]],
    tauxFictif: [5, [Validators.required, Validators.min(0)]]
  });}

  ngOnInit(): void {
    this.clientService.getAll().subscribe({
      next: (data: Client[]) => this.clients = data
    });
  }

  simuler(): void {
    if (this.form.invalid) return;
    this.demandeService.simuler(this.form.getRawValue()).subscribe({
      next: (res) => this.simulation = res,
      error: () => this.errorMessage = 'Impossible de simuler avec ces valeurs'
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.isSubmitting = true;
    this.errorMessage = '';

    this.demandeService.create(this.form.getRawValue()).subscribe({
      next: (demande) => {
        this.isSubmitting = false;
        this.router.navigate(['/demandes', demande.id]);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err?.error?.message || 'Erreur lors de la creation';
      }
    });
  }
}
