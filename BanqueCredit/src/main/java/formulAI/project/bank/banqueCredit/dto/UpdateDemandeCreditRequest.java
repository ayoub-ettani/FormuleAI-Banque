package formulAI.project.bank.banqueCredit.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDemandeCreditRequest {

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "1000.01")
    @DecimalMax(value = "100000.00")
    private Double montantDemande;

    @NotNull(message = "La durée est obligatoire")
    @Min(12)
    @Max(84)
    private Integer dureeMois;

    @NotNull(message = "Le taux fictif est obligatoire")
    private Double tauxFictif;
}