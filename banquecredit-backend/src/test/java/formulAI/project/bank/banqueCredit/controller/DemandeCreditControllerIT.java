package formulAI.project.bank.banqueCredit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import formulAI.project.bank.banqueCredit.dto.*;
import formulAI.project.bank.banqueCredit.model.*;
import formulAI.project.bank.banqueCredit.repository.ClientRepository;
import formulAI.project.bank.banqueCredit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DemandeCreditControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ClientRepository clientRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Long clientId;

    @BeforeEach
    void setUp() {
        // Client de test valide
        Client client = new Client();
        client.setNom("Jean Dupont");
        client.setEmail("jean@test.com");
        client.setRevenuMensuel(3000.0);
        client.setChargesMensuelles(500.0);
        client.setSituationProfessionnelle(SituationClient.CDI);
        clientId = clientRepository.save(client).getId();
    }

    // ---------- RG-BANK-02 : montant ----------

    @Test
    @WithMockUser(username = "conseiller", roles = "USER")
    void create_montantTropBas_retourne400() throws Exception {
        CreateDemandeCreditRequest req = buildRequest(clientId, 500.0, 24, 5.0);

        mockMvc.perform(post("/api/demandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "conseiller", roles = "USER")
    void create_montantTropHaut_retourne400() throws Exception {
        CreateDemandeCreditRequest req = buildRequest(clientId, 150000.0, 24, 5.0);

        mockMvc.perform(post("/api/demandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ---------- RG-BANK-03 : duree ----------

    @Test
    @WithMockUser(username = "conseiller", roles = "USER")
    void create_dureeTropCourte_retourne400() throws Exception {
        CreateDemandeCreditRequest req = buildRequest(clientId, 10000.0, 6, 5.0);

        mockMvc.perform(post("/api/demandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "conseiller", roles = "USER")
    void create_dureeTropLongue_retourne400() throws Exception {
        CreateDemandeCreditRequest req = buildRequest(clientId, 10000.0, 96, 5.0);

        mockMvc.perform(post("/api/demandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ---------- Creation valide ----------

    @Test
    @WithMockUser(username = "conseiller", roles = "USER")
    void create_donneesValides_retourne200EtStatutBrouillon() throws Exception {
        CreateDemandeCreditRequest req = buildRequest(clientId, 10000.0, 24, 5.0);

        mockMvc.perform(post("/api/demandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("BROUILLON"))
                .andExpect(jsonPath("$.mensualiteEstimee").isNumber());
    }

    // ---------- Client avec demande active (regle metier annexe) ----------

    @Test
    @WithMockUser(username = "conseiller", roles = "USER")
    void create_clientADejaUneDemandeActive_retourne400() throws Exception {
        CreateDemandeCreditRequest req = buildRequest(clientId, 10000.0, 24, 5.0);

        mockMvc.perform(post("/api/demandes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        // 2eme demande pour le meme client -> doit echouer
        mockMvc.perform(post("/api/demandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ---------- Workflow complet ----------

    @Test
    @WithMockUser(username = "conseiller", roles = "USER")
    void workflow_soumettreDepuisAutreStatutQueBrouillon_retourne400() throws Exception {
        Long demandeId = creerDemande();

        // 1ere soumission OK
        mockMvc.perform(post("/api/demandes/{id}/soumettre", demandeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("SOUMISE"));

        // 2eme soumission -> doit echouer (deja SOUMISE)
        mockMvc.perform(post("/api/demandes/{id}/soumettre", demandeId))
                .andExpect(status().isBadRequest());
    }

    // ---------- RG-BANK-07 : commentaire obligatoire si refus ----------

    @Test
    @WithMockUser(username = "manager", roles = "MANAGER")
    void decision_refusSansCommentaire_retourne400() throws Exception {
        Long demandeId = creerDemandeEnAnalyse();

        DecisionRequest decision = new DecisionRequest();
        decision.setNouveauStatut("REFUSEE");
        decision.setCommentaire(null); // pas de commentaire

        mockMvc.perform(post("/api/demandes/{id}/decision", demandeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "manager", roles = "MANAGER")
    void decision_refusAvecCommentaire_retourne200() throws Exception {
        Long demandeId = creerDemandeEnAnalyse();

        DecisionRequest decision = new DecisionRequest();
        decision.setNouveauStatut("REFUSEE");
        decision.setCommentaire("Revenu insuffisant");

        mockMvc.perform(post("/api/demandes/{id}/decision", demandeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("REFUSEE"));
    }

    // ---------- Controle des roles ----------

    @Test
    @WithMockUser(username = "conseiller", roles = "USER")
    void decision_avecRoleUser_retourne403() throws Exception {
        Long demandeId = creerDemandeEnAnalyse();

        DecisionRequest decision = new DecisionRequest();
        decision.setNouveauStatut("ACCEPTEE");

        mockMvc.perform(post("/api/demandes/{id}/decision", demandeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isForbidden());
    }

    // ---------- Historique ----------

    @Test
    @WithMockUser(username = "conseiller", roles = "USER")
    void historique_apresSoumission_contientDeuxEntrees() throws Exception {
        Long demandeId = creerDemande();
        mockMvc.perform(post("/api/demandes/{id}/soumettre", demandeId));

        mockMvc.perform(get("/api/demandes/{id}/historique", demandeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)); // creation + soumission
    }

    // ---------- Helpers ----------

    private CreateDemandeCreditRequest buildRequest(Long clientId, Double montant, Integer duree, Double taux) {
        CreateDemandeCreditRequest req = new CreateDemandeCreditRequest();
        req.setClientId(clientId);
        req.setMontantDemande(montant);
        req.setDureeMois(duree);
        req.setTauxFictif(taux);
        return req;
    }

    private Long creerDemande() throws Exception {
        CreateDemandeCreditRequest req = buildRequest(clientId, 10000.0, 24, 5.0);
        String response = mockMvc.perform(post("/api/demandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private Long creerDemandeEnAnalyse() throws Exception {
        Long id = creerDemande();
        mockMvc.perform(post("/api/demandes/{id}/soumettre", id));
        mockMvc.perform(post("/api/demandes/{id}/analyser", id).with(
                org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("manager").roles("MANAGER")
        ));
        return id;
    }
}