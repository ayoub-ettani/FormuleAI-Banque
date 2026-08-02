package formulAI.project.bank.banqueCredit.service.impl;

import formulAI.project.bank.banqueCredit.dto.CreateDemandeCreditRequest;
import formulAI.project.bank.banqueCredit.dto.DecisionRequest;
import formulAI.project.bank.banqueCredit.dto.DemandeCreditResponse;
import formulAI.project.bank.banqueCredit.exception.BusinessException;
import formulAI.project.bank.banqueCredit.exception.ResourceNotFoundException;
import formulAI.project.bank.banqueCredit.mapper.DemandeCreditMapper;
import formulAI.project.bank.banqueCredit.model.Client;
import formulAI.project.bank.banqueCredit.model.DemandeCredit;
import formulAI.project.bank.banqueCredit.model.StatutDemande;
import formulAI.project.bank.banqueCredit.repository.ClientRepository;
import formulAI.project.bank.banqueCredit.repository.DemandeCreditRepository;
import formulAI.project.bank.banqueCredit.repository.HistoriqueDecisionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemandeCreditServiceImplTest {

    @Mock private DemandeCreditRepository demandeCreditRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private HistoriqueDecisionRepository historiqueDecisionRepository;
    @Mock private DemandeCreditMapper demandeCreditMapper;

    @InjectMocks
    private DemandeCreditServiceImpl demandeCreditService;

    private Client client;
    private CreateDemandeCreditRequest request;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setNom("Jean Dupont");
        client.setRevenuMensuel(3000.0);
        client.setChargesMensuelles(500.0);

        request = new CreateDemandeCreditRequest();
        request.setClientId(1L);
        request.setMontantDemande(10000.0);
        request.setDureeMois(24);
        request.setTauxFictif(5.0);
    }


    @Test
    void create_montantTropBas_leveBusinessException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(demandeCreditRepository.existsByClientIdAndDeletedFalseAndStatutNotIn(any(), any())).thenReturn(false);

        request.setMontantDemande(500.0);

        assertThrows(BusinessException.class, () -> demandeCreditService.createDemandeCredit(request, "conseiller"));
    }

    @Test
    void create_montantTropHaut_leveBusinessException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(demandeCreditRepository.existsByClientIdAndDeletedFalseAndStatutNotIn(any(), any())).thenReturn(false);

        request.setMontantDemande(150000.0);

        assertThrows(BusinessException.class, () -> demandeCreditService.createDemandeCredit(request, "conseiller"));
    }


    @Test
    void create_dureeTropCourte_leveBusinessException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(demandeCreditRepository.existsByClientIdAndDeletedFalseAndStatutNotIn(any(), any())).thenReturn(false);

        request.setDureeMois(6);

        assertThrows(BusinessException.class, () -> demandeCreditService.createDemandeCredit(request, "conseiller"));
    }

    @Test
    void create_dureeTropLongue_leveBusinessException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(demandeCreditRepository.existsByClientIdAndDeletedFalseAndStatutNotIn(any(), any())).thenReturn(false);

        request.setDureeMois(96);

        assertThrows(BusinessException.class, () -> demandeCreditService.createDemandeCredit(request, "conseiller"));
    }


    @Test
    void create_clientADejaUneDemandeActive_leveBusinessException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(demandeCreditRepository.existsByClientIdAndDeletedFalseAndStatutNotIn(any(), any())).thenReturn(true);

        assertThrows(BusinessException.class, () -> demandeCreditService.createDemandeCredit(request, "conseiller"));
    }

    // ---------- Client introuvable ----------

    @Test
    void create_clientInexistant_leveResourceNotFoundException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> demandeCreditService.createDemandeCredit(request, "conseiller"));
    }


    @Test
    void soumettre_depuisBrouillon_passeEnSoumise() {
        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setClient(client);
        demande.setStatut(StatutDemande.BROUILLON);

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(demande));
        when(demandeCreditRepository.save(any(DemandeCredit.class))).thenReturn(demande);
        when(demandeCreditMapper.toResponse(any())).thenReturn(new DemandeCreditResponse());

        assertDoesNotThrow(() -> demandeCreditService.soumettre(1L, "conseiller"));
        assertEquals(StatutDemande.SOUMISE, demande.getStatut());
    }

    @Test
    void soumettre_depuisAutreStatutQueBrouillon_leveBusinessException() {
        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setStatut(StatutDemande.SOUMISE);

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(demande));

        assertThrows(BusinessException.class, () -> demandeCreditService.soumettre(1L, "conseiller"));
    }


    @Test
    void decider_refusSansCommentaire_leveBusinessException() {
        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setClient(client);
        demande.setStatut(StatutDemande.EN_ANALYSE);

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(demande));

        DecisionRequest decision = new DecisionRequest();
        decision.setNouveauStatut("REFUSEE");
        decision.setCommentaire(null);

        assertThrows(BusinessException.class, () -> demandeCreditService.decider(1L, decision, "manager"));
    }

    @Test
    void decider_refusAvecCommentaire_passeEnRefusee() {
        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setClient(client);
        demande.setStatut(StatutDemande.EN_ANALYSE);

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(demande));
        when(demandeCreditRepository.save(any(DemandeCredit.class))).thenReturn(demande);
        when(demandeCreditMapper.toResponse(any())).thenReturn(new DemandeCreditResponse());

        DecisionRequest decision = new DecisionRequest();
        decision.setNouveauStatut("REFUSEE");
        decision.setCommentaire("Revenu insuffisant");

        assertDoesNotThrow(() -> demandeCreditService.decider(1L, decision, "manager"));
        assertEquals(StatutDemande.REFUSEE, demande.getStatut());
    }

    @Test
    void decider_depuisAutreStatutQueEnAnalyse_leveBusinessException() {
        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setStatut(StatutDemande.BROUILLON);

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(demande));

        DecisionRequest decision = new DecisionRequest();
        decision.setNouveauStatut("ACCEPTEE");

        assertThrows(BusinessException.class, () -> demandeCreditService.decider(1L, decision, "manager"));
    }


    @Test
    void annuler_demandeDejaAcceptee_leveBusinessException() {
        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setStatut(StatutDemande.ACCEPTEE);

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(demande));

        assertThrows(BusinessException.class, () -> demandeCreditService.annuler(1L, "conseiller"));
    }
}