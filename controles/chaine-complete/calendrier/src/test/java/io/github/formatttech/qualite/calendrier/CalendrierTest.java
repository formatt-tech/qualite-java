package io.github.formatttech.qualite.calendrier;

import static io.github.formatttech.qualite.calendrier.TypeDeJour.CALENDAIRE;
import static io.github.formatttech.qualite.calendrier.TypeDeJour.OUVRABLE;
import static io.github.formatttech.qualite.calendrier.TypeDeJour.OUVRE;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@DisplayName("Calendrier")
class CalendrierTest {

    private static final LocalDate LUNDI = LocalDate.of(2026, 3, 2);
    private static final LocalDate VENDREDI = LocalDate.of(2026, 3, 6);
    private static final LocalDate SAMEDI = LocalDate.of(2026, 3, 7);
    private static final LocalDate DIMANCHE = LocalDate.of(2026, 3, 8);
    private static final LocalDate LUNDI_SUIVANT = LocalDate.of(2026, 3, 9);

    private static final LocalDate DEBUT_MAI_2026 = LocalDate.of(2026, 5, 1);
    private static final LocalDate DEBUT_JUIN_2026 = LocalDate.of(2026, 6, 1);
    private static final LocalDate ASCENSION_2026 = LocalDate.of(2026, 5, 14);
    private static final LocalDate LUNDI_DE_PENTECOTE_2026 = LocalDate.of(2026, 5, 25);

    private final Calendrier paris = Calendrier.de(Territoire.METROPOLE);
    private final Calendrier strasbourg = Calendrier.de(Territoire.ALSACE_MOSELLE);

    @Nested
    @DisplayName("ses jours fériés")
    class Feries {

        @Test
        @DisplayName("sont onze en métropole, classés par date")
        void lesOnzeFeriesSontClassesParDate() {
            assertThat(paris.feriesDe(2026).keySet())
                    .containsExactly(
                            LocalDate.of(2026, 1, 1),
                            LocalDate.of(2026, 4, 6),
                            LocalDate.of(2026, 5, 1),
                            LocalDate.of(2026, 5, 8),
                            ASCENSION_2026,
                            LUNDI_DE_PENTECOTE_2026,
                            LocalDate.of(2026, 7, 14),
                            LocalDate.of(2026, 8, 15),
                            LocalDate.of(2026, 11, 1),
                            LocalDate.of(2026, 11, 11),
                            LocalDate.of(2026, 12, 25));
        }

        @Test
        @DisplayName("se retrouvent par leur date")
        void unFerieSeRetrouveParSaDate() {
            assertThat(paris.feriesDe(2026))
                    .containsEntry(ASCENSION_2026, Ferie.ASCENSION)
                    .doesNotContainKey(LUNDI);
        }

        @Test
        @DisplayName("comptent le lundi de Pentecôte, toujours férié")
        void leLundiDePentecoteResteFerie() {
            assertThat(paris.feriesDe(2026))
                    .containsEntry(LUNDI_DE_PENTECOTE_2026, Ferie.LUNDI_DE_PENTECOTE);
        }

        @Test
        @DisplayName("sont treize en Alsace-Moselle : Vendredi saint et 26 décembre en plus")
        void lAlsaceMoselleAjouteDeuxFeries() {
            assertThat(strasbourg.feriesDe(2026))
                    .hasSize(13)
                    .containsAllEntriesOf(paris.feriesDe(2026))
                    .containsEntry(LocalDate.of(2026, 4, 3), Ferie.VENDREDI_SAINT)
                    .containsEntry(LocalDate.of(2026, 12, 26), Ferie.SAINT_ETIENNE);
        }

        @Test
        @DisplayName("ne comptent pas le 26 décembre en métropole")
        void le26DecembreNEstPasFerieEnMetropole() {
            assertThat(paris.feriesDe(2026)).doesNotContainKey(LocalDate.of(2026, 12, 26));
        }
    }

    @Nested
    @DisplayName("selon l'année")
    class Annee {

        @Test
        @DisplayName("accepte 1982, première année avec le 8 mai férié")
        void lAnnee1982EstAcceptee() {
            assertThat(paris.feriesDe(1982))
                    .containsEntry(LocalDate.of(1982, 5, 8), Ferie.VICTOIRE_1945);
        }

        @Test
        @DisplayName("refuse les années antérieures, où les fériés étaient autres")
        void lesAnneesAvant1982SontRefusees() {
            // When
            Throwable erreur = catchThrowable(() -> paris.feriesDe(1981));

            // Then
            assertThat(erreur)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("1981")
                    .hasMessageContaining("1982");
        }

