package io.github.formatttech.qualite.sbom.exemple;

import java.math.BigDecimal;

/** Une commande, telle qu'elle est transmise aux autres applications. */
public record Commande(String reference, BigDecimal montant) {}
