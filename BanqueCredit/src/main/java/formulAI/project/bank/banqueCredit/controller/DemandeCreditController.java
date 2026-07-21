package formulAI.project.bank.banqueCredit.controller;

import formulAI.project.bank.banqueCredit.dto.*;
import formulAI.project.bank.banqueCredit.service.DemandeCreditService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
public class DemandeCreditController {

    private final DemandeCreditService demandeCreditService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<DemandeCreditResponse> create(
            @Valid @RequestBody CreateDemandeCreditRequest request,
            @Parameter(hidden = true) Authentication authentication) {
        return ResponseEntity.ok(demandeCreditService.createDemandeCredit(request, authentication.getName()));
    }

    @PostMapping("/simulation")
    public ResponseEntity<SimulationResponse> simuler(@Valid @RequestBody CreateDemandeCreditRequest request) {
        return ResponseEntity.ok(demandeCreditService.simuler(request));
    }

    @GetMapping
    public ResponseEntity<List<DemandeCreditResponse>> getAll() {
        return ResponseEntity.ok(demandeCreditService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandeCreditResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(demandeCreditService.getDemandeCreditById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<DemandeCreditResponse> update(
            @PathVariable Long id, @Valid @RequestBody UpdateDemandeCreditRequest request) {
        return ResponseEntity.ok(demandeCreditService.updateDemandeCredit(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id, @Parameter(hidden = true) Authentication authentication) {
        demandeCreditService.deleteDemandeCredit(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/historique")
    public ResponseEntity<List<HistoriqueResponse>> getHistorique(@PathVariable Long id) {
        return ResponseEntity.ok(demandeCreditService.getHistorique(id));
    }

    @PostMapping("/{id}/soumettre")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<DemandeCreditResponse> soumettre(
            @PathVariable Long id, @Parameter(hidden = true) Authentication authentication) {
        return ResponseEntity.ok(demandeCreditService.soumettre(id, authentication.getName()));
    }

    @PostMapping("/{id}/analyser")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<DemandeCreditResponse> analyser(
            @PathVariable Long id, @Parameter(hidden = true) Authentication authentication) {
        return ResponseEntity.ok(demandeCreditService.passerEnAnalyse(id, authentication.getName()));
    }

    @PostMapping("/{id}/decision")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<DemandeCreditResponse> decider(
            @PathVariable Long id, @Valid @RequestBody DecisionRequest request,
            @Parameter(hidden = true) Authentication authentication) {
        return ResponseEntity.ok(demandeCreditService.decider(id, request, authentication.getName()));
    }

    @PostMapping("/{id}/annuler")
    public ResponseEntity<DemandeCreditResponse> annuler(
            @PathVariable Long id, @Parameter(hidden = true) Authentication authentication) {
        return ResponseEntity.ok(demandeCreditService.annuler(id, authentication.getName()));
    }
}