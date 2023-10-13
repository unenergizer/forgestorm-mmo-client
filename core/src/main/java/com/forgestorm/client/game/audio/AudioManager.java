package com.forgestorm.client.game.audio;

import com.forgestorm.client.ClientMain;
import lombok.Getter;

@Getter
public class AudioManager {

    private final SoundManager soundManager;
    private final MusicManager musicManager;

    public AudioManager(ClientMain clientMain) {
        soundManager = new SoundManager(clientMain);
        musicManager = new MusicManager(clientMain);
    }
}
