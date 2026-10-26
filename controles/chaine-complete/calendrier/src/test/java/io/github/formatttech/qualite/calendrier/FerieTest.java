package io.github.formatttech.qualite.calendrier;

import static org.assertj.core.api.Assertions.assertThat;

import static java.time.DayOfWeek.SUNDAY;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;
import java.util.stream.IntStream;

@DisplayName("Un jour férié")
class FerieTest {

    @Test
    @DisplayName("à date fixe tombe chaque année le même jour")
    void unFerieFixeTombeLeMemeJour() {
        assertThat(Ferie.FETE_NATIONALE.dateEn(2026)).isEqualTo(LocalDate.of(2026, 7, 14));
        assertThat(Ferie.FETE_NATIONALE.dateEn(2027)).isEqualTo(LocalDate.of(2027, 7, 14));
    }

    @Test
    @DisplayName("mobile suit Pâques, avant comme après")
    void unFerieMobileSuitPaques() {
        // Given : Pâques le dimanche 5 avril 2026

        // Then
        assertThat(Ferie.VENDREDI_SAINT.dateEn(2026)).isEqualTo(LocalDate.of(2026, 4, 3));
        assertThat(Ferie.ASCENSION.dateEn(2026)).isEqualTo(LocalDate.of(2026, 5, 14));
    }

    @Test
    @DisplayName("a un libellé lisible")
    void unFerieAUnLibelle() {
        assertThat(Ferie.LUNDI_DE_PENTECOTE.libelle()).isEqualTo("Lundi de Pentecôte");
    }

    @Nested
    @DisplayName("Pâques")
    class Paques {

        @ParameterizedTest(name = "{0} : {1}")
        @CsvSource({"1982, 1982-04-11", "2024, 2024-03-31", "2025, 2025-04-20", "2026, 2026-04-05"})
        @DisplayName("tombe à la date publiée")
        void paquesTombeALaDatePubliee(int annee, LocalDate datePubliee) {
            assertThat(Ferie.dimancheDePaques(annee)).isEqualTo(datePubliee);
        }

        /** Les deux extrêmes possibles : un algorithme faux s'y trompe en premier. */
        @Test
        @DisplayName("tombe au plus tôt le 22 mars, en 2285")
        void paquesTombeAuPlusTotLe22Mars() {
            assertThat(Ferie.dimancheDePaques(2285)).isEqualTo(LocalDate.of(2285, 3, 22));
        }

        @Test
        @DisplayName("tombe au plus tard le 25 avril, en 2038")
        void paquesTombeAuPlusTardLe25Avril() {
            assertThat(Ferie.dimancheDePaques(2038)).isEqualTo(LocalDate.of(2038, 4, 25));
        }

        /**
         * Les dates choisies ci-dessus vérifient quelques années ; celui-ci vérifie la règle sur
         * plus de quatorze siècles, de la première année entière du calendrier grégorien à 2999.
         */
        @Test
        @DisplayName("tombe toujours un dimanche, du 22 mars au 25 avril")
        void paquesTombeToujoursUnDimancheEntreLe22MarsEtLe25Avril() {
            // Given
            IntStream annees = IntStream.rangeClosed(1583, 2999);

            // When
            List<LocalDate> dimanchesDePaques = annees.mapToObj(Ferie::dimancheDePaques).toList();

            // Then
            assertThat(dimanchesDePaques)
                    .isNotEmpty()
                    .allSatisfy(
                            paques -> {
                                assertThat(paques.getDayOfWeek()).isEqualTo(SUNDAY);
                                assertThat(MonthDay.from(paques))
                                        .isBetween(MonthDay.of(3, 22), MonthDay.of(4, 25));
                            });
        }
    }
}
