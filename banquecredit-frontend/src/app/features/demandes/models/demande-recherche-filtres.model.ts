import { StatutDemande } from './demande.model';

export interface DemandeRechercheFiltres {
  clientNom?: string;
  statut?: StatutDemande;
  montantMin?: number;
  montantMax?: number;
}

