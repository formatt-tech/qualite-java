package io.github.formatttech.qualite.calendrier;

import static io.github.formatttech.qualite.calendrier.Ferie.SAINT_ETIENNE;
import static io.github.formatttech.qualite.calendrier.Ferie.VENDREDI_SAINT;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/** Territoire dont dépendent les jours fériés. */
public enum Territoire {

    /** Les onze fériés du Code du travail. */
    METROPOLE(
            // Complément : toutes les valeurs de Ferie, sauf les deux propres à l'Alsace-Moselle
            EnumSet.complementOf(EnumSet.of(VENDREDI_SAINT, SAINT_ETIENNE))),

    /** Moselle, Bas-Rhin et Haut-Rhin : les onze, plus le Vendredi saint et le 26 décembre. */
    ALSACE_MOSELLE(EnumSet.allOf(Ferie.class));

    private final Set<Ferie> feries;

    Territoire(Set<Ferie> feries) {
        this.feries = Collections.unmodifiableSet(feries);
    }

    Set<Ferie> feries() {
        return feries;
    }
}
