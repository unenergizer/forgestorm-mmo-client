package com.valenguard.client.game.assets;

@SuppressWarnings("unused")
public enum GameMusic {
    LOGIN_SCREEN_THEME_1("music_journey_to_the_battlefield.wav"),
    LOGIN_SCREEN_THEME_2("10112013.ogg");

    private final String filePath;

    GameMusic(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "audio/music/" + filePath;
    }
}
