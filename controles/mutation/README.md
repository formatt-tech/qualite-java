# Mutation

**Besoin** : savoir si les tests détectent vraiment une erreur dans le code, et pas seulement s'ils l'exécutent.
La [couverture](../couverture/README.md) dit ce que les tests parcourent ; la mutation dit ce qu'ils vérifient.

**Outil** : [PIT](https://pitest.org/), qui introduit une à une de petites erreurs dans le code compilé, des
**mutants** (`>=` devient `>`, une condition est inversée, un retour devient `null`…), et relance les tests sur
chacun. Un mutant que les tests font échouer est **tué** ; un mutant qui passe les tests **survit** : aucun test ne
vérifie ce comportement. Sous 80 % de mutants tués, le build échoue. Tout est dans le [POM parent](pom.xml).

## Voir l'outil à l'œuvre

```bash
./mvnw -f controles/mutation/conforme verify       # BUILD SUCCESS
./mvnw -f controles/mutation/non-conforme verify   # BUILD FAILURE
```

Les deux projets ont le même `FraisDePort` que le dossier couverture : frais offerts à partir de 50 €, majorés en
express. Dans les deux, les tests exécutent toutes les lignes et toutes les branches : la couverture est complète.
Mais le projet non conforme ne teste pas la limite, une commande de 50 € tout juste. PIT change `>=` en `>`, aucun
test ne le voit, et le build échoue avec :

```text
>> Generated 4 mutations Killed 3 (75%)
Mutation score of 75 is below threshold of 80
```

Le rapport, `target/pit-reports/index.html`, montre chaque mutant survivant sur sa ligne de code : ici,
« changed conditional boundary → SURVIVED ».

## Pièges

- **Le temps** : chaque mutant relance les tests qui couvrent sa ligne. Rapide sur un petit projet, comme ici ou
  dans [chaine-complete/](../chaine-complete/README.md), où PIT tourne à chaque build. Sur un gros projet, on le
  limite aux classes modifiées (`scmMutationCoverage`), on réutilise l'analyse précédente (`withHistory`), ou on le
  lance dans un profil réservé à la CI.
- **Les classes mutées** sont par défaut celles dont le paquet commence par le `groupId` du projet : un code rangé
  sous une autre racine n'est pas muté, sans avertissement. `targetClasses` les désigne explicitement.
- **JUnit Jupiter** a besoin du greffon `pitest-junit5-plugin`, déclaré dans les dépendances du plugin : sans lui,
  le build échoue sur « Please check you have correctly installed the pitest plugin for your project's test
  library ».
- **Les mutants équivalents** : certains changent le code sans changer son comportement, et aucun test ne peut les
  tuer. C'est pourquoi le seuil reste sous 100 %.
- **Les tests doivent passer avant** : PIT commence par lancer les tests tels quels, et s'arrête si l'un échoue.
