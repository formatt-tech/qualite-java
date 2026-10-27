package io.github.formatttech.qualite.architecture.exemple.metier;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Calcule un prix toutes taxes comprises, à partir du taux que lui donne sa source. */
public final class PrixTtc {

    private final SourceDeTaux source;

    public PrixTtc(SourceDeTaux source) {
        this.source = source;
    }

    public BigDecimal de(BigDecimal prixHt) {
        return prixHt.multiply(BigDecimal.ONE.add(source.tauxDeTva()))
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