        @Test
        @DisplayName("refuse aussi de décompter une date antérieure, même en jours calendaires")
        void unDecompteAvant1982EstRefuse() {
            // Given
            LocalDate fin1981 = LocalDate.of(1981, 12, 31);

            // When
            Throwable erreur = catchThrowable(() -> paris.estUnJour(CALENDAIRE, fin1981));

            // Then
            assertThat(erreur).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("en jours calendaires")
    class JoursCalendaires {

        @Test
        @DisplayName("compte tous les jours, week-end et fériés compris")
        void toutJourCompte() {
            assertThat(paris.estUnJour(CALENDAIRE, SAMEDI)).isTrue();
            assertThat(paris.estUnJour(CALENDAIRE, DIMANCHE)).isTrue();
            assertThat(paris.estUnJour(CALENDAIRE, ASCENSION_2026)).isTrue();
        }

        @Test
        @DisplayName("J+1 du 30 avril 2026 est le 1er mai, pourtant férié")
        void leLendemainCalendaireEstLeLendemain() {
            assertThat(paris.avancer(LocalDate.of(2026, 4, 30), 1, CALENDAIRE))
                    .isEqualTo(DEBUT_MAI_2026);
        }

        @Test
        @DisplayName("mai 2026 en compte 31")
        void maiCompte31JoursCalendaires() {
            assertThat(paris.entre(DEBUT_MAI_2026, DEBUT_JUIN_2026, CALENDAIRE)).isEqualTo(31);
        }
    }

    @Nested
    @DisplayName("en jours ouvrables")
    class JoursOuvrables {

        @Test
        @DisplayName("compte le samedi")
        void leSamediCompte() {
            assertThat(paris.estUnJour(OUVRABLE, SAMEDI)).isTrue();
        }

        @Test
        @DisplayName("ne compte pas le dimanche")
        void leDimancheNeComptePas() {
            assertThat(paris.estUnJour(OUVRABLE, DIMANCHE)).isFalse();
        }

        @Test
        @DisplayName("ne compte pas un samedi férié : le 1er mai 2027")
        void unSamediFerieNeComptePas() {
            assertThat(paris.estUnJour(OUVRABLE, LocalDate.of(2027, 5, 1))).isFalse();
        }

        @Test
        @DisplayName("J+1 d'un vendredi est le samedi")
        void leLendemainOuvrableDUnVendrediEstLeSamedi() {
            assertThat(paris.avancer(VENDREDI, 1, OUVRABLE)).isEqualTo(SAMEDI);
        }

        @Test
        @DisplayName("J+1 d'un samedi est le lundi")
        void leLendemainOuvrableDUnSamediEstLeLundi() {
            assertThat(paris.avancer(SAMEDI, 1, OUVRABLE)).isEqualTo(LUNDI_SUIVANT);
        }

        @Test
        @DisplayName("mai 2026 en compte 22 : 26 jours hors dimanches, dont 4 fériés")
        void maiCompte22JoursOuvrables() {
            assertThat(paris.entre(DEBUT_MAI_2026, DEBUT_JUIN_2026, OUVRABLE)).isEqualTo(22);
        }
    }

    @Nested
    @DisplayName("en jours ouvrés")
    class JoursOuvres {

        @Test
        @DisplayName("compte les jours de semaine")
        void unJourDeSemaineCompte() {
            assertThat(paris.estUnJour(OUVRE, LUNDI)).isTrue();
        }

        @Test
        @DisplayName("ne compte ni le samedi ni le dimanche")
        void leWeekEndNeComptePas() {
            assertThat(paris.estUnJour(OUVRE, SAMEDI)).isFalse();
            assertThat(paris.estUnJour(OUVRE, DIMANCHE)).isFalse();
        }

        @Test
        @DisplayName("ne compte pas un férié en semaine")
        void unFerieEnSemaineNeComptePas() {
            assertThat(paris.estUnJour(OUVRE, ASCENSION_2026)).isFalse();
        }

        @Test
        @DisplayName("J+1 d'un vendredi est le lundi")
        void leLendemainOuvreDUnVendrediEstLeLundi() {
            assertThat(paris.avancer(VENDREDI, 1, OUVRE)).isEqualTo(LUNDI_SUIVANT);
        }

        @Test
        @DisplayName("saute les fériés : J+1 du 30 avril 2026 est le 4 mai")
        void lesFeriesSontSautes() {
            // Given : 1er mai férié, puis le week-end
            LocalDate veilleDuPremierMai = LocalDate.of(2026, 4, 30);

            // When
            LocalDate arrivee = paris.avancer(veilleDuPremierMai, 1, OUVRE);

            // Then
            assertThat(arrivee).isEqualTo(LocalDate.of(2026, 5, 4));
        }

        @Test
        @DisplayName("mai 2026 en compte 17 : 21 jours de semaine, dont 4 fériés")
        void maiCompte17JoursOuvres() {
            assertThat(paris.entre(DEBUT_MAI_2026, DEBUT_JUIN_2026, OUVRE)).isEqualTo(17);
        }

        @Test
        @DisplayName("2026 en compte 252")
        void uneAnneeCompteSesJoursOuvres() {
            assertThat(paris.entre(LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), OUVRE))
                    .isEqualTo(252);
        }

        @Test
        @DisplayName("dépendent du territoire : le 26 décembre compte à Paris, pas à Strasbourg")
        void leResultatDependDuTerritoire() {
            // Given : mercredi 24 décembre 2025, Noël le jeudi
            LocalDate veilleDeNoel = LocalDate.of(2025, 12, 24);

            // When / Then
            assertThat(paris.avancer(veilleDeNoel, 1, OUVRE)).isEqualTo(LocalDate.of(2025, 12, 26));
            assertThat(strasbourg.avancer(veilleDeNoel, 1, OUVRE))
                    .isEqualTo(LocalDate.of(2025, 12, 29));
        }
    }

