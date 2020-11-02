package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.forgestorm.client.game.audio.AudioData;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.io.type.GameFont;
import com.forgestorm.client.io.type.GamePixmap;
import com.forgestorm.client.io.type.GameSkin;
import com.forgestorm.client.io.type.GameTexture;

import static com.forgestorm.client.util.Log.println;

@SuppressWarnings("unused")
public class FileManager {

    private static final boolean PRINT_DEBUG = false;

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
            println(getClass(), "Asset " + filePath + " not loaded. Nothing to unload.", true, PRINT_DEBUG);
        }
    }

    /**
     * Attempts to load a music file.
     *
     * @param audioData The music file to load.
     */
    public void loadMusic(AudioData audioData) {
        String filePath = audioData.getAudioType().getFilePath() + audioData.getFileName();
        // check if already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "Music already loaded: " + filePath, true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(filePath).exists()) {
            assetManager.setLoader(Music.class, new MusicLoader(filePathResolver));
            assetManager.load(filePath, Music.class);
            assetManager.finishLoadingAsset(filePath);
        } else {
            println(getClass(), "Music doesn't exist: " + filePath, true, PRINT_DEBUG);
        }
    }

    /**
     * Attempts to get a loaded music file from the asset manager.
     *
     * @param audioData The music to retrieve.
     * @return A music file.
     */
    public Music getMusic(AudioData audioData) {
        String filePath = audioData.getAudioType().getFilePath() + audioData.getFileName();
        Music music = null;

        if (assetManager.isLoaded(filePath)) {
            music = assetManager.get(filePath, Music.class);
        } else {
            println(getClass(), "Music not loaded: " + filePath, true, PRINT_DEBUG);
        }

        return music;
    }

    /**
     * Attempts to load a sound file.
     *
     * @param audioData The sound file to load.
     */
    public void loadSound(AudioData audioData) {
        String filePath = audioData.getAudioType().getFilePath() + audioData.getFileName();
        // check if already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "Sound already loaded: " + filePath, true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(filePath).exists()) {
            assetManager.setLoader(Sound.class, new SoundLoader(filePathResolver));
            assetManager.load(filePath, Sound.class);
            assetManager.finishLoadingAsset(filePath);
        } else {
            println(getClass(), "Sound doesn't exist: " + filePath, true, PRINT_DEBUG);
        }

    }

    /**
     * Attempts to get a loaded game sound from the asset manager.
     *
     * @param audioData The sound to retrieve.
     * @return A game sound.
     */
    public Sound getSound(AudioData audioData) {
        String filePath = audioData.getAudioType().getFilePath() + audioData.getFileName();
        Sound sound = null;

        if (assetManager.isLoaded(filePath)) {
            sound = assetManager.get(filePath, Sound.class);
        } else {
            println(getClass(), "Sound not loaded: " + filePath, true, PRINT_DEBUG);
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
            println(getClass(), "Texture already loaded: " + gameTexture.getFilePath(), true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameTexture.getFilePath()).exists()) {
            assetManager.setLoader(Texture.class, new TextureLoader(filePathResolver));
            assetManager.load(gameTexture.getFilePath(), Texture.class);
            assetManager.finishLoadingAsset(gameTexture.getFilePath());
        } else {
            println(getClass(), "Texture doesn't exist: " + gameTexture.getFilePath(), true, PRINT_DEBUG);
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
            println(getClass(), "Texture not loaded: " + gameTexture.getFilePath(), true, PRINT_DEBUG);
        }

        return texture;
    }

    public void loadFont(GameFont gameFont) {
        // check if already loaded
        if (isFileLoaded(gameFont.getFilePath())) {
            println(getClass(), "Sound already loaded: " + gameFont.getFilePath(), true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameFont.getFilePath()).exists()) {
            assetManager.setLoader(BitmapFont.class, new BitmapFontLoader(filePathResolver));
            assetManager.load(gameFont.getFilePath(), BitmapFont.class);
            assetManager.finishLoadingAsset(gameFont.getFilePath());
        } else {
            println(getClass(), "Font doesn't exist: " + gameFont.getFilePath(), true, PRINT_DEBUG);
        }

    }

    public BitmapFont getFont(GameFont gameFont) {
        BitmapFont bitmapFont = null;

        if (assetManager.isLoaded(gameFont.getFilePath())) {
            bitmapFont = assetManager.get(gameFont.getFilePath(), BitmapFont.class);
        } else {
            println(getClass(), "Font not loaded: " + gameFont.getFilePath(), true, PRINT_DEBUG);
        }

        return bitmapFont;
    }

    public void loadAtlas(GameAtlas gameAtlas) {
        // check if already loaded
        if (isFileLoaded(gameAtlas.getFilePath())) {
            println(getClass(), "Atlas already loaded: " + gameAtlas.getFilePath(), true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameAtlas.getFilePath()).exists()) {
            assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(filePathResolver));
            assetManager.load(gameAtlas.getFilePath(), TextureAtlas.class);
            assetManager.finishLoadingAsset(gameAtlas.getFilePath());
        } else {
            println(getClass(), "Atlas doesn't exist: " + gameAtlas.getFilePath(), true, PRINT_DEBUG);
        }
    }

    public TextureAtlas getAtlas(GameAtlas gameAtlas) {
        TextureAtlas textureAtlas = null;

        if (assetManager.isLoaded(gameAtlas.getFilePath())) {
            textureAtlas = assetManager.get(gameAtlas.getFilePath(), TextureAtlas.class);
        } else {
            println(getClass(), "Atlas not loaded: " + gameAtlas.getFilePath(), true, PRINT_DEBUG);
        }

        return textureAtlas;
    }

    public void loadPixmap(GamePixmap gamePixmap) {
        // check if already loaded
        if (isFileLoaded(gamePixmap.getFilePath())) {
            println(getClass(), "Pixmap already loaded: " + gamePixmap.getFilePath(), true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gamePixmap.getFilePath()).exists()) {
            assetManager.setLoader(Pixmap.class, new PixmapLoader(filePathResolver));
            assetManager.load(gamePixmap.getFilePath(), Pixmap.class);
            assetManager.finishLoadingAsset(gamePixmap.getFilePath());
        } else {
            println(getClass(), "Pixmap doesn't exist: " + gamePixmap.getFilePath(), true, PRINT_DEBUG);
        }
    }

    public Pixmap getPixmap(GamePixmap gamePixmap) {
        Pixmap pixmap = null;

        if (assetManager.isLoaded(gamePixmap.getFilePath())) {
            pixmap = assetManager.get(gamePixmap.getFilePath(), Pixmap.class);
        } else {
            println(getClass(), "Pixmap not loaded: " + gamePixmap.getFilePath(), true, PRINT_DEBUG);
        }

        return pixmap;
    }

    public void loadSkin(GameSkin gameSkin) {
        // check if already loaded
        if (isFileLoaded(gameSkin.getFilePath())) {
            println(getClass(), "GameSkin already loaded: " + gameSkin.getFilePath(), true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameSkin.getFilePath()).exists()) {
            assetManager.setLoader(Skin.class, new SkinLoader(filePathResolver));
            assetManager.load(gameSkin.getFilePath(), Skin.class);
            assetManager.finishLoadingAsset(gameSkin.getFilePath());
        } else {
            println(getClass(), "GameSkin doesn't exist: " + gameSkin.getFilePath(), true, PRINT_DEBUG);
        }
    }

    public Skin getSkin(GameSkin gameSkin) {
        Skin skin = null;

        if (assetManager.isLoaded(gameSkin.getFilePath())) {
            skin = assetManager.get(gameSkin.getFilePath(), Skin.class);
        } else {
            println(getClass(), "Skin not loaded: " + gameSkin.getFilePath(), true, PRINT_DEBUG);
        }

        return skin;
    }
}
