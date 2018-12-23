package com.valenguard.client.game.assets;

@SuppressWarnings("SpellCheckingInspection")
public enum GameTexture {
    LOGO_BIG("misc/logo_big.png"),
    TILE_PATH("misc/PathFinding.png"),
    RED_X("misc/RED-X.png"),
    WARP_LOCATION("misc/warp_door.png"),
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
