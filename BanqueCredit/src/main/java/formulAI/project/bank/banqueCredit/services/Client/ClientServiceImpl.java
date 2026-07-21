package formulAI.project.bank.banqueCredit.services.Client;

import formulAI.project.bank.banqueCredit.DTO.ClientDTO;
import formulAI.project.bank.banqueCredit.mappers.ClientMapper;
import formulAI.project.bank.banqueCredit.models.Client;
import formulAI.project.bank.banqueCredit.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private  final ClientMapper clientMapper;

    @Override
    public List<ClientDTO> getAllClients(){
        return clientRepository.findAll()
                .stream().map(clientMapper::toDTO)
                .toList();
    }

    @Override
    public ClientDTO createClient(ClientDTO clientDTO) {
        Client client = clientMapper.toEntity(clientDTO);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toDTO(savedClient);
    }

    @Override
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec l'id : " + id));

        client.setNom(clientDTO.getNom());
        client.setEmail(clientDTO.getEmail());
        client.setRevenuMensuel(clientDTO.getRevenuMensuel());
        client.setChargesMensuelles(clientDTO.getChargesMensuelles());
        client.setSituationProfessionnelle(clientDTO.getSituationProfessionnelle());
        Client updatedClient = clientRepository.save(client);
        return clientMapper.toDTO(updatedClient);

    }

    @Override
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Client introuvable avec l'id : " + id));
        client.setDeleted(true);
        clientRepository.save(client);
    }

}
