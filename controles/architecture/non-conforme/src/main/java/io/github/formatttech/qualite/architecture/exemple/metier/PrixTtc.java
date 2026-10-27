package io.github.formatttech.qualite.architecture.exemple.metier;

import io.github.formatttech.qualite.architecture.exemple.infrastructure.TauxEnDur;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Le même calcul, mais le métier va chercher lui-même son taux dans l'infrastructure. */
public final class PrixTtc {

    private final SourceDeTaux source = new TauxEnDur();

    public BigDecimal de(BigDecimal prixHt) {
        return prixHt.multiply(BigDecimal.ONE.add(source.tauxDeTva()))
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
