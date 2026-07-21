package formulAI.project.bank.banqueCredit.repository;

import formulAI.project.bank.banqueCredit.model.DemandeCredit;
import formulAI.project.bank.banqueCredit.model.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeCreditRepository extends JpaRepository<DemandeCredit, Long> {
    List<DemandeCredit> findByStatut(StatutDemande statut);
    long countByStatut(StatutDemande statut);
}
