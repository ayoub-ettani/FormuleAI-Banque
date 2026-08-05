package formulAI.project.bank.banqueCredit.specification;

import formulAI.project.bank.banqueCredit.model.Client;
import formulAI.project.bank.banqueCredit.model.DemandeCredit;
import formulAI.project.bank.banqueCredit.model.StatutDemande;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class DemandeCreditSpecifications {

    private static final String ESCAPE_CHAR = "\\";

    /**
     * Échappe les caractères spéciaux LIKE pour éviter les comportements non intentionnels
     */
    private static String escapeLikeSpecialChars(String value) {
        if (value == null) {
            return null;
        }
        return value.replace(ESCAPE_CHAR, ESCAPE_CHAR + ESCAPE_CHAR)
                    .replace("%", ESCAPE_CHAR + "%")
                    .replace("_", ESCAPE_CHAR + "_");
    }

    public static Specification<DemandeCredit> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<DemandeCredit> clientNotDeleted() {
        return (root, query, cb) -> {
            Join<DemandeCredit, Client> clientJoin = root.join("client");
            return cb.equal(clientJoin.get("deleted"), false);
        };
    }

    public static Specification<DemandeCredit> clientNomContains(String nomClient) {
        return (root, query, cb) -> {
            if (nomClient == null || nomClient.isBlank()) {
                return cb.conjunction();
            }
            Join<DemandeCredit, Client> clientJoin = root.join("client");
            String escaped = escapeLikeSpecialChars(nomClient.toLowerCase());
            return cb.like(cb.lower(clientJoin.get("nom")), "%" + escaped + "%", '\\');
        };
    }

    public static Specification<DemandeCredit> hasStatut(StatutDemande statut) {
        return (root, query, cb) -> {
            if (statut == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("statut"), statut);
        };
    }

    public static Specification<DemandeCredit> montantGreaterThanOrEqual(Double montantMin) {
        return (root, query, cb) -> {
            if (montantMin == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("montantDemande"), montantMin);
        };
    }

    public static Specification<DemandeCredit> montantLessThanOrEqual(Double montantMax) {
        return (root, query, cb) -> {
            if (montantMax == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("montantDemande"), montantMax);
        };
    }
}

