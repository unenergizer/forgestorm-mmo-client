package com.forgestorm.client.game.audio;

import lombok.Getter;

@Getter
public class AudioManager {

    private final SoundManager soundManager;
    private final MusicManager musicManager;

    public AudioManager() {
        soundManager = new SoundManager();
        musicManager = new MusicManager();
    }
}
