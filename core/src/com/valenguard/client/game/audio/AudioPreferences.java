package com.valenguard.client.game.audio;

import lombok.Data;

@Data
public class AudioPreferences {

    private float musicVolume = 0.5f;
    private float soundEffectsVolume = 0.5f;
    private float ambientVolume = 0.5f;

    private boolean playLoginScreenMusic = false;
    private boolean pauseMusicOnWindowLooseFocus = true;
}
