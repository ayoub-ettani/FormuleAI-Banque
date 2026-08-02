package formulAI.project.bank.banqueCredit.service.impl;

import formulAI.project.bank.banqueCredit.dto.DashboardResponse;
import formulAI.project.bank.banqueCredit.model.Client;
import formulAI.project.bank.banqueCredit.model.DemandeCredit;
import formulAI.project.bank.banqueCredit.model.StatutDemande;
import formulAI.project.bank.banqueCredit.repository.DemandeCreditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private DemandeCreditRepository demandeCreditRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private DemandeCredit buildDemande(StatutDemande statut, double montant, double revenu, double charges, double mensualite) {
        Client client = new Client();
        client.setRevenuMensuel(revenu);
        client.setChargesMensuelles(charges);

        DemandeCredit demande = new DemandeCredit();
        demande.setClient(client);
        demande.setStatut(statut);
        demande.setMontantDemande(montant);
        demande.setMensualiteEstimee(mensualite);
        return demande;
    }

    @Test
    void getDashboard_calculeLesCompteursCorrectement() {
        when(demandeCreditRepository.countByStatut(StatutDemande.SOUMISE)).thenReturn(2L);
        when(demandeCreditRepository.countByStatut(StatutDemande.EN_ANALYSE)).thenReturn(1L);
        when(demandeCreditRepository.countByStatut(StatutDemande.ACCEPTEE)).thenReturn(3L);
        when(demandeCreditRepository.countByStatut(StatutDemande.REFUSEE)).thenReturn(1L);

        when(demandeCreditRepository.findAll()).thenReturn(List.of(
                buildDemande(StatutDemande.ACCEPTEE, 10000, 3000, 500, 400),
                buildDemande(StatutDemande.REFUSEE, 5000, 2000, 300, 200),
                buildDemande(StatutDemande.BROUILLON, 8000, 2500, 400, 300)
        ));

        DashboardResponse result = dashboardService.getDashboard();

        assertEquals(2, result.getNbSoumises());
        assertEquals(1, result.getNbEnAnalyse());
        assertEquals(3, result.getNbAcceptees());
        assertEquals(1, result.getNbRefusees());

        assertEquals(15000.0, result.getMontantTotalDemande());
    }

    @Test
    void getDashboard_aucuneDemande_retourneZeroPartout() {
        when(demandeCreditRepository.countByStatut(StatutDemande.SOUMISE)).thenReturn(0L);
        when(demandeCreditRepository.countByStatut(StatutDemande.EN_ANALYSE)).thenReturn(0L);
        when(demandeCreditRepository.countByStatut(StatutDemande.ACCEPTEE)).thenReturn(0L);
        when(demandeCreditRepository.countByStatut(StatutDemande.REFUSEE)).thenReturn(0L);
        when(demandeCreditRepository.findAll()).thenReturn(List.of());

        DashboardResponse result = dashboardService.getDashboard();

        assertEquals(0, result.getNbSoumises());
        assertEquals(0.0, result.getMontantTotalDemande());
        assertEquals(0.0, result.getTauxMoyenEndettement());
    }

    @Test
    void getDashboard_excludBrouillonEtAnnulee_duCalculDuMontant() {
        when(demandeCreditRepository.countByStatut(StatutDemande.SOUMISE)).thenReturn(0L);
        when(demandeCreditRepository.countByStatut(StatutDemande.EN_ANALYSE)).thenReturn(0L);
        when(demandeCreditRepository.countByStatut(StatutDemande.ACCEPTEE)).thenReturn(0L);
        when(demandeCreditRepository.countByStatut(StatutDemande.REFUSEE)).thenReturn(0L);

        when(demandeCreditRepository.findAll()).thenReturn(List.of(
                buildDemande(StatutDemande.BROUILLON, 100000, 3000, 500, 400),
                buildDemande(StatutDemande.ANNULEE, 100000, 3000, 500, 400)
        ));

        DashboardResponse result = dashboardService.getDashboard();

        assertEquals(0.0, result.getMontantTotalDemande());
    }
}