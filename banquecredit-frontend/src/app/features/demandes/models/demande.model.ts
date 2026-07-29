export type StatutDemande =
  | 'BROUILLON'
  | 'SOUMISE'
  | 'EN_ANALYSE'
  | 'ACCEPTEE'
  | 'REFUSEE'
  | 'ANNULEE';

export interface DemandeCredit {
  id: number;
  clientId: number;
  clientNom: string;
  montantDemande: number;
  dureeMois: number;
  mensualiteEstimee: number;
  tauxFictif: number;
  statut: StatutDemande;
  scoreSimplifie: number;
  commentaireDecision: string | null;
  dateSoumission: string | null;
  dateDecision: string | null;
}
