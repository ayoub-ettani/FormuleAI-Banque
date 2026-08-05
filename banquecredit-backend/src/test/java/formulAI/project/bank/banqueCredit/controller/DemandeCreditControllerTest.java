package formulAI.project.bank.banqueCredit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import formulAI.project.bank.banqueCredit.dto.CreateDemandeCreditRequest;
import formulAI.project.bank.banqueCredit.dto.DecisionRequest;
import formulAI.project.bank.banqueCredit.dto.DemandeCreditResponse;
import formulAI.project.bank.banqueCredit.exception.BusinessException;
import formulAI.project.bank.banqueCredit.exception.GlobalExceptionHandler;
import formulAI.project.bank.banqueCredit.service.DemandeCreditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DemandeCreditControllerTest {

    private MockMvc mockMvc;
    private DemandeCreditService demandeCreditService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        demandeCreditService = Mockito.mock(DemandeCreditService.class);
        DemandeCreditController controller = new DemandeCreditController(demandeCreditService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Test
    void create_retourne200() throws Exception {
        CreateDemandeCreditRequest request = new CreateDemandeCreditRequest();
        request.setClientId(1L);
        request.setMontantDemande(10000.0);
        request.setDureeMois(24);
        request.setTauxFictif(5.0);

        DemandeCreditResponse response = new DemandeCreditResponse();
        response.setId(1L);
        response.setStatut("BROUILLON");

        when(demandeCreditService.createDemandeCredit(any(), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/demandes")
                        .principal(new UsernamePasswordAuthenticationToken("conseiller", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("BROUILLON"));
    }

    @Test
    void soumettre_retourne200EtStatutSoumise() throws Exception {
        DemandeCreditResponse response = new DemandeCreditResponse();
        response.setId(1L);
        response.setStatut("SOUMISE");

        when(demandeCreditService.soumettre(anyLong(), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/demandes/1/soumettre").principal(new UsernamePasswordAuthenticationToken("conseiller", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("SOUMISE"));
    }

    @Test
    void decision_refusAvecCommentaire_retourne200() throws Exception {
        DecisionRequest decision = new DecisionRequest();
        decision.setNouveauStatut("REFUSEE");
        decision.setCommentaire("Revenu insuffisant");

        DemandeCreditResponse response = new DemandeCreditResponse();
        response.setId(1L);
        response.setStatut("REFUSEE");

        when(demandeCreditService.decider(anyLong(), any(), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/demandes/1/decision")
                        .principal(new UsernamePasswordAuthenticationToken("manager", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("REFUSEE"));
    }

    @Test
    void getHistorique_retourneLaListe() throws Exception {
        when(demandeCreditService.getHistorique(1L)).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/demandes/1/historique"))
                .andExpect(status().isOk());
    }

    // ---------- Tests endpoint recherche ----------

    @Test
    void recherche_sansParametres_retourne200() throws Exception {
        when(demandeCreditService.rechercherDemandes(any(), any(), any(), any()))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/demandes/recherche"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void recherche_avecClientNom_retourne200() throws Exception {
        DemandeCreditResponse response = new DemandeCreditResponse();
        response.setId(1L);
        response.setClientNom("Dupont");

        when(demandeCreditService.rechercherDemandes(anyString(), any(), any(), any()))
                .thenReturn(java.util.List.of(response));

        mockMvc.perform(get("/api/demandes/recherche")
                        .param("clientNom", "Dupont"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientNom").value("Dupont"));
    }

    @Test
    void recherche_avecStatut_retourne200() throws Exception {
        when(demandeCreditService.rechercherDemandes(any(), any(), any(), any()))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/demandes/recherche")
                        .param("statut", "EN_ANALYSE"))
                .andExpect(status().isOk());
    }

    @Test
    void recherche_avecPlageMonant_retourne200() throws Exception {
        when(demandeCreditService.rechercherDemandes(any(), any(), any(), any()))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/demandes/recherche")
                        .param("montantMin", "5000")
                        .param("montantMax", "20000"))
                .andExpect(status().isOk());
    }

    @Test
    void recherche_tousLesParametres_retourne200() throws Exception {
        when(demandeCreditService.rechercherDemandes(any(), any(), any(), any()))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/demandes/recherche")
                        .param("clientNom", "Martin")
                        .param("statut", "SOUMISE")
                        .param("montantMin", "10000")
                        .param("montantMax", "50000"))
                .andExpect(status().isOk());
    }

    @Test
    void recherche_montantMinNegatif_retourne400() throws Exception {
        // Le service lève une BusinessException pour les montants négatifs
        when(demandeCreditService.rechercherDemandes(any(), any(), any(), any()))
                .thenThrow(new BusinessException("Le montant minimum doit être positif ou nul"));

        // Le GlobalExceptionHandler convertit BusinessException en HTTP 400
        mockMvc.perform(get("/api/demandes/recherche")
                        .param("montantMin", "-1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Le montant minimum doit être positif ou nul"));
    }
}