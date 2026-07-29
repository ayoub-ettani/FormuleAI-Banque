package formulAI.project.bank.banqueCredit.service;


import formulAI.project.bank.banqueCredit.dto.ClientDTO;
import formulAI.project.bank.banqueCredit.dto.ClientResponse;

import java.util.List;

public interface ClientService {
    List<ClientResponse> getAllClients();
    ClientResponse getClientById(Long id);
    ClientResponse createClient(ClientDTO clientDTO);
    ClientResponse updateClient(Long id, ClientDTO clientDTO);
    void deleteClient(Long id);
}
