package io.github.formatttech.qualite.formatage;

/**
 * Formule de politesse selon l'heure.
 */
public class Salutation {
    public String pour(int heure) {
        if (heure < 18) {
            return "Bonjour";
        }
        return "Bonsoir";
    }
}
