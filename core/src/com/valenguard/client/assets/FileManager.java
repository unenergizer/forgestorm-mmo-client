package com.valenguard.client.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

@SuppressWarnings("unused")
public class FileManager {

    private AssetManager assetManager = new AssetManager();
    private InternalFileHandleResolver filePathResolver = new InternalFileHandleResolver();

    /**
     * Wrapper method to dispose of all assets. Free's system resources.
     */
    public void dispose() {
        assetManager.dispose();
    }

    public boolean updateAssetLoading() {
        return assetManager.update();
    }

    public float loadCompleted() {
        return assetManager.getProgress();
    }

    /**
     * Check to see if the AssetManager has loaded a file.
     *
     * @param filePath The file to check for.
     * @return True if loaded, false if otherwise.
     */
    private boolean isFileLoaded(String filePath) {
        return assetManager.isLoaded(filePath);
    }

    /**
     * Unloads a game asset if it has already been loaded by the AssetManager.
     *
     * @param filePath The asset to try to unload.
     */
    public void unloadAsset(String filePath) {
        if (isFileLoaded(filePath)) {
            assetManager.unload(filePath);
        } else {
            System.err.println("Asset " + filePath + " not loaded. Nothing to unload.");
        }
    }

    /**
     * Attempts to load a music file.
     *
     * @param gameMusic The music file to load.
     */
    public void loadMusic(GameMusic gameMusic) {
        // check if already loaded
        if (isFileLoaded(gameMusic.getFilePath())) {
            System.err.println("Music already loaded: " + gameMusic.getFilePath());
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameMusic.getFilePath()).exists()) {
            assetManager.setLoader(Music.class, new MusicLoader(filePathResolver));
            assetManager.load(gameMusic.getFilePath(), Music.class);
            assetManager.finishLoadingAsset(gameMusic.getFilePath());
        } else {
            System.err.println("Music doesn't exist: " + gameMusic.getFilePath());
        }
    }

    /**
     * Attempts to get a loaded music file from the asset manager.
     *
     * @param gameMusic The music to retrieve.
     * @return A music file.
     */
    public Music getMusic(GameMusic gameMusic) {
        Music music = null;

        if (assetManager.isLoaded(gameMusic.getFilePath())) {
            music = assetManager.get(gameMusic.getFilePath(), Music.class);
        } else {
            System.err.println("Music not loaded: " + gameMusic.getFilePath());
        }

        return music;
    }

    /**
     * Attempts to load a sound file.
     *
     * @param gameSound The sound file to load.
     */
    public void loadSound(GameSound gameSound) {
        // check if already loaded
        if (isFileLoaded(gameSound.getFilePath())) {
            System.err.println("Sound already loaded: " + gameSound.getFilePath());
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameSound.getFilePath()).exists()) {
            assetManager.setLoader(Sound.class, new SoundLoader(filePathResolver));
            assetManager.load(gameSound.getFilePath(), Sound.class);
            assetManager.finishLoadingAsset(gameSound.getFilePath());
        } else {
            System.err.println("Sound doesn't exist: " + gameSound.getFilePath());
        }

    }

    /**
     * Attempts to get a loaded game sound from the asset manager.
     *
     * @param gameSound The sound to retrieve.
     * @return A game sound.
     */
    public Sound getSound(GameSound gameSound) {
        Sound sound = null;

        if (assetManager.isLoaded(gameSound.getFilePath())) {
            sound = assetManager.get(gameSound.getFilePath(), Sound.class);
        } else {
            System.err.println("Sound not loaded: " + gameSound.getFilePath());
        }

        return sound;
    }

    /**
     * Attempts to load a texture file.
     *
     * @param gameTexture The texture file to load.
     */
    public void loadTexture(GameTexture gameTexture) {
        // check if already loaded
        if (isFileLoaded(gameTexture.getFilePath())) {
            System.err.println("Texture already loaded: " + gameTexture.getFilePath());
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameTexture.getFilePath()).exists()) {
            assetManager.setLoader(Texture.class, new TextureLoader(filePathResolver));
            assetManager.load(gameTexture.getFilePath(), Texture.class);
            assetManager.finishLoadingAsset(gameTexture.getFilePath());
        } else {
            System.err.println("Texture doesn't exist: " + gameTexture.getFilePath());
        }
    }

    /**
     * Attempts to get a loaded texture from the asset manager.
     *
     * @param gameTexture The graphic to retrieve.
     * @return A texture.
     */
    public Texture getTexture(GameTexture gameTexture) {
        Texture texture = null;

        if (assetManager.isLoaded(gameTexture.getFilePath())) {
            texture = assetManager.get(gameTexture.getFilePath(), Texture.class);
        } else {
            System.err.println("Texture not loaded: " + gameTexture.getFilePath());
        }

        return texture;
    }

    /**
     * Attempts to load a TMX map file.
     *
     * @param mapFilePath The tmx map file to load.
     */
    public void loadTiledMap(String mapFilePath) {
        // check if already loaded
        if (isFileLoaded(mapFilePath)) {
            return;
        }

        // load asset
        if (filePathResolver.resolve(mapFilePath).exists()) {
            assetManager.setLoader(TiledMap.class, new TmxMapLoader(filePathResolver));
            assetManager.load(mapFilePath, TiledMap.class);
            assetManager.finishLoadingAsset(mapFilePath);
        } else {
            System.err.println("TmxMap doesn't exist: " + mapFilePath);
        }
    }

    /**
     * Attempts to get a loaded TMX map from the asset manager.
     *
     * @param mapFilePath The TMX map to retrieve.
     * @return A TMX tiled map.
     */
    public TiledMap getTiledMap(String mapFilePath) {
        TiledMap tiledMap = null;

        if (assetManager.isLoaded(mapFilePath)) {
            tiledMap = assetManager.get(mapFilePath, TiledMap.class);
        } else {
            System.err.println("TiledMap not loaded: " + mapFilePath);
        }

        return tiledMap;
    }
}
