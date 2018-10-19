package com.valenguard.client.assets;

@SuppressWarnings("SameParameterValue")
public enum GameUI {
    UI_SKIN("uiskin.json");

    private String filePath;

    GameUI(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "skin/" + filePath;
    }
}
