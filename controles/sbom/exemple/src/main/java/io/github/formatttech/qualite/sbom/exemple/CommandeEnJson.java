package io.github.formatttech.qualite.sbom.exemple;

import tools.jackson.databind.json.JsonMapper;

/** Met une commande au format JSON, avec Jackson : la dépendance que recense le SBOM. */
public final class CommandeEnJson {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private CommandeEnJson() {}

    public static String de(Commande commande) {
        return JSON.writeValueAsString(commande);
    }
}
