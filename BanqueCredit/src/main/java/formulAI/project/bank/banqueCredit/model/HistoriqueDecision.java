package formulAI.project.bank.banqueCredit.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_decisions")
@Data
@NoArgsConstructor
public class HistoriqueDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "demande_credit_id", nullable = false)
    private DemandeCredit demandeCredit;

    private String ancienStatut;

    private String nouveauStatut;

    private String commentaire;

    private String auteur;

    private LocalDateTime date;

}
