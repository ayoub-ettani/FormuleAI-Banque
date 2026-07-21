package formulAI.project.bank.banqueCredit.service;

import formulAI.project.bank.banqueCredit.DTO.CreateDemandeCreditRequest;
import formulAI.project.bank.banqueCredit.DTO.DemandeCreditResponse;
import formulAI.project.bank.banqueCredit.DTO.UpdateDemandeCreditRequest;
import formulAI.project.bank.banqueCredit.exception.BusinessException;
import formulAI.project.bank.banqueCredit.exception.ResourceNotFoundException;
import formulAI.project.bank.banqueCredit.mapper.DemandeCreditMapper;
import formulAI.project.bank.banqueCredit.model.Client;
import formulAI.project.bank.banqueCredit.model.DemandeCredit;
import formulAI.project.bank.banqueCredit.model.StatutDemande;
import formulAI.project.bank.banqueCredit.repository.ClientRepository;
import formulAI.project.bank.banqueCredit.repository.DemandeCreditRepository;
import formulAI.project.bank.banqueCredit.service.impl.DemandeCreditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemandeCreditServiceTest {

    @Mock
    private DemandeCreditRepository demandeCreditRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private DemandeCreditMapper demandeCreditMapper;

    @InjectMocks
    private DemandeCreditService demandeCreditService;


    @Test
    void shouldCreateDemandeCreditSuccessfully() {

        Client client = new Client();
        client.setId(1L);
        client.setNom("Ayoub");
        client.setRevenuMensuel(5000.0);
        client.setChargesMensuelles(1000.0);

        CreateDemandeCreditRequest request =
                new CreateDemandeCreditRequest(
                        1L,
                        30000.0,
                        48,
                        4.5
                );

        DemandeCredit savedDemande = new DemandeCredit();
        savedDemande.setId(10L);

        DemandeCreditResponse response =
                new DemandeCreditResponse();
        response.setId(10L);

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        when(demandeCreditRepository
                .existsByClientIdAndDeletedFalseAndStatutNotIn(
                        anyLong(),
                        anyList()))
                .thenReturn(false);

        when(demandeCreditRepository.save(any(DemandeCredit.class)))
                .thenReturn(savedDemande);

        when(demandeCreditMapper.toResponse(savedDemande))
                .thenReturn(response);

        DemandeCreditResponse result =
                demandeCreditService.createDemandeCredit(request);

        assertNotNull(result);
        assertEquals(10L, result.getId());

        verify(clientRepository).findById(1L);
        verify(demandeCreditRepository).save(any(DemandeCredit.class));
    }

    @Test
    void shouldThrowExceptionWhenClientNotFound() {

        CreateDemandeCreditRequest request =
                new CreateDemandeCreditRequest(
                        1L,
                        30000.0,
                        48,
                        4.5
                );

        when(clientRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> demandeCreditService.createDemandeCredit(request)
        );

        verify(demandeCreditRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenClientAlreadyHasActiveDemande() {

        Client client = new Client();
        client.setId(1L);

        CreateDemandeCreditRequest request =
                new CreateDemandeCreditRequest(
                        1L,
                        30000.0,
                        48,
                        4.5
                );

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        when(demandeCreditRepository
                .existsByClientIdAndDeletedFalseAndStatutNotIn(
                        anyLong(),
                        anyList()))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> demandeCreditService.createDemandeCredit(request)
        );

        verify(demandeCreditRepository, never())
                .save(any());
    }

    @Test
    void shouldReturnDemandeCreditById() {

        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);

        DemandeCreditResponse response =
                new DemandeCreditResponse();
        response.setId(1L);

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(demande));

        when(demandeCreditMapper.toResponse(demande))
                .thenReturn(response);

        DemandeCreditResponse result =
                demandeCreditService.getDemandeCreditById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenDemandeNotFound() {

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> demandeCreditService.getDemandeCreditById(1L)
        );
    }

    @Test
    void shouldReturnAllDemandes() {

        DemandeCredit demande = new DemandeCredit();

        DemandeCreditResponse response =
                new DemandeCreditResponse();

        when(demandeCreditRepository.findByDeletedFalse())
                .thenReturn(List.of(demande));

        when(demandeCreditMapper.toResponse(demande))
                .thenReturn(response);

        List<DemandeCreditResponse> result =
                demandeCreditService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoDemandes() {

        when(demandeCreditRepository.findByDeletedFalse())
                .thenReturn(List.of());

        List<DemandeCreditResponse> result =
                demandeCreditService.getAll();

        assertTrue(result.isEmpty());
    }


    @Test
    void shouldSoftDeleteDemandeCredit() {

        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setDeleted(false);

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(demande));

        demandeCreditService.deleteDemandeCredit(1L);

        assertTrue(demande.getDeleted());

        verify(demandeCreditRepository)
                .save(demande);
    }

    @Test
    void shouldThrowExceptionWhenDeletingUnknownDemande() {

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> demandeCreditService.deleteDemandeCredit(1L)
        );

        verify(demandeCreditRepository, never())
                .save(any());
    }

    @Test
    void shouldUpdateDemandeCreditSuccessfully() {

        Client client = new Client();
        client.setRevenuMensuel(5000.0);
        client.setChargesMensuelles(1000.0);

        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setClient(client);
        demande.setStatut(StatutDemande.BROUILLON);

        UpdateDemandeCreditRequest request =
                new UpdateDemandeCreditRequest(
                        40000.0,
                        60,
                        5.0
                );

        DemandeCreditResponse response =
                new DemandeCreditResponse();
        response.setId(1L);

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(demande));

        when(demandeCreditRepository.save(any(DemandeCredit.class)))
                .thenReturn(demande);

        when(demandeCreditMapper.toResponse(demande))
                .thenReturn(response);

        DemandeCreditResponse result =
                demandeCreditService.updateDemandeCredit(1L, request);

        assertNotNull(result);

        verify(demandeCreditRepository)
                .save(any(DemandeCredit.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUnknownDemande() {

        UpdateDemandeCreditRequest request =
                new UpdateDemandeCreditRequest(
                        40000.0,
                        60,
                        5.0
                );

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> demandeCreditService.updateDemandeCredit(1L, request)
        );

        verify(demandeCreditRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenDemandeIsNotDraft() {

        Client client = new Client();

        DemandeCredit demande = new DemandeCredit();
        demande.setClient(client);
        demande.setStatut(StatutDemande.SOUMISE);

        UpdateDemandeCreditRequest request =
                new UpdateDemandeCreditRequest(
                        40000.0,
                        60,
                        5.0
                );

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(demande));

        assertThrows(
                BusinessException.class,
                () -> demandeCreditService.updateDemandeCredit(1L, request)
        );

        verify(demandeCreditRepository, never())
                .save(any());
    }

    @Test
    void shouldUpdateFieldsCorrectly() {

        Client client = new Client();
        client.setRevenuMensuel(5000.0);
        client.setChargesMensuelles(1000.0);

        DemandeCredit demande = new DemandeCredit();
        demande.setId(1L);
        demande.setClient(client);
        demande.setStatut(StatutDemande.BROUILLON);

        UpdateDemandeCreditRequest request =
                new UpdateDemandeCreditRequest(
                        45000.0,
                        72,
                        4.8
                );

        when(demandeCreditRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(demande));

        when(demandeCreditRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(demandeCreditMapper.toResponse(any()))
                .thenReturn(new DemandeCreditResponse());

        demandeCreditService.updateDemandeCredit(1L, request);

        assertEquals(45000.0, demande.getMontantDemande());
        assertEquals(72, demande.getDureeMois());
        assertEquals(4.8, demande.getTauxFictif());
    }
}