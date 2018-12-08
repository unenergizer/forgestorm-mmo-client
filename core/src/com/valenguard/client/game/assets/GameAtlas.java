package com.valenguard.client.game.assets;

@SuppressWarnings({"SameParameterValue", "SpellCheckingInspection"})
public enum GameAtlas {
    ITEM_TEXTURES("items.atlas"),
    ENTITY_CHARACTER("character.atlas"),
    ENTITY_MONSTER("monster.atlas");

    private final String filePath;

    GameAtlas(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "atlas/" + filePath;
    }
}
