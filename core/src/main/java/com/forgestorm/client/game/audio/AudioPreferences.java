package com.forgestorm.client.game.audio;

import lombok.Data;

@Data
public class AudioPreferences {

    private float musicVolume = 0.5f;
    private float soundEffectsVolume = 0.5f;
    private float ambientVolume = 0.5f;

    private boolean playLoginScreenMusic = true;
    private boolean pauseMusicOnWindowLooseFocus = true;
}
