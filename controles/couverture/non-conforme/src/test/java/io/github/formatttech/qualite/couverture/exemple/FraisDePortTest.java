package io.github.formatttech.qualite.couverture.exemple;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/** Un seul cas testé : toutes les lignes s'exécutent, mais la moitié des branches seulement. */
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
}
