package com.valenguard.client.game.audio;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AudioType {
    SOUND_FX("audio/sounds/"),
    GAME_MUSIC("audio/music/");

    private String filePath;
}
