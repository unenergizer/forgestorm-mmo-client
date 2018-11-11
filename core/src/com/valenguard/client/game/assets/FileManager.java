package com.valenguard.client.game.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.valenguard.client.util.Log;

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
            Log.println(getClass(), "Asset " + filePath + " not loaded. Nothing to unload.", true);
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
            Log.println(getClass(), "Music already loaded: " + gameMusic.getFilePath(), true);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameMusic.getFilePath()).exists()) {
            assetManager.setLoader(Music.class, new MusicLoader(filePathResolver));
            assetManager.load(gameMusic.getFilePath(), Music.class);
            assetManager.finishLoadingAsset(gameMusic.getFilePath());
        } else {
            Log.println(getClass(), "Music doesn't exist: " + gameMusic.getFilePath(), true);
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
            Log.println(getClass(), "Music not loaded: " + gameMusic.getFilePath(), true);
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
            Log.println(getClass(), "Sound already loaded: " + gameSound.getFilePath(), true);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameSound.getFilePath()).exists()) {
            assetManager.setLoader(Sound.class, new SoundLoader(filePathResolver));
            assetManager.load(gameSound.getFilePath(), Sound.class);
            assetManager.finishLoadingAsset(gameSound.getFilePath());
        } else {
            Log.println(getClass(), "Sound doesn't exist: " + gameSound.getFilePath(), true);
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
            Log.println(getClass(), "Sound not loaded: " + gameSound.getFilePath(), true);
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
            Log.println(getClass(), "Texture already loaded: " + gameTexture.getFilePath(), true);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameTexture.getFilePath()).exists()) {
            assetManager.setLoader(Texture.class, new TextureLoader(filePathResolver));
            assetManager.load(gameTexture.getFilePath(), Texture.class);
            assetManager.finishLoadingAsset(gameTexture.getFilePath());
        } else {
            Log.println(getClass(), "Texture doesn't exist: " + gameTexture.getFilePath(), true);
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
            Log.println(getClass(), "Texture not loaded: " + gameTexture.getFilePath(), true);
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
            throw new RuntimeException("GameMap Doesn't exist: " + mapFilePath);
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
            Log.println(getClass(), "TiledMap not loaded: " + mapFilePath, true);
        }

        return tiledMap;
    }

    public void loadFont(GameFont gameFont) {
        // check if already loaded
        if (isFileLoaded(gameFont.getFilePath())) {
            Log.println(getClass(), "Sound already loaded: " + gameFont, true);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameFont.getFilePath()).exists()) {
            assetManager.setLoader(BitmapFont.class, new BitmapFontLoader(filePathResolver));
            assetManager.load(gameFont.getFilePath(), BitmapFont.class);
            assetManager.finishLoadingAsset(gameFont.getFilePath());
        } else {
            Log.println(getClass(), "Font doesn't exist: " + gameFont.getFilePath(), true);
        }

    }

    public BitmapFont getFont(GameFont gameFont) {
        BitmapFont bitmapFont = null;

        if (assetManager.isLoaded(gameFont.getFilePath())) {
            bitmapFont = assetManager.get(gameFont.getFilePath(), BitmapFont.class);
        } else {
            Log.println(getClass(), "Font not loaded: " + gameFont, true);
        }

        return bitmapFont;
    }

    public void loadAtlas(GameAtlas gameAtlas) {
        // check if already loaded
        if (isFileLoaded(gameAtlas.getFilePath())) {
            Log.println(getClass(), "Atlas already loaded: " + gameAtlas, true);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameAtlas.getFilePath()).exists()) {
            assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(filePathResolver));
            assetManager.load(gameAtlas.getFilePath(), TextureAtlas.class);
            assetManager.finishLoadingAsset(gameAtlas.getFilePath());
        } else {
            Log.println(getClass(), "Atlas doesn't exist: " + gameAtlas.getFilePath(), true);
        }
    }

    public TextureAtlas getAtlas(GameAtlas gameAtlas) {
        TextureAtlas textureAtlas = null;

        if (assetManager.isLoaded(gameAtlas.getFilePath())) {
            textureAtlas = assetManager.get(gameAtlas.getFilePath(), TextureAtlas.class);
        } else {
            Log.println(getClass(), "Atlas not loaded: " + gameAtlas.getFilePath(), true);
        }

        return textureAtlas;
    }

    public void loadPixmap(GamePixmap gamePixmap) {
        // check if already loaded
        if (isFileLoaded(gamePixmap.getFilePath())) {
            Log.println(getClass(), "Pixmap already loaded: " + gamePixmap.getFilePath(), true);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gamePixmap.getFilePath()).exists()) {
            assetManager.setLoader(Pixmap.class, new PixmapLoader(filePathResolver));
            assetManager.load(gamePixmap.getFilePath(), Pixmap.class);
            assetManager.finishLoadingAsset(gamePixmap.getFilePath());
        } else {
            Log.println(getClass(), "Pixmap doesn't exist: " + gamePixmap.getFilePath(), true);
        }
    }

    public Pixmap getPixmap(GamePixmap gamePixmap) {
        Pixmap pixmap = null;

        if (assetManager.isLoaded(gamePixmap.getFilePath())) {
            pixmap = assetManager.get(gamePixmap.getFilePath(), Pixmap.class);
        } else {
            Log.println(getClass(), "Pixmap not loaded: " + gamePixmap, true);
        }

        return pixmap;
    }
}
