package com.valenguard.client.io.type;

@SuppressWarnings("SpellCheckingInspection")
public enum GameTexture {
    LOGO_BIG("misc/logo_big.png"),
    LOGIN_BACKGROUND("background/background.jpg"),
    PARALLAX_BACKGROUND("background/background-purple.png");

    private final String filePath;

    GameTexture(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "graphics/" + filePath;
    }
}
