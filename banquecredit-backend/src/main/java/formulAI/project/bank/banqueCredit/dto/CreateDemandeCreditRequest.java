package formulAI.project.bank.banqueCredit.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDemandeCreditRequest {

    @NotNull(message = "Le client est obligatoire")
    private Long clientId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "1000.01", message = "Le montant doit être supérieur à 1000")
    @DecimalMax(value = "100000.00", message = "Le montant doit être inférieur ou égal à 100000")
    private Double montantDemande;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 12, message = "La durée minimale est de 12 mois")
    @Max(value = 84, message = "La durée maximale est de 84 mois")
    private Integer dureeMois;

    @NotNull(message = "Le taux fictif est obligatoire")
    private Double tauxFictif;
}