package com.valenguard.client.game.assets;

@SuppressWarnings({"SameParameterValue", "SpellCheckingInspection"})
public enum GameAtlas {
    MAIN_ATLAS("running.atlas");

    private final String filePath;

    GameAtlas(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "atlas/" + filePath;
    }
}
