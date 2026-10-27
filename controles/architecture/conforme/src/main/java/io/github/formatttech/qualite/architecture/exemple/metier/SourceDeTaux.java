package io.github.formatttech.qualite.architecture.exemple.metier;

import java.math.BigDecimal;

/** Fournit le taux de TVA en vigueur ; le métier ne sait pas d'où il vient. */
public interface SourceDeTaux {

    BigDecimal tauxDeTva();
}
