package io.github.formatttech.qualite.couverture.exemple;

import java.math.BigDecimal;

/**
 * Frais de port d'une commande : offerts à partir de 50 €, sinon forfait de 4,90 €, majoré de 5 €
 * en livraison express, même quand les frais sont offerts.
 */
public final class FraisDePort {

    private static final BigDecimal SEUIL_DE_GRATUITE = new BigDecimal("50.00");
    private static final BigDecimal OFFERTS = new BigDecimal("0.00");
    private static final BigDecimal FORFAIT = new BigDecimal("4.90");
    private static final BigDecimal MAJORATION_EXPRESS = new BigDecimal("5.00");

    private FraisDePort() {}

    public static BigDecimal pour(BigDecimal montantDeLaCommande, boolean express) {
        BigDecimal frais =
                montantDeLaCommande.compareTo(SEUIL_DE_GRATUITE) >= 0 ? OFFERTS : FORFAIT;
        return express ? frais.add(MAJORATION_EXPRESS) : frais;
    }
}
