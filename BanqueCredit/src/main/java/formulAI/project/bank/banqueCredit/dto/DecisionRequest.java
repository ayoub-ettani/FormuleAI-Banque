package formulAI.project.bank.banqueCredit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DecisionRequest {

    @NotBlank(message = "Le nouveau statut est obligatoire (ACCEPTEE ou REFUSEE)")
    private String nouveauStatut;

    private String commentaire;
}
