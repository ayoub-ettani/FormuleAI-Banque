package formulAI.project.bank.banqueCredit.service.impl;


import formulAI.project.bank.banqueCredit.dto.DashboardResponse;
import formulAI.project.bank.banqueCredit.model.DemandeCredit;
import formulAI.project.bank.banqueCredit.model.StatutDemande;
import formulAI.project.bank.banqueCredit.repository.DemandeCreditRepository;
import formulAI.project.bank.banqueCredit.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DemandeCreditRepository demandeCreditRepository;

    @Override
    public DashboardResponse getDashboard() {
        long nbSoumises = demandeCreditRepository.countByStatut(StatutDemande.SOUMISE);
        long nbEnAnalyse = demandeCreditRepository.countByStatut(StatutDemande.EN_ANALYSE);
        long nbAcceptees = demandeCreditRepository.countByStatut(StatutDemande.ACCEPTEE);
        long nbRefusees = demandeCreditRepository.countByStatut(StatutDemande.REFUSEE);

        List<DemandeCredit> toutes = demandeCreditRepository.findAll();

        double montantTotal = toutes.stream()
                .filter(d -> d.getStatut() != StatutDemande.BROUILLON && d.getStatut() != StatutDemande.ANNULEE)
                .mapToDouble(DemandeCredit::getMontantDemande)
                .sum();

        double tauxMoyen = toutes.stream()
                .filter(d -> d.getStatut() != StatutDemande.BROUILLON && d.getStatut() != StatutDemande.ANNULEE)
                .mapToDouble(d -> {
                    double revenu = d.getClient().getRevenuMensuel();
                    double charges = d.getClient().getChargesMensuelles();
                    double mensualite = d.getMensualiteEstimee() != null ? d.getMensualiteEstimee() : 0;
                    return revenu > 0 ? (charges + mensualite) / revenu * 100 : 0;
                })
                .average()
                .orElse(0);

        return new DashboardResponse(
                nbSoumises,
                nbEnAnalyse,
                nbAcceptees,
                nbRefusees,
                Math.round(montantTotal * 100.0) / 100.0,
                Math.round(tauxMoyen * 100.0) / 100.0
        );
    }
}
