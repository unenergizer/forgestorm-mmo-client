package com.valenguard.client.game.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.io.type.GameMusic;

import lombok.Getter;

public class MusicManager implements Disposable {

    @Getter
    private AudioPreferences audioPreferences = new AudioPreferences();
    private Music currentSong;

    public void playSong(GameMusic gameMusic) {
        if (currentSong != null) {
            if (currentSong.isPlaying()) currentSong.stop();
            currentSong.dispose();
        }
        Valenguard.getInstance().getFileManager().loadMusic(gameMusic);
        currentSong = Valenguard.getInstance().getFileManager().getMusic(gameMusic);
        currentSong.setVolume(audioPreferences.getMusicVolume());
        currentSong.play();
    }

    public void stopSong(boolean dispose) {
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
