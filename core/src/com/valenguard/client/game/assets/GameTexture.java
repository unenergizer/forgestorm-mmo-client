package com.valenguard.client.game.assets;

@SuppressWarnings("SpellCheckingInspection")
public enum GameTexture {
    TILE_PATH("redtile.png"),
    LOGO_BIG("logo_big.png"),
    LOGIN_BACKGROUND("background/background.jpg"),
    TEMP_PLAYER_IMG("player/player.png"),
    TEMP_OTHER_PLAYER_IMG("player/smile.png"),
    INVALID_MOVE("buttons_242.png"),
    WARP_LOCATION("warp_door.png");

    private final String filePath;

    GameTexture(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
