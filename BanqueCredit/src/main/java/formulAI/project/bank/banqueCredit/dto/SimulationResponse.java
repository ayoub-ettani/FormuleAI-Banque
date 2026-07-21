package formulAI.project.bank.banqueCredit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimulationResponse {
    private Double mensualiteEstimee;
    private Double tauxEndettement;
    private Integer scoreSimplifie;
    private Boolean eligibleAcceptationAuto;
}
