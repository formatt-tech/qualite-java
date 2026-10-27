# Qualité Java

[![build](https://github.com/formatt-tech/qualite-java/actions/workflows/build.yml/badge.svg)](https://github.com/formatt-tech/qualite-java/actions/workflows/build.yml)
[![Licence Apache 2.0](https://img.shields.io/badge/licence-Apache%202.0-blue.svg)](LICENSE)
![Java 25](https://img.shields.io/badge/Java-25-orange.svg)

Les outils qui encadrent la qualité d'un projet Java / Maven, montrés un par un, puis réunis.

Tout est dans [controles/](controles), un dossier par besoin. Chaque dossier suit le même schéma : un `pom.xml`
**parent** qui porte la configuration commentée de l'outil, et des projets **indépendants** qui en héritent. Un projet
conforme, que l'outil accepte ; un projet non conforme, qu'il doit rejeter. La CI vérifie les deux : l'outil est bien
configuré s'il laisse passer l'un **et** arrête l'autre, pour la bonne raison.

Le dossier `chaine-complete/` réunit ensuite tous les contrôles dans un seul parent, sur un petit projet métier.

## Par où commencer

| Vous avez | Lisez |
|---|---|
| 2 minutes | le tableau ci-dessous |
| 10 minutes | le README d'un dossier qui vous intéresse |
| 30 minutes | [chaine-complete/](controles/chaine-complete/README.md) : tous les outils ensemble, et son `pom.xml` |

## Les contrôles

| Besoin | Dossier | Outil |
|---|---|---|
| Un code formaté de la même façon par tous | [formatage/](controles/formatage/README.md) | Spotless, avec google-java-format ou le formateur Eclipse |
| Un build qui refuse un mauvais environnement ou une dépendance indésirable | [environnement/](controles/environnement/README.md) | Maven Enforcer |
| Des règles d'architecture vérifiées | `architecture/` (à venir) | ArchUnit |
| Une couverture de tests mesurée | `couverture/` (à venir) | JaCoCo |
| Des tests qui détectent vraiment les erreurs | `mutation/` (à venir) | PIT |
| Des dépendances sans faille connue | `vulnerabilites/` (à venir) | OWASP Dependency-Check |
| L'inventaire des composants livrés (SBOM) | `sbom/` (à venir) | CycloneDX |
| Tout réunir dans un POM parent | [chaine-complete/](controles/chaine-complete/README.md) | tous, plus SonarQube Cloud |

## Essayer

[![Ouvrir dans GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/formatt-tech/qualite-java)

Un environnement prêt s'ouvre dans le navigateur, avec Java 25 et un premier build déjà lancé. En local : Java 25
suffit, le dépôt fournit le Maven Wrapper.

```bash
./mvnw -f controles verify                               # tous les projets qui doivent passer
./mvnw -f controles/chaine-complete/calendrier verify     # un seul projet
```

Sous Windows : `.\mvnw.cmd` au lieu de `./mvnw`.

## Licence

[Apache 2.0](LICENSE) — Matthieu Ladrat ([formatt](https://github.com/formatt-tech)).
