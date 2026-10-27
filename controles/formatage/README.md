# Formatage

**Besoin** : un code formaté de la même façon par tous, sans débat de style en revue. Le build refuse un fichier mal
formaté ; une commande le corrige.

**Outil** : [Spotless](https://github.com/diffplug/spotless), plugin Maven qui vérifie le format (`spotless:check`,
phase `verify`) et le corrige (`spotless:apply`). Il applique l'un des deux formateurs ci-dessous.

| Formateur | Réglages | Quand le choisir |
|---|---|---|
| [google-java-format/](google-java-format/pom.xml), style AOSP | aucun : le format est fixé par l'outil | nouveau projet ; on ne discute plus du format, on l'applique |
| [eclipse/](eclipse/pom.xml), le formateur d'Eclipse | un profil XML ([formateur-eclipse.xml](eclipse/formateur-eclipse.xml)), exporté depuis Eclipse | équipe qui a déjà son profil ; projet existant qu'on ne veut pas reformater |

Les deux s'intègrent à IntelliJ : google-java-format par son extension, le profil Eclipse par *Code Style → Import
Scheme → Eclipse XML Profile*. L'IDE et le build appliquent alors le même format.

## Voir l'outil à l'œuvre

```bash
./mvnw -f controles/formatage/google-java-format/conforme verify       # BUILD SUCCESS
./mvnw -f controles/formatage/google-java-format/non-conforme verify   # BUILD FAILURE
```

Le projet non conforme contient le même code que le conforme, mal indenté. Le build échoue avec :

```text
The following files had format violations:
    src/main/java/io/github/formatttech/qualite/formatage/Salutation.java
        (la différence avec le format attendu, ligne par ligne)
Run 'mvn spotless:apply' to fix these violations.
```

Le message montre la différence attendue, ligne par ligne. `spotless:apply` réécrit le fichier ; le build passe
ensuite. Mêmes commandes pour `eclipse/`.

Les deux formateurs ne donnent pas exactement le même résultat : google-java-format met une javadoc courte sur une
ligne, Eclipse la garde sur trois. C'est le formateur qui fait foi, jamais l'œil du développeur.

## Pièges

- **google-java-format** : sa version doit être celle que la version de Spotless attend ; une version plus récente
  fait échouer le build sur une erreur d'appel interne. Elle est épinglée dans le POM.
- **Ordre des imports** : Spotless les range selon le style Google, même en AOSP, sauf avec
  `<reorderImports>true</reorderImports>` ; sans cette option, l'IDE et le build se contredisent.
- **Profil Eclipse et `.editorconfig`** : IntelliJ applique `.editorconfig` avant le profil importé ; l'indentation
  et la longueur de ligne doivent y être les mêmes que dans le profil.
- **Code existant** : le reformater d'un coup, dans un commit qui ne fait que ça, puis déclarer ce commit dans
  `.git-blame-ignore-revs`, pour que `git blame` continue de montrer le vrai auteur de chaque ligne.
