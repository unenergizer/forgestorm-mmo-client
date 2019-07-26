package com.valenguard.client.game.audio;

import com.valenguard.client.io.AudioLoader;

import lombok.Getter;

@Getter
public class AudioManager {

    private final SoundManager soundManager;
    private final MusicManager musicManager;

    public AudioManager() {
        AudioLoader audioLoader = new AudioLoader();
        soundManager = new SoundManager(audioLoader.loadSoundFX());
        musicManager = new MusicManager(audioLoader.loadGameMusic());
    }
}
