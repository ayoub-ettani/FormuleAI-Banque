package formulAI.project.bank.banqueCredit.models;
import formulAI.project.bank.banqueCredit.Enumerations.StatutDemande;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "historique_decisions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDemande ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDemande nouveauStatut;

    @Column(length = 1000)
    private String commentaire;

    @Column(nullable = false)
    private String auteur;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Boolean deleted = false;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_credit_id", nullable = false)
    private DemandeCredit demandeCredit;

}
