package io.github.formatttech.qualite.architecture.exemple.infrastructure;

import io.github.formatttech.qualite.architecture.exemple.metier.SourceDeTaux;
import java.math.BigDecimal;

/** Taux écrit dans le code ; en vrai, il viendrait d'une base ou d'un fichier de configuration. */
public final class TauxEnDur implements SourceDeTaux {

    @Override
    public BigDecimal tauxDeTva() {
        return new BigDecimal("0.20");
    }
}
