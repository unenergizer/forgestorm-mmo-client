package com.valenguard.client.game.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.Valenguard;

import java.util.Map;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

public class MusicManager implements Disposable {

    private static final boolean PRINT_DEBUG = true;

    private final Map<Short, AudioData> gameMusic;

    @Getter
    private AudioPreferences audioPreferences = new AudioPreferences();
    private Music currentSong;
    private AudioData audioData;

    MusicManager(Map<Short, AudioData> gameMusic) {
        this.gameMusic = gameMusic;
    }

    public void playMusic(Class clazz, short audioId) {
        AudioData audioData = gameMusic.get(audioId);

        if (audioData == null) {
            println(getClass(), "AudioID is null: " + audioId, true);
            return;
        }

        this.audioData = audioData;

        if (currentSong != null) {
            if (currentSong.isPlaying()) currentSong.stop();
            currentSong.dispose();
        }

        Valenguard.getInstance().getFileManager().loadMusic(audioData);
        currentSong = Valenguard.getInstance().getFileManager().getMusic(audioData);
        currentSong.setVolume(audioPreferences.getMusicVolume());
        currentSong.play();

        println(getClass(), "AudioID: " + audioData.getAudioId());
        println(getClass(), " -Playing: " + audioData.getFileName());
        println(getClass(), " -Description: " + audioData.getDescription());
        println(getClass(), " -Source: " + clazz);
    }

    public void stopMusic(boolean dispose) {
        if (currentSong != null && currentSong.isPlaying()) currentSong.stop();
        if (dispose) dispose();
    }

    public void pauseMusic() {
        if (currentSong != null && currentSong.isPlaying() && audioPreferences.isPauseMusicOnWindowLooseFocus())
            currentSong.pause();
    }

    public void resumeMusic() {
        if (currentSong != null && !currentSong.isPlaying()) currentSong.play();
    }

    public void setVolume(float volume) {
        if (currentSong != null) currentSong.setVolume(volume);
    }

    @Override
    public void dispose() {
        if (currentSong != null) currentSong.dispose();
    }
}
