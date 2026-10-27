# Environnement

**Besoin** : un build qui refuse de démarrer dans un mauvais environnement, ou avec une dépendance indésirable, au
lieu d'échouer plus loin pour une raison obscure, ou pire, de réussir.

**Outil** : [Maven Enforcer](https://maven.apache.org/enforcer/maven-enforcer-plugin/), plugin qui vérifie des
règles dès la phase `validate`, avant toute compilation. Les règles sont dans le [POM parent](pom.xml) :

| Règle | Le build s'arrête si… |
|---|---|
| `requireJavaVersion` | le JDK est plus ancien que la version du projet (25) |
| `requireMavenVersion` | Maven est plus ancien que 3.9 |
| `banDuplicatePomDependencyVersions` | une même dépendance est déclarée deux fois dans un POM : la seconde écraserait la première sans le dire |
| `bannedDependencies` | une dépendance interdite est présente, **même arrivée par transitivité** : Log4j 1, Commons Logging, JUnit 4 |

## Voir l'outil à l'œuvre

```bash
./mvnw -f controles/environnement/conforme verify       # BUILD SUCCESS
./mvnw -f controles/environnement/non-conforme verify   # BUILD FAILURE
```

Le [projet non conforme](non-conforme/pom.xml) déclare une seule dépendance, anodine : `httpclient`. Mais celle-ci
apporte `commons-logging`, interdite. Enforcer le signale, avec le chemin qui y mène :

```text
Rule 3: org.apache.maven.enforcer.rules.dependency.BannedDependencies failed with message:
...
   commons-logging:commons-logging:jar:1.2 <--- banned via the exclude/include list
```

Le [projet conforme](conforme/pom.xml) montre la correction habituelle : exclure la dépendance indésirable, et la
remplacer par son équivalent autorisé, ici le pont `jcl-over-slf4j`, qui redirige les appels de Commons Logging vers
SLF4J.

Les deux projets n'ont pas de code : les règles portent sur l'environnement et les dépendances. Les règles de
version du JDK et de Maven ne se montrent pas par un projet non conforme : il faudrait un autre JDK ; elles
s'éprouvent en lançant le build avec Java 21.

## Pièges

- **Trouver d'où vient une dépendance interdite** : `./mvnw dependency:tree -Dincludes=commons-logging` montre la
  chaîne qui l'apporte.
- **Exclure sans remplacer** casse à l'exécution si la bibliothèque appelle encore la dépendance exclue : c'est le
  rôle des ponts comme `jcl-over-slf4j` ou `log4j-over-slf4j`.
- **Une règle trop large bloque tout le monde d'un coup** : ajouter une interdiction dans un POM parent partagé fait
  échouer tous les projets qui en héritent au build suivant. On l'annonce, et on la passe d'abord en avertissement
  (`<fail>false</fail>`).
