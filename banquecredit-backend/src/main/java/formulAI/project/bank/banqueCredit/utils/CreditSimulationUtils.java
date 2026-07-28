package formulAI.project.bank.banqueCredit.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CreditSimulationUtils {

    private CreditSimulationUtils() {
    }

    public static double calculMensualite(
            Double montant,
            Integer duree,
            Double tauxAnnuel) {

        double mensualite;

        double tauxMensuel = tauxAnnuel / 12 / 100;

        if (tauxMensuel == 0) {
            mensualite = montant / duree;
        } else {
            mensualite = montant * (
                    tauxMensuel /
                            (1 - Math.pow(1 + tauxMensuel, -duree))
            );
        }

        return arrondir(mensualite);
    }

    public static double calculTauxEndettement(
            Double revenuMensuel,
            Double chargesMensuelles,
            Double mensualiteEstimee) {

        if (revenuMensuel == null || revenuMensuel <= 0) {
            throw new IllegalArgumentException(
                    "Le revenu mensuel doit être supérieur à 0");
        }

        double taux = ((chargesMensuelles + mensualiteEstimee)
                / revenuMensuel) * 100;

        return arrondir(taux);
    }

    private static double arrondir(double valeur) {
        return BigDecimal.valueOf(valeur)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}