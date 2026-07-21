package formulAI.project.bank.banqueCredit.controller;

import formulAI.project.bank.banqueCredit.DTO.CreateDemandeCreditRequest;
import formulAI.project.bank.banqueCredit.DTO.DemandeCreditResponse;
import formulAI.project.bank.banqueCredit.DTO.UpdateDemandeCreditRequest;
import formulAI.project.bank.banqueCredit.service.IDemandeCreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes-credit")
@RequiredArgsConstructor
public class DemandeCreditController {

    private final IDemandeCreditService demandeCreditService;

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
    @PutMapping("/{id}")
    public ResponseEntity<DemandeCreditResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDemandeCreditRequest request) {

        return ResponseEntity.ok(
                demandeCreditService.updateDemandeCredit(
                        id,
                        request)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDemandeCredit(@PathVariable Long id) {

        demandeCreditService.deleteDemandeCredit(id);

        return ResponseEntity.noContent().build();
    }

}
