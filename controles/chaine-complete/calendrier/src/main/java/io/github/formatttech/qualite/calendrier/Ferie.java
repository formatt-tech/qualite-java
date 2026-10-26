package io.github.formatttech.qualite.calendrier;

import java.time.LocalDate;
import java.util.function.IntFunction;

/**
 * Jours fériés légaux en France : les onze du Code du travail (article L3133-1), et les deux
 * propres à l'Alsace-Moselle.
 *
 * <p>Férié ne veut pas dire chômé : hormis le 1er mai, c'est l'employeur ou un accord collectif qui
 * décide si un férié est travaillé.
 */
public enum Ferie {
    JOUR_DE_L_AN("Jour de l'an", fixe(1, 1)),
    LUNDI_DE_PAQUES("Lundi de Pâques", apresPaques(1)),

    /** Seul férié obligatoirement chômé, sauf là où l'activité ne peut pas s'interrompre. */
    FETE_DU_TRAVAIL("Fête du travail", fixe(5, 1)),

    VICTOIRE_1945("Victoire 1945", fixe(5, 8)),
    ASCENSION("Ascension", apresPaques(39)),

    /**
     * Toujours férié, mais souvent travaillé : il est fréquemment retenu pour la journée de
     * solidarité, que l'entreprise place librement depuis 2008.
     */
    LUNDI_DE_PENTECOTE("Lundi de Pentecôte", apresPaques(50)),

    FETE_NATIONALE("Fête nationale", fixe(7, 14)),
    ASSOMPTION("Assomption", fixe(8, 15)),
    TOUSSAINT("Toussaint", fixe(11, 1)),
    ARMISTICE_1918("Armistice 1918", fixe(11, 11)),
    NOEL("Noël", fixe(12, 25)),

    /** Alsace-Moselle seulement. */
    VENDREDI_SAINT("Vendredi saint", apresPaques(-2)),

    /** Alsace-Moselle seulement. */
    SAINT_ETIENNE("Saint-Étienne", fixe(12, 26));

    private final String libelle;
    private final IntFunction<LocalDate> dateDansLAnnee;

    Ferie(String libelle, IntFunction<LocalDate> dateDansLAnnee) {
        this.libelle = libelle;
        this.dateDansLAnnee = dateDansLAnnee;
    }

    public String libelle() {
        return libelle;
    }

    LocalDate dateEn(int annee) {
        return dateDansLAnnee.apply(annee);
    }

    private static IntFunction<LocalDate> fixe(int mois, int jour) {
        return annee -> LocalDate.of(annee, mois, jour);
    }

    /** Écart en jours avec le dimanche de Pâques, négatif pour un férié qui le précède. */
    private static IntFunction<LocalDate> apresPaques(int jours) {
        return annee -> dimancheDePaques(annee).plusDays(jours);
    }

    /**
     * Dimanche de Pâques du calendrier grégorien, par l'algorithme de Meeus, Jones et Butcher. Les
     * variables gardent les noms d'une lettre de l'algorithme publié, pour pouvoir le comparer à sa
     * source ligne à ligne.
     */
    static LocalDate dimancheDePaques(int annee) {
        int a = annee % 19;
        int b = annee / 100;
        int c = annee % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int n = h + l - 7 * m + 114;
        int mois = n / 31;
        int jour = n % 31 + 1;
        return LocalDate.of(annee, mois, jour);
    }
}
