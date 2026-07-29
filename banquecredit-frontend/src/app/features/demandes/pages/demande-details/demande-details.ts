import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DemandeService } from '../../services/demande.service';
import { DemandeCredit } from '../../models/demande.model';
import { HistoriqueDecision } from '../../models/historique.model';
import {AuthService} from '../../../../core/services/auth-service';

@Component({
  selector: 'app-demande-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './demande-details.html',
  styleUrl: './demande-details.css'
})
export class DemandeDetail implements OnInit {

  demande: DemandeCredit | null = null;
  historique: HistoriqueDecision[] = [];
  isLoading = true;
  errorMessage = '';

  decisionForm ;

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private demandeService: DemandeService,
    public authService: AuthService
  ) { this.decisionForm= this.fb.nonNullable.group({
    nouveauStatut: ['ACCEPTEE' as 'ACCEPTEE' | 'REFUSEE', Validators.required],
    commentaire: ['']
  })}

  get isManager(): boolean {
    return this.authService.hasRole('ROLE_MANAGER');
  }

  ngOnInit(): void {
    this.charger();
  }

  private charger(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.isLoading = true;

    this.demandeService.getById(id).subscribe({
      next: (d) => { this.demande = d; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });

    this.demandeService.getHistorique(id).subscribe({
      next: (h) => this.historique = h
    });
  }

  soumettre(): void {
    if (!this.demande) return;
    this.demandeService.soumettre(this.demande.id).subscribe({
      next: () => this.charger(),
      error: (err) => this.errorMessage = err?.error?.message || 'Erreur'
    });
  }

  passerEnAnalyse(): void {
    if (!this.demande) return;
    this.demandeService.passerEnAnalyse(this.demande.id).subscribe({
      next: () => this.charger(),
      error: (err) => this.errorMessage = err?.error?.message || 'Erreur'
    });
  }

  decider(): void {
    if (!this.demande || this.decisionForm.invalid) return;
    this.errorMessage = '';

    this.demandeService.decider(this.demande.id, this.decisionForm.getRawValue()).subscribe({
      next: () => this.charger(),
      error: (err) => this.errorMessage = err?.error?.message || 'Erreur lors de la decision'
    });
  }

  annuler(): void {
    if (!this.demande) return;
    this.demandeService.annuler(this.demande.id).subscribe({
      next: () => this.charger(),
      error: (err) => this.errorMessage = err?.error?.message || 'Erreur'
    });
  }
}
