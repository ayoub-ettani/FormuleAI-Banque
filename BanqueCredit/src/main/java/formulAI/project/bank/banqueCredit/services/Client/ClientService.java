package formulAI.project.bank.banqueCredit.services.Client;

import formulAI.project.bank.banqueCredit.DTO.ClientDTO;

public interface ClientService {
    ClientDTO createClient(ClientDTO clientDTO);
    ClientDTO updateClient(Long id, ClientDTO clientDTO);
    void deleteClient(Long id);
}
