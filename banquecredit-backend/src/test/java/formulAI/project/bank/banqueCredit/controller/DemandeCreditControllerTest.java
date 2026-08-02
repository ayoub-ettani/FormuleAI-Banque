package formulAI.project.bank.banqueCredit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import formulAI.project.bank.banqueCredit.dto.CreateDemandeCreditRequest;
import formulAI.project.bank.banqueCredit.dto.DecisionRequest;
import formulAI.project.bank.banqueCredit.dto.DemandeCreditResponse;
import formulAI.project.bank.banqueCredit.service.DemandeCreditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

class DemandeCreditControllerTest {

    private MockMvc mockMvc;
    private DemandeCreditService demandeCreditService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        demandeCreditService = Mockito.mock(DemandeCreditService.class);
        DemandeCreditController controller = new DemandeCreditController(demandeCreditService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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
}