package formulAI.project.bank.banqueCredit.services.Client;

import formulAI.project.bank.banqueCredit.DTO.ClientDTO;

import java.util.List;

public interface ClientService {
    List<ClientDTO> getAllClients();
    ClientDTO createClient(ClientDTO clientDTO);
    ClientDTO updateClient(Long id, ClientDTO clientDTO);
    void deleteClient(Long id);
}
