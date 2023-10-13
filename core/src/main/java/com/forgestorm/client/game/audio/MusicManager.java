package com.forgestorm.client.game.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.io.FileManager;
import lombok.Getter;

import java.util.Map;

import static com.forgestorm.client.util.Log.println;

public class MusicManager implements Disposable {

    private static final boolean PRINT_DEBUG = false;
    private final FileManager fileManager;
    private final Map<Short, AudioData> gameMusic;

    @Getter
    private final AudioPreferences audioPreferences = new AudioPreferences();
    private Music currentSong;
    private boolean isMusicPaused = false;

    MusicManager(ClientMain clientMain) {
        this.fileManager = clientMain.getFileManager();
        this.gameMusic = fileManager.getMusicData().getMusicDataMap();
        audioPreferences.setPlayLoginScreenMusic(clientMain.isPlayIntroMusic());
    }

    public void playMusic(Class clazz, short audioId) {
        AudioData audioData = gameMusic.get(audioId);

        if (audioData == null) {
            println(getClass(), "AudioID is null: " + audioId, true, PRINT_DEBUG);
            return;
        }

        if (currentSong != null) {
            if (currentSong.isPlaying()) currentSong.stop();
            currentSong.dispose();
        }

        fileManager.loadMusic(audioData);
        currentSong = fileManager.getMusic(audioData);
        currentSong.setVolume(audioPreferences.getMusicVolume());
        currentSong.play();

        println(getClass(), "AudioID: " + audioData.getAudioId(), true, PRINT_DEBUG);
        println(getClass(), " -Playing: " + audioData.getFileName(), true, PRINT_DEBUG);
        println(getClass(), " -Description: " + audioData.getDescription(), true, PRINT_DEBUG);
        println(getClass(), " -Source: " + clazz, true, PRINT_DEBUG);
    }

    public void stopMusic(boolean dispose) {
        if (currentSong != null && currentSong.isPlaying()) currentSong.stop();
        if (dispose) dispose();
    }

    public void pauseMusic() {
        if (currentSong != null && currentSong.isPlaying() && audioPreferences.isPauseMusicOnWindowLooseFocus()) {
            currentSong.pause();
            isMusicPaused = true;
        }
    }

    public void resumeMusic() {
        if (currentSong != null && !currentSong.isPlaying()) {
            currentSong.play();
            isMusicPaused = false;
        }
    }

    public void setVolume(float volume) {
        if (currentSong != null) currentSong.setVolume(volume);
    }

    public boolean isMusicPlaying() {
        if (currentSong == null) return false;
        return currentSong.isPlaying();
    }

    public boolean isMusicPaused() {
        if (currentSong == null) return false;
        return isMusicPaused;
    }

    @Override
    public void dispose() {
        if (currentSong != null) currentSong.dispose();
    }
}
