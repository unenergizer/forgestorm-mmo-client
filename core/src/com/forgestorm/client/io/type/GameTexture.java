package com.forgestorm.client.io.type;

@SuppressWarnings("SpellCheckingInspection")
public enum GameTexture {
    LOGO_BIG("misc/logo_big.png"),
    LOGIN_BACKGROUND("background/main_city.png"),
    PARALLAX_BACKGROUND("background/background-purple.png"),
    SHADOW("misc/shadow.png"),
    SHADOW_HIGHLIGHT("misc/shadow_highlight.png");

    private final String filePath;

    GameTexture(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "graphics/" + filePath;
    }
}
