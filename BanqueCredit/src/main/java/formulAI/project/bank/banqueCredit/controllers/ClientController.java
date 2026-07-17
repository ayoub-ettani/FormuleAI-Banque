package formulAI.project.bank.banqueCredit.controllers;


import formulAI.project.bank.banqueCredit.DTO.ClientDTO;
import formulAI.project.bank.banqueCredit.services.Client.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDTO) {
        ClientDTO savedClient = clientService.createClient(clientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @Valid @RequestBody ClientDTO clientDTO)
    {
        ClientDTO updatedClient =clientService.updateClient(id, clientDTO);
        return ResponseEntity.ok(updatedClient);
    }

}
