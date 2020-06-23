package com.valenguard.client.game.audio;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.ClientMain;
import com.valenguard.client.game.world.item.ItemStack;

import java.util.Map;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

public class SoundManager implements Disposable {

    private static final boolean PRINT_DEBUG = false;

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

        ClientMain.getInstance().getFileManager().loadSound(audioData);
        currentSound = ClientMain.getInstance().getFileManager().getSound(audioData);
        id = currentSound.play();
        currentSound.setVolume(id, audioPreferences.getSoundEffectsVolume());

        println(getClass(), "AudioID: " + audioData.getAudioId(), false, PRINT_DEBUG);
        println(getClass(), " -Playing: " + audioData.getFileName(), false, PRINT_DEBUG);
        println(getClass(), " -Description: " + audioData.getDescription(), false, PRINT_DEBUG);
        println(getClass(), " -Source: " + clazz, false, PRINT_DEBUG);
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

    public void playItemStackSoundFX(Class clazz, ItemStack itemStack) {
        switch (itemStack.getItemStackType()) {
            case HELM:
                playSoundFx(clazz, (short) 3);
                break;
            case CHEST:
                playSoundFx(clazz, (short) 3);
                break;
            case PANTS:
                playSoundFx(clazz, (short) 3);
                break;
            case SHOES:
                playSoundFx(clazz, (short) 3);
                break;
            case CAPE:
                playSoundFx(clazz, (short) 3);
                break;
            case GLOVES:
                playSoundFx(clazz, (short) 3);
                break;
            case BELT:
                playSoundFx(clazz, (short) 3);
                break;
            case RING:
                playSoundFx(clazz, (short) 6);
                break;
            case NECKLACE:
                playSoundFx(clazz, (short) 7);
                break;
            case SWORD:
                playSoundFx(clazz, (short) 4);
                break;
            case BOW:
                playSoundFx(clazz, (short) 5);
                break;
            case SHIELD:
                playSoundFx(clazz, (short) 4);
                break;
            case ARROW:
                playSoundFx(clazz, (short) 5);
                break;
            case GOLD:
                playSoundFx(clazz, (short) 1);
                break;
            case POTION:
                playSoundFx(clazz, (short) 2);
                break;
            case MATERIAL:
                playSoundFx(clazz, (short) 8);
                break;
        }
    }
}
