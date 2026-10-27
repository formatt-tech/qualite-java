# SBOM

**Besoin** : savoir, pour chaque version livrée, de quels composants elle est faite. Le jour où une faille est
publiée dans une bibliothèque, on répond en minutes à « quelles applications, dans quelles versions, sont
touchées ? », sans reconstruire ni fouiller les dépôts.

**Outil** : [CycloneDX](https://cyclonedx.org/) pour Maven, qui produit avec le jar un **SBOM** (*Software Bill of
Materials*) : l'inventaire de tous les composants livrés, dans un format standard que lisent les outils de suivi.
Il est dans le [POM parent](pom.xml).

Ce n'est pas un contrôle : un inventaire n'accepte ni ne rejette rien. Ce dossier n'a donc pas de projet non
conforme, seulement un [exemple](exemple/pom.xml).

## Voir l'outil à l'œuvre

```bash
./mvnw -f controles/sbom/exemple verify
jq -r '.components[] | "\(.name) \(.version)"' controles/sbom/exemple/target/bom.json
```

Le projet déclare une seule dépendance, `jackson-databind`. Le SBOM en recense trois :

```text
jackson-databind 3.2.4
jackson-annotations 2.22
jackson-core 3.2.4
```

| Le SBOM contient, pour chaque composant… | Pour |
|---|---|
| son identifiant (`purl`), comme `pkg:maven/tools.jackson.core/jackson-core@3.2.4` | le rapprocher des bases de failles |
| ses empreintes (SHA-256, SHA-512…) | prouver que c'est bien ce fichier qui a été livré |
| sa licence (`Apache-2.0`) | vérifier ce qu'on a le droit de livrer |
| le graphe des dépendances (`dependencies`) | savoir par quelle dépendance directe un composant arrive |

JUnit n'y figure pas : utilisé seulement par les tests, il n'est pas livré.

## À quoi il sert ensuite

- **Dependency-Track** (ou un outil équivalent) reçoit le SBOM de chaque version livrée, et le confronte chaque jour
  aux failles publiées : c'est la surveillance des versions en production, que le build ne fait pas.
- **Le client ou l'auditeur** le demande de plus en plus : le Cyber Resilience Act européen impose aux fabricants
  de logiciels de tenir un SBOM de leurs produits.
- **Le dépôt Maven** le garde avec le jar : CycloneDX l'attache au projet (`-cyclonedx.json`, `-cyclonedx.xml`),
  et `deploy` le publie à côté du jar.

## Pièges

- **Le SBOM décrit ce qui est livré** : celui d'un jar ne dit rien du JDK ni de l'image Docker qui le portent. Une
  image se décrit par un outil propre aux conteneurs (Syft, Trivy).
- **Le produire au build de la version livrée**, pas après coup : reconstruit plus tard, il peut recenser d'autres
  versions, et ne prouve plus rien.
- **Un SBOM ne se lit pas seul** : sans outil qui le confronte aux failles publiées, c'est un fichier de plus dans
  `target/`.
