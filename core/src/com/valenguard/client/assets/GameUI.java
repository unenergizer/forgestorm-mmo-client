package com.valenguard.client.assets;

@SuppressWarnings({"SameParameterValue", "SpellCheckingInspection"})
public enum GameUI {
    UI_SKIN("uiskin.json");

    private final String filePath;

    GameUI(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "skin/" + filePath;
    }
}
