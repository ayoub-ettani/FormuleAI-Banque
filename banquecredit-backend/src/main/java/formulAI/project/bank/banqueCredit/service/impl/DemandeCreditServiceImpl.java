package formulAI.project.bank.banqueCredit.service.impl;

import formulAI.project.bank.banqueCredit.dto.*;
import formulAI.project.bank.banqueCredit.exception.BusinessException;
import formulAI.project.bank.banqueCredit.exception.ResourceNotFoundException;
import formulAI.project.bank.banqueCredit.mapper.DemandeCreditMapper;
import formulAI.project.bank.banqueCredit.model.*;
import formulAI.project.bank.banqueCredit.repository.ClientRepository;
import formulAI.project.bank.banqueCredit.repository.DemandeCreditRepository;
import formulAI.project.bank.banqueCredit.repository.HistoriqueDecisionRepository;
import formulAI.project.bank.banqueCredit.service.DemandeCreditService;
import formulAI.project.bank.banqueCredit.utils.CreditSimulationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandeCreditServiceImpl implements DemandeCreditService {

    private final DemandeCreditRepository demandeCreditRepository;
    private final ClientRepository clientRepository;
    private final HistoriqueDecisionRepository historiqueDecisionRepository;
    private final DemandeCreditMapper demandeCreditMapper;

    // ---------- CRUD ----------

    @Override
    public DemandeCreditResponse createDemandeCredit(CreateDemandeCreditRequest request, String auteur) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

        verifyClientHasNoActiveDemande(client.getId());
        validerMontant(request.getMontantDemande());
        validerDuree(request.getDureeMois());

        double mensualite = CreditSimulationUtils.calculMensualite(
                request.getMontantDemande(), request.getDureeMois(), request.getTauxFictif());
        double tauxEndettement = CreditSimulationUtils.calculTauxEndettement(
                client.getRevenuMensuel(), client.getChargesMensuelles(), mensualite);
        int score = calculScore(client.getRevenuMensuel(), tauxEndettement, request.getMontantDemande());

        DemandeCredit demande = new DemandeCredit();
        demande.setClient(client);
        demande.setMontantDemande(request.getMontantDemande());
        demande.setDureeMois(request.getDureeMois());
        demande.setTauxFictif(request.getTauxFictif());
        demande.setMensualiteEstimee(mensualite);
        demande.setScoreSimplifie(score);
        demande.setStatut(StatutDemande.BROUILLON);
        demande.setDeleted(false);

        DemandeCredit saved = demandeCreditRepository.save(demande);
        historiser(saved, null, StatutDemande.BROUILLON, "Creation de la demande", auteur);
        return demandeCreditMapper.toResponse(saved);
    }

    @Override
    public DemandeCreditResponse getDemandeCreditById(Long id) {
        return demandeCreditMapper.toResponse(findActive(id));
    }

    @Override
    public List<DemandeCreditResponse> getAll() {
        return demandeCreditRepository.findByDeletedFalse().stream()
                .map(demandeCreditMapper::toResponse)
                .toList();
    }

    @Override
    public DemandeCreditResponse updateDemandeCredit(Long id, UpdateDemandeCreditRequest request) {
        DemandeCredit demande = findActive(id);

        if (demande.getStatut() != StatutDemande.BROUILLON) {
            throw new BusinessException("Seule une demande en BROUILLON peut etre modifiee");
        }

        validerMontant(request.getMontantDemande());
        validerDuree(request.getDureeMois());

        double mensualite = CreditSimulationUtils.calculMensualite(
                request.getMontantDemande(), request.getDureeMois(), request.getTauxFictif());
        double tauxEndettement = CreditSimulationUtils.calculTauxEndettement(
                demande.getClient().getRevenuMensuel(), demande.getClient().getChargesMensuelles(), mensualite);
        int score = calculScore(demande.getClient().getRevenuMensuel(), tauxEndettement, request.getMontantDemande());

        demande.setMontantDemande(request.getMontantDemande());
        demande.setDureeMois(request.getDureeMois());
        demande.setTauxFictif(request.getTauxFictif());
        demande.setMensualiteEstimee(mensualite);
        demande.setScoreSimplifie(score);

        return demandeCreditMapper.toResponse(demandeCreditRepository.save(demande));
    }

    @Override
    public void deleteDemandeCredit(Long id, String auteur) {
        DemandeCredit demande = findActive(id);

        if (demande.getStatut() != StatutDemande.BROUILLON) {
            throw new BusinessException("Seule une demande en BROUILLON peut etre supprimee");
        }

        demande.setDeleted(true);
        demandeCreditRepository.save(demande);
        historiser(demande, demande.getStatut(), demande.getStatut(), "Suppression de la demande", auteur);
    }

    // ---------- Simulation (sans persistance) ----------

    @Override
    public SimulationResponse simuler(CreateDemandeCreditRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

        validerMontant(request.getMontantDemande());
        validerDuree(request.getDureeMois());

        double mensualite = CreditSimulationUtils.calculMensualite(
                request.getMontantDemande(), request.getDureeMois(), request.getTauxFictif());
        double tauxEndettement = CreditSimulationUtils.calculTauxEndettement(
                client.getRevenuMensuel(), client.getChargesMensuelles(), mensualite);
        int score = calculScore(client.getRevenuMensuel(), tauxEndettement, request.getMontantDemande());
        boolean eligible = tauxEndettement <= 35 && client.getRevenuMensuel() >= 1500 && request.getMontantDemande() <= 50000;

        return new SimulationResponse(arrondir(mensualite), arrondir(tauxEndettement), score, eligible);
    }

    // ---------- Workflow ----------

    @Override
    public DemandeCreditResponse soumettre(Long id, String auteur) {
        DemandeCredit demande = findActive(id);
        if (demande.getStatut() != StatutDemande.BROUILLON) {
            throw new BusinessException("Seule une demande en BROUILLON peut etre soumise");
        }
        StatutDemande ancien = demande.getStatut();
        demande.setStatut(StatutDemande.SOUMISE);
        demande.setDateSoumission(LocalDateTime.now());
        DemandeCredit saved = demandeCreditRepository.save(demande);
        historiser(saved, ancien, StatutDemande.SOUMISE, "Soumission de la demande", auteur);
        return demandeCreditMapper.toResponse(saved);
    }

    @Override
    public DemandeCreditResponse passerEnAnalyse(Long id, String auteur) {
        DemandeCredit demande = findActive(id);
        if (demande.getStatut() != StatutDemande.SOUMISE) {
            throw new BusinessException("Seule une demande SOUMISE peut passer en analyse");
        }
        StatutDemande ancien = demande.getStatut();
        demande.setStatut(StatutDemande.EN_ANALYSE);
        DemandeCredit saved = demandeCreditRepository.save(demande);
        historiser(saved, ancien, StatutDemande.EN_ANALYSE, "Passage en analyse", auteur);
        return demandeCreditMapper.toResponse(saved);
    }

    @Override
    public DemandeCreditResponse decider(Long id, DecisionRequest request, String auteur) {
        DemandeCredit demande = findActive(id);
        if (demande.getStatut() != StatutDemande.EN_ANALYSE) {
            throw new BusinessException("Une decision ne peut etre prise que sur une demande EN_ANALYSE");
        }

        StatutDemande nouveauStatut;
        try {
            nouveauStatut = StatutDemande.valueOf(request.getNouveauStatut().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Statut de decision invalide : " + request.getNouveauStatut());
        }
        if (nouveauStatut != StatutDemande.ACCEPTEE && nouveauStatut != StatutDemande.REFUSEE) {
            throw new BusinessException("La decision doit etre ACCEPTEE ou REFUSEE");
        }
        // RG-BANK-07
        if (nouveauStatut == StatutDemande.REFUSEE
                && (request.getCommentaire() == null || request.getCommentaire().isBlank())) {
            throw new BusinessException("Un commentaire est obligatoire pour refuser une demande");
        }

        StatutDemande ancien = demande.getStatut();
        demande.setStatut(nouveauStatut);
        demande.setCommentaireDecision(request.getCommentaire());
        demande.setDateDecision(LocalDateTime.now());
        DemandeCredit saved = demandeCreditRepository.save(demande);
        historiser(saved, ancien, nouveauStatut, request.getCommentaire(), auteur);
        return demandeCreditMapper.toResponse(saved);
    }

    @Override
    public DemandeCreditResponse annuler(Long id, String auteur) {
        DemandeCredit demande = findActive(id);
        if (demande.getStatut() == StatutDemande.ACCEPTEE
                || demande.getStatut() == StatutDemande.REFUSEE
                || demande.getStatut() == StatutDemande.ANNULEE) {
            throw new BusinessException("Cette demande ne peut plus etre annulee (statut : " + demande.getStatut() + ")");
        }
        StatutDemande ancien = demande.getStatut();
        demande.setStatut(StatutDemande.ANNULEE);
        DemandeCredit saved = demandeCreditRepository.save(demande);
        historiser(saved, ancien, StatutDemande.ANNULEE, "Annulation de la demande", auteur);
        return demandeCreditMapper.toResponse(saved);
    }

    // ---------- Historique ----------

    @Override
    public List<HistoriqueResponse> getHistorique(Long demandeId) {
        return historiqueDecisionRepository.findByDemandeCreditIdOrderByDateAsc(demandeId).stream()
                .map(h -> new HistoriqueResponse(
                        h.getId(), h.getDemandeCredit().getId(),
                        h.getAncienStatut().name(), h.getNouveauStatut().name(),
                        h.getCommentaire(), h.getAuteur(), h.getDate()))
                .toList();
    }

    // ---------- Regles de gestion / utils prives ----------

    private DemandeCredit findActive(Long id) {
        return demandeCreditRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de credit introuvable"));
    }

    private void verifyClientHasNoActiveDemande(Long clientId) {
        boolean hasActive = demandeCreditRepository.existsByClientIdAndDeletedFalseAndStatutNotIn(
                clientId, List.of(StatutDemande.ACCEPTEE, StatutDemande.REFUSEE, StatutDemande.ANNULEE));
        if (hasActive) {
            throw new BusinessException("Le client possede deja une demande active");
        }
    }

    private void validerMontant(Double montant) {
        if (montant == null || montant <= 1000 || montant > 100000) {
            throw new BusinessException("Le montant doit etre superieur a 1000 et inferieur ou egal a 100000");
        }
    }

    private void validerDuree(Integer duree) {
        if (duree == null || duree < 12 || duree > 84) {
            throw new BusinessException("La duree doit etre comprise entre 12 et 84 mois");
        }
    }

    private int calculScore(double revenu, double tauxEndettement, double montant) {
        if (revenu >= 1500 && montant <= 50000 && tauxEndettement <= 35) return 100;
        if (tauxEndettement <= 45) return 60;
        return 20;
    }

    private double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }

    private void historiser(DemandeCredit demande, StatutDemande ancien, StatutDemande nouveau, String commentaire, String auteur) {
        HistoriqueDecision h = new HistoriqueDecision();
        h.setDemandeCredit(demande);
        h.setAncienStatut(ancien);
        h.setNouveauStatut(nouveau);
        h.setCommentaire(commentaire);
        h.setAuteur(auteur);
        h.setDate(LocalDateTime.now());
        historiqueDecisionRepository.save(h);
    }
}