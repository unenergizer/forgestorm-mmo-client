package com.valenguard.client.game.audio;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.Valenguard;

import java.util.Map;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

public class SoundManager implements Disposable {

    private final Map<Short, AudioData> soundFX;

    @Getter
    private AudioPreferences audioPreferences = new AudioPreferences();
    private Sound currentSound;
    private AudioData audioData;
    private long id;

    SoundManager(Map<Short, AudioData> soundFX) {
        this.soundFX = soundFX;
    }

    public void playSoundFx(Class clazz, short audioId) {
        AudioData audioData = soundFX.get(audioId);

        if (audioData == null) {
            println(getClass(), "AudioID is null: " + audioId, true);
            return;
        }

        this.audioData = audioData;

        if (currentSound != null && audioId != audioData.getAudioId()) {
            currentSound.dispose();
        }

        Valenguard.getInstance().getFileManager().loadSound(audioData);
        currentSound = Valenguard.getInstance().getFileManager().getSound(audioData);
        id = currentSound.play();
        currentSound.setVolume(id, audioPreferences.getSoundEffectsVolume());

        println(getClass(), "AudioID: " + audioData.getAudioId());
        println(getClass(), " -Playing: " + audioData.getFileName());
        println(getClass(), " -Description: " + audioData.getDescription());
        println(getClass(), " -Source: " + clazz);
    }

    public void stopSoundFx(boolean dispose) {
        if (currentSound != null) currentSound.stop();
        if (dispose) dispose();
    }

    public void setPitch(long id, float pitch) {
        if (currentSound != null) currentSound.setPitch(id, pitch);
    }

    @Override
    public void dispose() {
        if (currentSound != null) currentSound.dispose();
    }
}
