package formulAI.project.bank.banqueCredit.dto;

import formulAI.project.bank.banqueCredit.model.StatutDemande;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDemandeCreditRequest {

    private String nomClient;  // recherche partielle insensible à la casse

    private StatutDemande statut;

    @DecimalMin(value = "0.0", message = "Le montant minimum doit être positif")
    private Double montantMin;

    @DecimalMin(value = "0.0", message = "Le montant maximum doit être positif")
    private Double montantMax;

    // Validation personnalisée : montantMin <= montantMax
    @AssertTrue(message = "Le montant minimum ne peut pas être supérieur au montant maximum")
    public boolean isMontantRangeValid() {
        if (montantMin != null && montantMax != null) {
            return montantMin <= montantMax;
        }
        return true;
    }
}

