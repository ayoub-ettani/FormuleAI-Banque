package formulAI.project.bank.banqueCredit.repository;

import formulAI.project.bank.banqueCredit.models.DemandeCredit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeCreditRepository extends JpaRepository<DemandeCredit, Long> {

}
