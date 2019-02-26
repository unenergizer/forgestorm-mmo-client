package com.valenguard.client.game.assets;

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
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import static com.valenguard.client.util.Log.println;

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
     * @param gameMusic The music file to load.
     */
    public void loadMusic(GameMusic gameMusic) {
        // check if already loaded
        if (isFileLoaded(gameMusic.getFilePath())) {
            println(getClass(), "Music already loaded: " + gameMusic.getFilePath(), true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameMusic.getFilePath()).exists()) {
            assetManager.setLoader(Music.class, new MusicLoader(filePathResolver));
            assetManager.load(gameMusic.getFilePath(), Music.class);
            assetManager.finishLoadingAsset(gameMusic.getFilePath());
        } else {
            println(getClass(), "Music doesn't exist: " + gameMusic.getFilePath(), true, PRINT_DEBUG);
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
            println(getClass(), "Music not loaded: " + gameMusic.getFilePath(), true, PRINT_DEBUG);
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
            println(getClass(), "Sound already loaded: " + gameSound.getFilePath(), true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(gameSound.getFilePath()).exists()) {
            assetManager.setLoader(Sound.class, new SoundLoader(filePathResolver));
            assetManager.load(gameSound.getFilePath(), Sound.class);
            assetManager.finishLoadingAsset(gameSound.getFilePath());
        } else {
            println(getClass(), "Sound doesn't exist: " + gameSound.getFilePath(), true, PRINT_DEBUG);
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
            println(getClass(), "Sound not loaded: " + gameSound.getFilePath(), true, PRINT_DEBUG);
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
            TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
            params.textureMinFilter = Texture.TextureFilter.Nearest;
            params.textureMagFilter = Texture.TextureFilter.Nearest;
            assetManager.setLoader(TiledMap.class, new TmxMapLoader(filePathResolver));
            assetManager.load(mapFilePath, TiledMap.class, params);
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
            println(getClass(), "TiledMap not loaded: " + mapFilePath, true, PRINT_DEBUG);
        }

        return tiledMap;
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
