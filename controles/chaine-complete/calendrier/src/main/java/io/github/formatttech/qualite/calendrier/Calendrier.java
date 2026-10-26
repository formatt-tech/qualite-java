package io.github.formatttech.qualite.calendrier;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * Calendrier d'un territoire : ses jours fériés, et le décompte des délais en jours calendaires,
 * ouvrables ou ouvrés.
 *
 * <p>Un calendrier n'est pas une année : c'est une configuration, un territoire et les choix de
 * l'entreprise, valable pour toutes les années. On le construit une fois, au démarrage de
 * l'application, puis on l'interroge pour n'importe quelle date :
 *
 * <pre>{@code
 * Calendrier calendrier = Calendrier.de(Territoire.METROPOLE)
 *         .enTravaillant(Ferie.LUNDI_DE_PENTECOTE);
 * LocalDate echeance = calendrier.avancer(dateDeReception, 3, TypeDeJour.OUVRE);
 * }</pre>
 *
 * <p>Seule la liste actuelle des fériés est connue. Elle est complète depuis 1982, année où le 8
 * mai est redevenu férié ; les années antérieures sont refusées plutôt que calculées faux.
 */
public final class Calendrier {

    /** Première année où les fériés actuels s'appliquent tous. */
    public static final int PREMIERE_ANNEE = 1982;

    private final Territoire territoire;
    private final Set<Ferie> feriesTravailles;

    private Calendrier(Territoire territoire, Set<Ferie> feriesTravailles) {
        this.territoire = territoire;
        this.feriesTravailles = feriesTravailles;
    }

    /** Calendrier où tous les fériés du territoire sont chômés. */
    public static Calendrier de(Territoire territoire) {
        return new Calendrier(territoire, EnumSet.noneOf(Ferie.class));
    }

    /**
     * Même calendrier, où ces fériés comptent comme des jours ordinaires dans les délais ; celui-ci
     * reste inchangé. Par exemple, une entreprise qui place la journée de solidarité le lundi de
     * Pentecôte.
     *
     * <p>Le choix vaut pour toutes les années : il convient quand un accord d'entreprise fixe ce
     * jour une fois pour toutes, pas quand il change d'une année à l'autre.
     */
    public Calendrier enTravaillant(Ferie... feries) {
        Set<Ferie> travailles = EnumSet.copyOf(feriesTravailles);
        travailles.addAll(Arrays.asList(feries));
        return new Calendrier(territoire, travailles);
    }

    /**
     * Fériés de l'année, classés par date : de quoi afficher un planning ou marquer les jours de
     * fermeture. Un férié travaillé y figure toujours : il reste férié, seul le décompte change.
     */
    public NavigableMap<LocalDate, Ferie> feriesDe(int annee) {
        verifierAnnee(annee);
        NavigableMap<LocalDate, Ferie> feries = new TreeMap<>();
        territoire.feries().forEach(ferie -> feries.put(ferie.dateEn(annee), ferie));
        return Collections.unmodifiableNavigableMap(feries);
    }

    /**
     * Indique si la date compte dans un délai de ce type. Par exemple : ne lancer un traitement que
     * les jours ouvrés, ou griser dans un formulaire les jours où une livraison est impossible.
     */
    public boolean estUnJour(TypeDeJour type, LocalDate date) {
        verifierAnnee(date.getYear());
        if (type.estUnJourDeRepos(date.getDayOfWeek())) {
            return false;
        }
        return !type.exclutLesFeriesChomes() || !estChome(date);
    }

    /**
     * Date atteinte en avançant de {@code n} jours du type demandé, « J+n ». Par exemple : la date
     * limite d'une demande à traiter sous 3 jours ouvrés.
     *
     * <p>Le jour de départ ne compte jamais : J+1 ouvré d'un vendredi comme d'un samedi est le
     * lundi suivant, s'il n'est pas férié. Avec {@code n} égal à 0, la date de départ est rendue
     * telle quelle.
     */
    public LocalDate avancer(LocalDate depart, int n, TypeDeJour type) {
        if (n < 0) {
            throw new IllegalArgumentException(
                    "Nombre de jours négatif : " + n + " ; seul l'avancement est possible");
        }
        LocalDate date = depart;
        int restants = n;
        while (restants > 0) {
            date = date.plusDays(1);
            if (estUnJour(type, date)) {
                restants--;
            }
        }
        return date;
    }

    /**
     * Nombre de jours du type demandé, de {@code debut} inclus à {@code fin} exclue. Par exemple :
     * les jours ouvrables décomptés pour des congés, ou le délai réellement mis à traiter une
     * demande.
     *
     * <p>La fin est exclue, comme dans les intervalles de {@code java.time} : pour tout le mois de
     * mai, la fin est le 1er juin.
     */
    public long entre(LocalDate debut, LocalDate fin, TypeDeJour type) {
        if (fin.isBefore(debut)) {
            throw new IllegalArgumentException(
                    "Intervalle refusé : la fin (" + fin + ") précède le début (" + debut + ")");
        }
        return debut.datesUntil(fin).filter(date -> estUnJour(type, date)).count();
    }

    private boolean estChome(LocalDate date) {
        return ferieDu(date).filter(ferie -> !feriesTravailles.contains(ferie)).isPresent();
    }

    private Optional<Ferie> ferieDu(LocalDate date) {
        return territoire.feries().stream()
                .filter(ferie -> ferie.dateEn(date.getYear()).equals(date))
                .findFirst();
    }

    private static void verifierAnnee(int annee) {
        if (annee < PREMIERE_ANNEE) {
            throw new IllegalArgumentException(
                    "Année "
                            + annee
                            + " refusée : les fériés actuels ne s'appliquent tous qu'à partir de "
                            + PREMIERE_ANNEE);
        }
    }
}
