import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DemandeService } from '../../services/demande.service';
import { DemandeCredit, StatutDemande } from '../../models/demande.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-demande-list',
  standalone: true,
  imports: [CommonModule, RouterLink,FormsModule],
  templateUrl: './demande-list.html',
  styleUrl: './demande-list.css'
})

export class DemandeList implements OnInit {

  demandes: DemandeCredit[] = [];
  filtreStatut: StatutDemande | 'TOUS' = 'TOUS';
  isLoading = true;

  statuts: (StatutDemande | 'TOUS')[] = [
    'TOUS', 'BROUILLON', 'SOUMISE', 'EN_ANALYSE', 'ACCEPTEE', 'REFUSEE', 'ANNULEE'
  ];

  constructor(private demandeService: DemandeService) {}

  ngOnInit(): void {
    this.demandeService.getAll().subscribe({
      next: (data) => {
        this.demandes = data;
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }

  get demandesFiltrees(): DemandeCredit[] {
    if (this.filtreStatut === 'TOUS') return this.demandes;
    return this.demandes.filter(d => d.statut === this.filtreStatut);
  }

  statutClass(statut: StatutDemande): string {
    switch (statut) {
      case 'ACCEPTEE': return 'badge-green';
      case 'REFUSEE': return 'badge-red';
      case 'EN_ANALYSE': return 'badge-orange';
      case 'SOUMISE': return 'badge-orange';
      case 'ANNULEE': return 'badge-gray';
      default: return 'badge-blue';
    }
  }
}
