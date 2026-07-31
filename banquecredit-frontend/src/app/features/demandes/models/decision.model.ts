export interface DecisionRequest {
  nouveauStatut: 'ACCEPTEE' | 'REFUSEE';
  commentaire?: string;
}
