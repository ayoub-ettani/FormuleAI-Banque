export interface Demande{
    id : number;
    clientId : number;
    clientNom : string;
    montantDemande : number;
    dureeMois : number;
    mensualiteEstimee : number;
    tauxFictif : number;
    statut : string;
    scoreSimplifie : number;
    commentaireDecision : string;
    dateSoumission : string;
    dateDecision : string | null;
}