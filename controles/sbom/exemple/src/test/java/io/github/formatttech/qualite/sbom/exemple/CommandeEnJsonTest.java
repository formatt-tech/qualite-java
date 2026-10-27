package io.github.formatttech.qualite.sbom.exemple;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class CommandeEnJsonTest {

    @Test
    void uneCommandeSeTransmetAvecSaReferenceEtSonMontant() {
        // Given
        Commande commande = new Commande("C-2026-001", new BigDecimal("42.50"));

        // When
        String json = CommandeEnJson.de(commande);

        // Then
        assertEquals("{\"reference\":\"C-2026-001\",\"montant\":42.50}", json);
    }
}
