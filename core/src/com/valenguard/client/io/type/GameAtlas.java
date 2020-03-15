package com.valenguard.client.io.type;

@SuppressWarnings({"SameParameterValue", "SpellCheckingInspection"})
public enum GameAtlas {

    CURSOR("cursor.atlas"),
    ITEMS("items.atlas"),
    ENTITY_CHARACTER("character.atlas"),
    ENTITY_MONSTER("monster.atlas"),
    SKILL_NODES("skillnodes.atlas"),

    TILES("tiles.atlas");

    private final String filePath;

    GameAtlas(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "graphics/atlas/" + filePath;
    }
}
