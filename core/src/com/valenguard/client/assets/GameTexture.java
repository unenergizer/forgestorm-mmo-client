package com.valenguard.client.assets;

public enum GameTexture {
    REDTILE("redtile.png"),
    LOGO_BIG("logo_big.png"),
    LOGIN_BACKGROUND("background/background.jpg"),
    TEMP_PLAYER_IMG("player/player.png"),
    TEMP_OTHER_PLAYER_IMG("player/smile.png");

    private String filePath;

    GameTexture(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
