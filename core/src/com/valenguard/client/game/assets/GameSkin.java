package com.valenguard.client.game.assets;

@SuppressWarnings({"SameParameterValue", "SpellCheckingInspection"})
public enum GameSkin {

    DEFAULT("uiskin.json");

    private final String filePath;

    GameSkin(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "skin/" + filePath;
    }
}
