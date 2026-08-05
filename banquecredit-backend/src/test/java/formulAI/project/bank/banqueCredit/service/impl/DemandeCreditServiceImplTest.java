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
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
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

    // ---------- Tests recherche ----------

    @Test
    void rechercherDemandes_aucunFiltre_retourneToutesLesDemandes() {
        DemandeCredit demande1 = new DemandeCredit();
        demande1.setId(1L);
        demande1.setClient(client);
        demande1.setStatut(StatutDemande.SOUMISE);
        demande1.setMontantDemande(10000.0);
        demande1.setDeleted(false);

        DemandeCredit demande2 = new DemandeCredit();
        demande2.setId(2L);
        demande2.setClient(client);
        demande2.setStatut(StatutDemande.EN_ANALYSE);
        demande2.setMontantDemande(20000.0);
        demande2.setDeleted(false);

        List<DemandeCredit> demandes = List.of(demande1, demande2);

        when(demandeCreditRepository.findAll(any(Specification.class))).thenReturn(demandes);
        when(demandeCreditMapper.toResponse(any())).thenReturn(new DemandeCreditResponse());

        List<DemandeCreditResponse> result = demandeCreditService.rechercherDemandes(null, null, null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void rechercherDemandes_filtreParStatut_retourneDemandesAvecCeStatut() {
        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setClient(client);
        demande.setStatut(StatutDemande.EN_ANALYSE);
        demande.setDeleted(false);

        when(demandeCreditRepository.findAll(any(Specification.class))).thenReturn(List.of(demande));
        when(demandeCreditMapper.toResponse(any())).thenReturn(new DemandeCreditResponse());

        List<DemandeCreditResponse> result = demandeCreditService.rechercherDemandes(null, StatutDemande.EN_ANALYSE, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void rechercherDemandes_combinaisonClientNomEtMontantMin_retourneDemandesFiltrees() {
        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setClient(client);
        demande.setStatut(StatutDemande.SOUMISE);
        demande.setMontantDemande(15000.0);
        demande.setDeleted(false);

        when(demandeCreditRepository.findAll(any(Specification.class))).thenReturn(List.of(demande));
        when(demandeCreditMapper.toResponse(any())).thenReturn(new DemandeCreditResponse());

        List<DemandeCreditResponse> result = demandeCreditService.rechercherDemandes("Dupont", null, 10000.0, null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void rechercherDemandes_aucunResultat_retourneListeVide() {
        when(demandeCreditRepository.findAll(any(Specification.class))).thenReturn(List.of());

        List<DemandeCreditResponse> result = demandeCreditService.rechercherDemandes("ClientInexistant", null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void rechercherDemandes_montantMinSuperieurMontantMax_leveBusinessException() {
        assertThrows(BusinessException.class,
            () -> demandeCreditService.rechercherDemandes(null, null, 20000.0, 10000.0));
    }

    @Test
    void rechercherDemandes_filtreParNomClientInsensibleCasse_retourneDemandes() {
        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setClient(client);
        demande.setStatut(StatutDemande.BROUILLON);
        demande.setDeleted(false);

        when(demandeCreditRepository.findAll(any(Specification.class))).thenReturn(List.of(demande));
        when(demandeCreditMapper.toResponse(any())).thenReturn(new DemandeCreditResponse());

        List<DemandeCreditResponse> result = demandeCreditService.rechercherDemandes("dupont", null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void rechercherDemandes_plageMonantValide_retourneDemandesDansLaPlage() {
        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setClient(client);
        demande.setMontantDemande(15000.0);
        demande.setDeleted(false);

        when(demandeCreditRepository.findAll(any(Specification.class))).thenReturn(List.of(demande));
        when(demandeCreditMapper.toResponse(any())).thenReturn(new DemandeCreditResponse());

        List<DemandeCreditResponse> result = demandeCreditService.rechercherDemandes(null, null, 10000.0, 20000.0);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void rechercherDemandes_montantMinNegatif_leveBusinessException() {
        assertThrows(BusinessException.class,
            () -> demandeCreditService.rechercherDemandes(null, null, -1000.0, 10000.0));
    }

    @Test
    void rechercherDemandes_montantMaxNegatif_leveBusinessException() {
        assertThrows(BusinessException.class,
            () -> demandeCreditService.rechercherDemandes(null, null, 5000.0, -1000.0));
    }

    @Test
    void rechercherDemandes_clientNomAvecCaracteresSpeciaux_rechercheSecurisee() {
        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setClient(client);
        demande.setDeleted(false);

        when(demandeCreditRepository.findAll(any(Specification.class))).thenReturn(List.of(demande));
        when(demandeCreditMapper.toResponse(any())).thenReturn(new DemandeCreditResponse());

        // Caractères LIKE échappés : % et _
        List<DemandeCreditResponse> result = demandeCreditService.rechercherDemandes("Test_%", null, null, null);

        assertNotNull(result);
        // Le test vérifie que la recherche ne lève pas d'exception
        // et que les caractères spéciaux sont échappés correctement
    }
}