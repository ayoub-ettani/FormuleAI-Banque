package formulAI.project.bank.banqueCredit.repository;

import formulAI.project.bank.banqueCredit.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByDeletedFalse();
    Optional<Client> findByIdAndDeletedFalse(Long id);
}
