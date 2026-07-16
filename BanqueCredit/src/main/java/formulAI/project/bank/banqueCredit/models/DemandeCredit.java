package formulAI.project.bank.banqueCredit.models;

import formulAI.project.bank.banqueCredit.Enumerations.StatutDemande;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "demandes_credit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandeCredit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Min(value = 1001, message = "Le montant doit etre superieur à 1000")
    @Max(value = 100000, message = "Le montant doit etre inferieur ou egal à 100000")
    @Column(nullable = false)
    private Double montantDemande;

    @Min(value = 12, message = "La duree minimale est 12 mois")
    @Max(value = 84, message = "La duree maximale est 84 mois")
    @Column(nullable = false)
    private Integer dureeMois;

    private Double mensualiteEstimee;

    private Double tauxFictif;

    private Integer scoreSimplifie;

    private LocalDateTime dateSoumission;

    private LocalDateTime dateDecision;

    @OneToMany(mappedBy = "demandeCredit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoriqueDecision> historiques;

    @Column(length = 1000)
    private String commentaireDecision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDemande statut = StatutDemande.BROUILLON;

    @Column(nullable = false)
    private Boolean deleted = false;

}
