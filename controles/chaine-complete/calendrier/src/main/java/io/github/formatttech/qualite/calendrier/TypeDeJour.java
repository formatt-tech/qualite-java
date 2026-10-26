package io.github.formatttech.qualite.calendrier;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/** Jours qui comptent dans un délai, selon la façon dont il est exprimé. */
public enum TypeDeJour {

    /**
     * Tous les jours, fériés compris. Par exemple : le délai de rétractation de 14 jours après un
     * achat à distance, un délai de paiement de 60 jours, la durée d'un arrêt de travail.
     */
    CALENDAIRE(EnumSet.noneOf(DayOfWeek.class), false),

    /**
     * Du lundi au samedi, hors fériés chômés. Par exemple : les congés payés légaux (2,5 jours par
     * mois travaillé, soit 30 par an) et de nombreux délais du Code du travail.
     */
    OUVRABLE(EnumSet.of(SUNDAY), true),

    /**
     * Du lundi au vendredi, hors fériés chômés. Par exemple : un délai de traitement « sous 3 jours
     * ouvrés », ou les congés payés quand l'entreprise les compte ainsi (25 par an).
     */
    OUVRE(EnumSet.of(SATURDAY, SUNDAY), true);

    private final Set<DayOfWeek> joursDeRepos;
    private final boolean exclutLesFeriesChomes;

    TypeDeJour(Set<DayOfWeek> joursDeRepos, boolean exclutLesFeriesChomes) {
        this.joursDeRepos = Collections.unmodifiableSet(joursDeRepos);
        this.exclutLesFeriesChomes = exclutLesFeriesChomes;
    }

    boolean estUnJourDeRepos(DayOfWeek jour) {
        return joursDeRepos.contains(jour);
    }

    boolean exclutLesFeriesChomes() {
        return exclutLesFeriesChomes;
    }
}
