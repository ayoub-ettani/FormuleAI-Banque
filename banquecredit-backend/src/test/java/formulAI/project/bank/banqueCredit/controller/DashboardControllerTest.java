package formulAI.project.bank.banqueCredit.controller;

import formulAI.project.bank.banqueCredit.dto.DashboardResponse;
import formulAI.project.bank.banqueCredit.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DashboardControllerTest {

    private MockMvc mockMvc;
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = Mockito.mock(DashboardService.class);
        DashboardController controller = new DashboardController(dashboardService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getDashboard_retourneLesStatistiques() throws Exception {
        DashboardResponse response = new DashboardResponse(2, 1, 3, 1, 15000.0, 27.5);
        when(dashboardService.getDashboard()).thenReturn(response);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nbSoumises").value(2))
                .andExpect(jsonPath("$.nbAcceptees").value(3))
                .andExpect(jsonPath("$.montantTotalDemande").value(15000.0));
    }
}