package io.github.formatttech.qualite.mutation.exemple;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * Chaque ligne et chaque branche est exécutée, mais aucun test ne porte sur la limite : une
 * commande de 50 €.
 */
class FraisDePortTest {

    @Test
    void sousLeSeuilDeGratuiteLeForfaitEstFacture() {
        // Given
        BigDecimal montant = new BigDecimal("30.00");

        // When
        BigDecimal frais = FraisDePort.pour(montant, false);

        // Then
        assertEquals(new BigDecimal("4.90"), frais);
    }

    @Test
    void auDelaDuSeuilDeGratuiteLesFraisSontOfferts() {
        // Given
        BigDecimal montant = new BigDecimal("80.00");

        // When
        BigDecimal frais = FraisDePort.pour(montant, false);

        // Then
        assertEquals(new BigDecimal("0.00"), frais);
    }

    @Test
    void laLivraisonExpressResteMajoreeQuandLesFraisSontOfferts() {
        // Given
        BigDecimal montant = new BigDecimal("80.00");

        // When
        BigDecimal frais = FraisDePort.pour(montant, true);

        // Then
        assertEquals(new BigDecimal("5.00"), frais);
    }
}
