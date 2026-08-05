package formulAI.project.bank.banqueCredit.repository;

import formulAI.project.bank.banqueCredit.model.DemandeCredit;
import formulAI.project.bank.banqueCredit.model.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DemandeCreditRepository extends JpaRepository<DemandeCredit, Long>, JpaSpecificationExecutor<DemandeCredit> {

    List<DemandeCredit> findByDeletedFalse();
    Optional<DemandeCredit> findByIdAndDeletedFalse(Long id);
    boolean existsByClientIdAndDeletedFalseAndStatutNotIn(
            Long clientId,
            List<StatutDemande> statuts);

    List<DemandeCredit> findByStatut(StatutDemande statut);
    long countByStatut(StatutDemande statut);
}
