export interface HistoriqueDecision {
  id: number;
  demandeCreditId: number;
  ancienStatut: string | null;
  nouveauStatut: string;
  commentaire: string | null;
  auteur: string;
  date: string;
}
