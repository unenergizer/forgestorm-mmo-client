package com.valenguard.client.game.assets;

@SuppressWarnings({"SameParameterValue", "SpellCheckingInspection"})
public enum GameAtlas {
    ENTITY_CHARACTER("running.atlas"),
    ENTITY_MONSTER("monster.atlas");

    private final String filePath;

    GameAtlas(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "atlas/" + filePath;
    }
}
