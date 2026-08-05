package formulAI.project.bank.banqueCredit.service;



import formulAI.project.bank.banqueCredit.dto.*;
import formulAI.project.bank.banqueCredit.model.StatutDemande;

import java.util.List;

public interface DemandeCreditService {

    DemandeCreditResponse createDemandeCredit(CreateDemandeCreditRequest request, String auteur);
    DemandeCreditResponse getDemandeCreditById(Long id);
    List<DemandeCreditResponse> getAll();
    DemandeCreditResponse updateDemandeCredit(Long id, UpdateDemandeCreditRequest request);
    void deleteDemandeCredit(Long id, String auteur);
    SimulationResponse simuler(CreateDemandeCreditRequest request);
    DemandeCreditResponse soumettre(Long id, String auteur);
    DemandeCreditResponse passerEnAnalyse(Long id, String auteur);
    DemandeCreditResponse decider(Long id, DecisionRequest request, String auteur);
    DemandeCreditResponse annuler(Long id, String auteur);
    List<HistoriqueResponse> getHistorique(Long demandeId);
    List<DemandeCreditResponse> rechercherDemandes(String clientNom, StatutDemande statut, Double montantMin, Double montantMax);


}