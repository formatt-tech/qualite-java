package io.github.formatttech.qualite.calendrier.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import io.github.formatttech.qualite.calendrier.Calendrier;

/**
 * Règles d'architecture, vérifiées sur le code de production à chaque build.
 *
 * <p>Une règle enfreinte fait échouer le build, avec les classes en cause dans le message.
 */
@AnalyzeClasses(
        packages = "io.github.formatttech.qualite.calendrier",
        importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    /**
     * Les énumérations sont le vocabulaire du calendrier (fériés, territoires, types de jour) ;
     * elles ne connaissent pas le calcul qui les utilise.
     */
    @ArchTest
    static final ArchRule LE_VOCABULAIRE_NE_DEPEND_PAS_DU_CALCUL =
            noClasses()
                    .that()
                    .areEnums()
                    .should()
                    .dependOnClassesThat()
                    .areAssignableTo(Calendrier.class)
                    .as("Les énumérations ne dépendent pas du calendrier");

    /** Les sorties passent par les logs, jamais par System.out ni System.err. */
    @ArchTest
    static final ArchRule PAS_DE_SORTIE_STANDARD = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    /** Une exception dit ce qui s'est passé : ni Exception, ni RuntimeException, ni Throwable. */
    @ArchTest
    static final ArchRule PAS_D_EXCEPTION_GENERIQUE = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    /** Une seule façade de logs pour tout le projet. */
    @ArchTest
    static final ArchRule PAS_DE_JAVA_UTIL_LOGGING = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    /** Les dépendances sont injectées par le constructeur : visibles et testables. */
    @ArchTest
    static final ArchRule PAS_D_INJECTION_PAR_CHAMP = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    /** Jamais instanciée : ArchUnit lit directement les règles, qui sont des champs statiques. */
    private ArchitectureTest() {}
}
