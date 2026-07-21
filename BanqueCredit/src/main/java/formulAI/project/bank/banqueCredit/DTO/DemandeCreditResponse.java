package formulAI.project.bank.banqueCredit.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeCreditResponse {

    private Long id;

    private Long clientId;

    private String clientNom;

    private Double montantDemande;

    private Integer dureeMois;

    private Double mensualiteEstimee;

    private Double tauxFictif;

    private String statut;

    private Integer scoreSimplifie;

    private String commentaireDecision;

    private LocalDateTime dateSoumission;

    private LocalDateTime dateDecision;
}