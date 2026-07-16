package formulAI.project.bank.banqueCredit.repository;

import formulAI.project.bank.banqueCredit.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
