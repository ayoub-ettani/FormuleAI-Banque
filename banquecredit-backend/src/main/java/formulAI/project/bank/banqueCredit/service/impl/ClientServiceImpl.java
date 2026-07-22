package formulAI.project.bank.banqueCredit.service.impl;

import formulAI.project.bank.banqueCredit.dto.ClientDTO;
import formulAI.project.bank.banqueCredit.exception.ResourceNotFoundException;
import formulAI.project.bank.banqueCredit.mapper.ClientMapper;
import formulAI.project.bank.banqueCredit.model.Client;
import formulAI.project.bank.banqueCredit.repository.ClientRepository;
import formulAI.project.bank.banqueCredit.service.ClientService;
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
        return clientRepository.findByDeletedFalse()
                .stream().map(clientMapper::toDTO)
                .toList();
    }

    @Override
    public ClientDTO getClientById(Long id){
        return clientRepository.findByIdAndDeletedFalse(id)
                .map(clientMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id : " + id));
    }

    @Override
    public ClientDTO createClient(ClientDTO clientDTO) {
        Client client = clientMapper.toEntity(clientDTO);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toDTO(savedClient);
    }

    @Override
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client client = clientRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id : " + id));

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
        Client client = clientRepository.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new ResourceNotFoundException("Client introuvable avec l'id : " + id));
        client.setDeleted(true);
        clientRepository.save(client);
    }

}
