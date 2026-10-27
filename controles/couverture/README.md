# Couverture

**Besoin** : savoir quelle part du code les tests exécutent vraiment, et refuser un build qui descend sous le seuil
convenu, au lieu de le découvrir en revue ou en production.

**Outil** : [JaCoCo](https://www.jacoco.org/jacoco/), qui note pendant les tests chaque ligne et chaque branche
exécutée, en produit un rapport, et fait échouer le build sous les seuils. Tout est dans le [POM parent](pom.xml) :

| Étape | Phase | Résultat |
|---|---|---|
| `prepare-agent` | avant les tests | un agent Java qui enregistre l'exécution (`target/jacoco.exec`) |
| `report` | `verify` | le rapport : `target/site/jacoco/index.html`, et `jacoco.xml` pour Sonar |
| `check` | `verify` | le build échoue sous 80 % des lignes **ou** sous 80 % des branches |

## Voir l'outil à l'œuvre

```bash
./mvnw -f controles/couverture/conforme verify       # BUILD SUCCESS
./mvnw -f controles/couverture/non-conforme verify   # BUILD FAILURE
```

Les deux projets ont le même code : `FraisDePort`, offerts à partir de 50 €, majorés en express. Le projet conforme
teste chaque règle ; le non conforme, un seul cas. Ce seul test exécute **toutes les lignes**, mais la moitié des
branches seulement. Le build échoue avec :

```text
Rule violated for bundle couverture-non-conforme: branches covered ratio is 0.50, but expected minimum is 0.80
Coverage checks have not been met. See log for details.
```

## Pièges

- **La couverture des lignes trompe** : une ligne avec une condition est couverte dès qu'elle s'exécute, quel que
  soit le cas pris. Le seuil des branches est celui qui compte ; le projet non conforme en est l'exemple.
- **Couvert ne veut pas dire vérifié** : un test sans assertion couvre autant qu'un bon test. Les tests de mutation
  (PIT) mesurent ce que les tests détectent vraiment.
- **Un `argLine` propre au projet** remplace celui que pose `prepare-agent` : JaCoCo ne mesure plus rien, sans erreur.
  On écrit `<argLine>@{argLine} …</argLine>` pour garder les deux.
- **Le seuil dans le build ou dans Sonar** : ici, le build échoue dès le poste du développeur, sur tout le code.
  Dans [chaine-complete/](../chaine-complete/README.md), JaCoCo ne fait que mesurer, et c'est Sonar qui juge, sur le
  nouveau code seulement : la voie à préférer pour un projet existant, peu couvert, qu'on ne veut pas bloquer.
- **Exclure plutôt que baisser le seuil** : le code généré ou sans logique (configuration, `main`) s'exclut par
  `<excludes>` dans la configuration du plugin, le seuil reste le même pour le reste.
