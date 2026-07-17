package formulAI.project.bank.banqueCredit.controller;

import formulAI.project.bank.banqueCredit.DTO.CreateDemandeCreditRequest;
import formulAI.project.bank.banqueCredit.DTO.DemandeCreditResponse;
import formulAI.project.bank.banqueCredit.service.impl.DemandeCreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes-credit")
@RequiredArgsConstructor
public class DemandeCreditController {

    private final DemandeCreditService demandeCreditService;

    @GetMapping
    public ResponseEntity<List<DemandeCreditResponse>> getAll(){
        return ResponseEntity.ok(demandeCreditService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandeCreditResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                demandeCreditService.getDemandeCreditById(id)
        );
    }

    @PostMapping
    public ResponseEntity<DemandeCreditResponse> create(
            @Valid @RequestBody CreateDemandeCreditRequest request){

        return ResponseEntity.ok(
                demandeCreditService.createDemandeCredit(request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDemandeCredit(@PathVariable Long id) {

        demandeCreditService.deleteDemandeCredit(id);

        return ResponseEntity.noContent().build();
    }

}
