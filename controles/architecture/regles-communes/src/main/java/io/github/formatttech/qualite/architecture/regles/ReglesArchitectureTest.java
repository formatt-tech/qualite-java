package io.github.formatttech.qualite.architecture.regles;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Règles d'architecture communes à tout le domaine, lancées dans chaque projet par Surefire
 * ({@code dependenciesToScan}), sans que le projet n'écrive de classe de test.
 *
 * <p>Le périmètre est la racine du domaine : chaque projet y range son code, selon la convention de
 * paquets {@code metier} et {@code infrastructure}. Les classes des jars, dont celle-ci, et celles
 * des tests sont écartées : seul le code du projet est analysé. Le nom finit par {@code Test} pour
 * que Surefire la reconnaisse.
 */
@AnalyzeClasses(
        packages = "io.github.formatttech.qualite.architecture",
        importOptions = {ImportOption.DoNotIncludeJars.class, ImportOption.DoNotIncludeTests.class})
public final class ReglesArchitectureTest {

    /** Le métier ne connaît ni base, ni fichier, ni service : l'infrastructure dépend de lui. */
    @ArchTest
    static final ArchRule LE_METIER_NE_DEPEND_PAS_DE_L_INFRASTRUCTURE =
            noClasses()
                    .that()
                    .resideInAPackage("..metier..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..infrastructure..")
                    .as("Le métier ne dépend pas de l'infrastructure");

    /** Les sorties passent par les logs, jamais par System.out ni System.err. */
    @ArchTest
    static final ArchRule PAS_DE_SORTIE_STANDARD = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    /** Une exception dit ce qui s'est passé : ni Exception, ni RuntimeException, ni Throwable. */
    @ArchTest
    static final ArchRule PAS_D_EXCEPTION_GENERIQUE = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    /** Jamais instanciée : ArchUnit lit directement les règles, qui sont des champs statiques. */
    private ReglesArchitectureTest() {}
}
