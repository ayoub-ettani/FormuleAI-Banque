package formulAI.project.bank.banqueCredit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardResponse {
    private long nbSoumises;
    private long nbEnAnalyse;
    private long nbAcceptees;
    private long nbRefusees;
    private double montantTotalDemande;
    private double tauxMoyenEndettement;
}
