package formulAI.project.bank.banqueCredit.service;

//import formulAI.project.bank.banqueCredit.dto.ClientDTO;
import formulAI.project.bank.banqueCredit.dto.ClientDTO;
import formulAI.project.bank.banqueCredit.dto.ClientResponse;
import formulAI.project.bank.banqueCredit.exception.ResourceNotFoundException;
import formulAI.project.bank.banqueCredit.mapper.ClientMapper;
import formulAI.project.bank.banqueCredit.model.Client;
import formulAI.project.bank.banqueCredit.model.SituationClient;
import formulAI.project.bank.banqueCredit.repository.ClientRepository;
import formulAI.project.bank.banqueCredit.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    @Mock
    private ClientRepository clientRepo;
    @Mock
    private ClientMapper clientMapper;
    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void returnAllCliets()
    {
        Client client = new Client();
        client.setId(1L);
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setId(1L);

        when(clientRepo.findByDeletedFalse()).thenReturn(List.of(client));

        when(clientMapper.toResponse(client)).thenReturn(clientResponse);
//
        List<ClientResponse> result = clientService.getAllClients();
//
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());

        verify(clientRepo).findByDeletedFalse();
        verify(clientMapper).toResponse(client);
    }
    //test cas où le client existe
    @Test
    void returnClientById()
    {
        Long id= 1L;
        Client client = new Client();
        client.setId(id);
        client.setNom("Ouafae MALKI");

        ClientResponse response = new ClientResponse();
        response.setId(id);
        response.setNom("Ouafae MALKI" );

        when(clientRepo.findByIdAndDeletedFalse(id)).thenReturn(
                Optional.of(client));

        when(clientMapper.toResponse(client)).thenReturn(
                response
        );

        ClientResponse result = clientService.getClientById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Ouafae MALKI", result.getNom());


        verify(clientRepo).findByIdAndDeletedFalse(id);
        verify(clientMapper).toResponse(client);
    }

    //test cas où le client n'existe pas
    @Test
    void throwsExceptionWhenClientNotFound()
    {
        Long id= 1L;
        when(clientRepo.findByIdAndDeletedFalse(id)).thenReturn(
                Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clientService.getClientById(id));

        verify(clientRepo).findByIdAndDeletedFalse(id);

    }

    @Test
    void createClientTest()
    {
        ClientDTO dto= new ClientDTO();
        dto.setNom("Ouafae M");
        dto.setEmail("ouafae.test@gmail.com");
        dto.setRevenuMensuel(10000.0);
        dto.setChargesMensuelles(8000.0);
        dto.setSituationProfessionnelle(SituationClient.CDI);

        Client client = new Client();
        client.setNom("Ouafae M");
        client.setEmail("ouafae.test@gmail.com");
        client.setRevenuMensuel(10000.0);;
        client.setChargesMensuelles(8000.0);;
        client.setSituationProfessionnelle(SituationClient.CDI);

        Client savedClient = new Client();
        savedClient.setId(1L);
        savedClient.setNom("Ouafae M");
        savedClient.setEmail("ouafae.test@gmail.com");
        savedClient.setRevenuMensuel(10000.0);
        savedClient.setChargesMensuelles(8000.0);
        savedClient.setSituationProfessionnelle(SituationClient.CDI);

        ClientResponse response = new ClientResponse();
        response.setId(1L);
        response.setNom("Ouafae M");
        response.setEmail("ouafae.test@gmail.com");
        response.setRevenuMensuel(10000.0);
        response.setChargesMensuelles(8000.0);
        response.setSituationProfessionnelle("CDI");

        when(clientMapper.toEntity(dto)).thenReturn(client);

        when(clientRepo.save(client)).thenReturn(
                savedClient);

        when(clientMapper.toResponse(savedClient)).thenReturn(
                response
        );

        ClientResponse createdClient= clientService.createClient(dto);

        assertNotNull(createdClient);
        assertEquals(1L,createdClient.getId() );
        assertEquals("Ouafae M",createdClient.getNom());
        assertEquals(10000.0,createdClient.getRevenuMensuel());
        assertEquals(8000.0,createdClient.getChargesMensuelles());
        assertEquals("CDI",createdClient.getSituationProfessionnelle());

        verify(clientMapper).toEntity(dto);
        verify(clientRepo).save(client);
        verify(clientMapper).toResponse(savedClient);
    }

    @Test
    void updateClientTest(){

        Long id = 1L;

        ClientDTO dto= new ClientDTO();
        dto.setNom("Ouafae M");
        dto.setEmail("ouafae.test@gmail.com");
        dto.setRevenuMensuel(10000.0);
        dto.setChargesMensuelles(8000.0);
        dto.setSituationProfessionnelle(SituationClient.CDI);

        Client client = new Client();
        client.setId(id);
        client.setNom("Ouafae MALKI");
        client.setEmail("ouafae.malki@gmail.com");


        Client updatedClient= new Client();
        updatedClient.setId(id);
        updatedClient.setNom("Ouafae MALKI");
        updatedClient.setEmail("ouafae.malki@gmail.com");
        updatedClient.setRevenuMensuel(10000.0);
        updatedClient.setChargesMensuelles(8000.0);
        updatedClient.setSituationProfessionnelle(SituationClient.CDI);

        ClientResponse response= new ClientResponse();
        response.setId(id);
        response.setNom("Ouafae MALKI");
        response.setEmail("ouafae.malki@gmail.com");
        response.setRevenuMensuel(10000.0);
        response.setChargesMensuelles(8000.0);
        response.setSituationProfessionnelle("CDI");

        when(clientRepo.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(
                client
        ));

        when(clientRepo.save(client)).thenReturn(updatedClient);

        when(clientMapper.toResponse(updatedClient)).thenReturn(response);

        ClientResponse result = clientService.updateClient(id, dto);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Ouafae MALKI", result.getNom());
        assertEquals("ouafae.malki@gmail.com", result.getEmail());

        verify(clientRepo).findByIdAndDeletedFalse(id);
        verify(clientRepo).save(client);
        verify(clientMapper).toResponse(updatedClient);
    }

}