    @Nested
    @DisplayName("avec un férié travaillé")
    class FerieTravaille {

        private final Calendrier avecSolidarite = paris.enTravaillant(Ferie.LUNDI_DE_PENTECOTE);

        @Test
        @DisplayName("compte le lundi de Pentecôte")
        void unFerieTravailleCompte() {
            assertThat(avecSolidarite.estUnJour(OUVRE, LUNDI_DE_PENTECOTE_2026)).isTrue();
        }

        @Test
        @DisplayName("le garde férié : seul le décompte change")
        void unFerieTravailleResteFerie() {
            assertThat(avecSolidarite.feriesDe(2026))
                    .containsEntry(LUNDI_DE_PENTECOTE_2026, Ferie.LUNDI_DE_PENTECOTE);
        }

        @Test
        @DisplayName("laisse les autres fériés chômés")
        void lesAutresFeriesRestentChomes() {
            assertThat(avecSolidarite.estUnJour(OUVRE, ASCENSION_2026)).isFalse();
        }

        @Test
        @DisplayName("ne modifie pas le calendrier d'origine")
        void leCalendrierDOrigineEstInchange() {
            assertThat(paris.estUnJour(OUVRE, LUNDI_DE_PENTECOTE_2026)).isFalse();
        }

        @Test
        @DisplayName("raccourcit J+1 du vendredi 22 mai 2026 : le 25 au lieu du 26")
        void unFerieTravailleRaccourcitLeDelai() {
            // Given
            LocalDate vendredi = LocalDate.of(2026, 5, 22);

            // When / Then
            assertThat(paris.avancer(vendredi, 1, OUVRE)).isEqualTo(LocalDate.of(2026, 5, 26));
            assertThat(avecSolidarite.avancer(vendredi, 1, OUVRE))
                    .isEqualTo(LUNDI_DE_PENTECOTE_2026);
        }
    }

    @Nested
    @DisplayName("aux limites")
    class Limites {

        @Test
        @DisplayName("ne compte jamais le jour de départ : J+1 ouvré d'un samedi est le lundi")
        void leJourDeDepartNeComptePas() {
            assertThat(paris.avancer(SAMEDI, 1, OUVRE)).isEqualTo(LUNDI_SUIVANT);
        }

        @Test
        @DisplayName("J+3 ouvrés d'un lundi est le jeudi")
        void plusieursJoursSAjoutent() {
            assertThat(paris.avancer(LUNDI, 3, OUVRE)).isEqualTo(LocalDate.of(2026, 3, 5));
        }

        @Test
        @DisplayName("J+0 rend la date de départ, même un dimanche")
        void zeroJourRendLaDateDeDepart() {
            assertThat(paris.avancer(DIMANCHE, 0, OUVRE)).isEqualTo(DIMANCHE);
        }

        @Test
        @DisplayName("refuse un nombre de jours négatif")
        void unNombreNegatifEstRefuse() {
            // When
            Throwable erreur = catchThrowable(() -> paris.avancer(LUNDI, -1, OUVRE));

            // Then
            assertThat(erreur)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("-1");
        }

        @Test
        @DisplayName("exclut la fin : d'un lundi au mardi, un seul jour")
        void laFinEstExclue() {
            assertThat(paris.entre(LUNDI, LUNDI.plusDays(1), OUVRE)).isEqualTo(1);
        }

        @Test
        @DisplayName("ne compte rien entre une date et elle-même")
        void unIntervalleVideNeCompteRien() {
            assertThat(paris.entre(LUNDI, LUNDI, OUVRE)).isZero();
        }

        @Test
        @DisplayName("refuse une fin antérieure au début")
        void uneFinAvantLeDebutEstRefusee() {
            // When
            Throwable erreur = catchThrowable(() -> paris.entre(VENDREDI, LUNDI, OUVRE));

            // Then
            assertThat(erreur).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
