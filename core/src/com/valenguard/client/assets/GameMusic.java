package com.valenguard.client.assets;

@SuppressWarnings("SameParameterValue")
public enum GameMusic {
    LOGIN_SCREEN_THEME("10112013.ogg");

    private final String filePath;

    GameMusic(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "music/" + filePath;
    }
}
