package formulAI.project.bank.banqueCredit.service.impl;

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
import formulAI.project.bank.banqueCredit.service.IDemandeCreditService;
import formulAI.project.bank.banqueCredit.utils.CreditSimulationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandeCreditService implements IDemandeCreditService {

    private final DemandeCreditRepository demandeCreditRepository;
    private final ClientRepository clientRepository;
    private final DemandeCreditMapper demandeCreditMapper;


    @Override
    public DemandeCreditResponse createDemandeCredit(
            CreateDemandeCreditRequest request) {

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Client introuvable"));

        verifyClientHasNoActiveDemande(client.getId());

        double mensualite =
                CreditSimulationUtils.calculMensualite(
                        request.getMontantDemande(),
                        request.getDureeMois(),
                        request.getTauxFictif());

        double tauxEndettement =
                CreditSimulationUtils.calculTauxEndettement(
                        client.getRevenuMensuel(),
                        client.getChargesMensuelles(),
                        mensualite);

        int score;

        if (client.getRevenuMensuel() >= 1500
                && request.getMontantDemande() <= 50000
                && tauxEndettement <= 35) {

            score = 100;

        } else if (tauxEndettement <= 45) {

            score = 60;

        } else {

            score = 20;
        }

        DemandeCredit demandeCredit = new DemandeCredit();

        demandeCredit.setClient(client);
        demandeCredit.setMontantDemande(request.getMontantDemande());
        demandeCredit.setDureeMois(request.getDureeMois());
        demandeCredit.setTauxFictif(request.getTauxFictif());
        demandeCredit.setMensualiteEstimee(mensualite);
        demandeCredit.setScoreSimplifie(score);
        demandeCredit.setStatut(StatutDemande.BROUILLON);
        demandeCredit.setDeleted(false);

        DemandeCredit savedDemande =
                demandeCreditRepository.save(demandeCredit);

        return demandeCreditMapper.toResponse(savedDemande);
    }

    @Override
    public DemandeCreditResponse getDemandeCreditById(Long id) {

        DemandeCredit demandeCredit = demandeCreditRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new RuntimeException("Demande de crédit introuvable"));

        return demandeCreditMapper.toResponse(demandeCredit);
    }

    @Override
    public List<DemandeCreditResponse> getAll() {
        return demandeCreditRepository.findByDeletedFalse()
                .stream()
                .map(demandeCreditMapper::toResponse)
                .toList();
    }

    @Override
    public DemandeCreditResponse updateDemandeCredit(
            Long id,
            UpdateDemandeCreditRequest request) {

        DemandeCredit demandeCredit =
                demandeCreditRepository
                        .findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Demande de crédit introuvable"));

        if (demandeCredit.getStatut() != StatutDemande.BROUILLON) {

            throw new BusinessException(
                    "Seule une demande en statut BROUILLON peut être modifiée");
        }

        double mensualite =
                CreditSimulationUtils.calculMensualite(
                        request.getMontantDemande(),
                        request.getDureeMois(),
                        request.getTauxFictif());

        double tauxEndettement =
                CreditSimulationUtils.calculTauxEndettement(
                        demandeCredit.getClient().getRevenuMensuel(),
                        demandeCredit.getClient().getChargesMensuelles(),
                        mensualite);

        int score = calculScore(
                demandeCredit.getClient().getRevenuMensuel(),
                demandeCredit.getClient().getChargesMensuelles(),
                mensualite,
                request.getMontantDemande());

        demandeCredit.setMontantDemande(
                request.getMontantDemande());

        demandeCredit.setDureeMois(
                request.getDureeMois());

        demandeCredit.setTauxFictif(
                request.getTauxFictif());

        demandeCredit.setMensualiteEstimee(
                mensualite);

        demandeCredit.setScoreSimplifie(
                score);

        DemandeCredit updatedDemande =
                demandeCreditRepository.save(demandeCredit);

        return demandeCreditMapper.toResponse(updatedDemande);
    }

    @Override
    public void deleteDemandeCredit(Long id) {

        DemandeCredit demandeCredit = demandeCreditRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new RuntimeException("Demande de crédit introuvable"));

        demandeCredit.setDeleted(true);

        demandeCreditRepository.save(demandeCredit);
    }

    private Integer calculScore(
            Double revenu,
            Double charges,
            Double mensualite,
            Double montant){

        double taux =
                ((charges + mensualite) / revenu) * 100;

        if(revenu >= 1500
                && montant <= 50000
                && taux <= 35){
            return 100;
        }

        if(taux <= 45){
            return 60;
        }

        return 20;
    }

    private void verifyClientHasNoActiveDemande(Long clientId) {

        boolean hasActiveDemande =
                demandeCreditRepository
                        .existsByClientIdAndDeletedFalseAndStatutNotIn(
                                clientId,
                                List.of(
                                        StatutDemande.ACCEPTEE,
                                        StatutDemande.REFUSEE,
                                        StatutDemande.ANNULEE
                                )
                        );

        if (hasActiveDemande) {
            throw new BusinessException(
                    "Le client possède déjà une demande active");
        }
    }
}