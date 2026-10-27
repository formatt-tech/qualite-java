package io.github.formatttech.qualite.mutation.exemple;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class FraisDePortTest {

    @Test
    void sousLeSeuilDeGratuiteLeForfaitEstFacture() {
        // Given
        BigDecimal montant = new BigDecimal("49.99");

        // When
        BigDecimal frais = FraisDePort.pour(montant, false);

        // Then
        assertEquals(new BigDecimal("4.90"), frais);
    }

    @Test
    void desLeSeuilDeGratuiteLesFraisSontOfferts() {
        // Given
        BigDecimal montant = new BigDecimal("50.00");

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
