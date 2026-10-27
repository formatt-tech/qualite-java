# Architecture

**Besoin** : des règles d'architecture qui ne dépendent pas de la vigilance des relecteurs, et qui s'appliquent à
tous les projets d'un même domaine, sans que chacun ait à les recopier.

**Outil** : [ArchUnit](https://www.archunit.org/), bibliothèque qui exprime les règles d'architecture comme des
tests JUnit : une règle enfreinte fait échouer le build, avec les classes en cause.

## Des règles communes à tout un domaine

| Élément | Rôle |
|---|---|
| [regles-communes/](regles-communes/src/main/java/io/github/formatttech/qualite/architecture/regles/ReglesArchitectureTest.java) | les règles, publiées comme une bibliothèque (un jar versionné) |
| [pom.xml](pom.xml), le parent | ajoute ce jar aux dépendances de test de chaque projet, et demande à Surefire d'en lancer les tests (`dependenciesToScan`) |
| [conforme/](conforme/pom.xml), [non-conforme/](non-conforme/pom.xml) | des projets qui héritent du parent : **aucune classe de test**, les règles leur sont imposées |

Les règles portent sur la racine du domaine (`io.github.formatttech.qualite.architecture`) et sur une convention de
paquets : `metier` pour les règles de gestion, `infrastructure` pour ce qui touche aux bases, fichiers et services. La
règle principale : **le métier ne dépend pas de l'infrastructure**. C'est l'infrastructure qui dépend du métier, en
implémentant ses interfaces.

## Voir l'outil à l'œuvre

Les règles sont une dépendance : on les installe d'abord dans le dépôt Maven local.

```bash
./mvnw -f controles/architecture/regles-communes install
./mvnw -f controles/architecture/conforme verify        # BUILD SUCCESS
./mvnw -f controles/architecture/non-conforme verify    # BUILD FAILURE
```

Dans le projet conforme, `PrixTtc` (métier) reçoit une `SourceDeTaux`, interface du métier, que `TauxEnDur`
(infrastructure) implémente. Dans le projet non conforme, `PrixTtc` crée lui-même un `TauxEnDur` : le métier dépend
de l'infrastructure. Le build échoue avec :

```text
Architecture Violation [Priority: MEDIUM] - Rule 'Le métier ne dépend pas de l'infrastructure' was violated (1 times):
Constructor <...metier.PrixTtc.<init>()> calls constructor <...infrastructure.TauxEnDur.<init>()> in (PrixTtc.java:10)
```

## Imposer ou intégrer

| Façon de faire | Dans chaque projet | Pour |
|---|---|---|
| **Imposer** (ce dossier) : `dependenciesToScan` dans le parent | rien | appliquer les mêmes règles à tout un domaine, sans qu'un projet puisse les oublier |
| **Intégrer** : `ArchTests.in(ReglesArchitectureTest.class)` dans un `ArchitectureTest` du projet | une petite classe de test | combiner les règles communes avec des règles propres au projet, sur un périmètre que le projet choisit |

Les deux se combinent : règles communes imposées par le parent, règles propres dans un `ArchitectureTest` du projet.

## Pièges

- **Le nom de la classe de règles finit par `Test`** : Surefire ne lance que les classes qui suivent ses conventions
  de nommage, y compris dans un jar scanné.
- **Une convention de paquets partagée** est la condition de règles communes : sans elle, chaque projet a besoin de ses
  propres règles.
- **Les classes des jars sont écartées** (`DoNotIncludeJars`) : sinon, les règles analyseraient aussi les
  bibliothèques, et la leur.
- **Une règle durcie dans le jar commun** fait échouer tous les projets à leur montée de version : on la publie dans
  une nouvelle version, et chaque projet monte quand il est prêt.
