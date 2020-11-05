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
import com.forgestorm.client.io.updater.RssFeedLoader;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

@SuppressWarnings("unused")
public class FileManager {

    private static final boolean PRINT_DEBUG = false;

    @Getter
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

    public void loadItemStackData() {
        String filePath = FilePaths.ITEM_STACK.getFilePath();

        // check if already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "ItemStackData already loaded: " + filePath, true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(filePath).exists()) {
            assetManager.setLoader(ItemStackLoader.ItemStackData.class, new ItemStackLoader(filePathResolver));
            assetManager.load(filePath, ItemStackLoader.ItemStackData.class);
        } else {
            println(getClass(), "ItemStackData doesn't exist: " + filePath, true, PRINT_DEBUG);
        }
    }

    public ItemStackLoader.ItemStackData getItemStackData() {
        String filePath = FilePaths.ITEM_STACK.getFilePath();
        ItemStackLoader.ItemStackData data = null;

        if (assetManager.isLoaded(filePath)) {
            data = assetManager.get(filePath, ItemStackLoader.ItemStackData.class);
        } else {
            println(getClass(), "ItemStackData not loaded: " + filePath, true, PRINT_DEBUG);
        }

        return data;
    }

    public void loadFactionData() {
        String filePath = FilePaths.FACTIONS.getFilePath();

        // check if already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "FactionData already loaded: " + filePath, true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(filePath).exists()) {
            assetManager.setLoader(FactionLoader.FactionDataWrapper.class, new FactionLoader(filePathResolver));
            assetManager.load(filePath, FactionLoader.FactionDataWrapper.class);
        } else {
            println(getClass(), "FactionData doesn't exist: " + filePath, true, PRINT_DEBUG);
        }
    }

    public FactionLoader.FactionDataWrapper getFactionData() {
        String filePath = FilePaths.FACTIONS.getFilePath();
        FactionLoader.FactionDataWrapper data = null;

        if (assetManager.isLoaded(filePath)) {
            data = assetManager.get(filePath, FactionLoader.FactionDataWrapper.class);
        } else {
            println(getClass(), "FactionData not loaded: " + filePath, true, PRINT_DEBUG);
        }

        return data;
    }

    public void loadAbilityData() {
        String filePath = FilePaths.COMBAT_ABILITIES.getFilePath();

        // check if already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "AbilityData already loaded: " + filePath, true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(filePath).exists()) {
            assetManager.setLoader(AbilityLoader.AbilityDataWrapper.class, new AbilityLoader(filePathResolver));
            assetManager.load(filePath, AbilityLoader.AbilityDataWrapper.class);
        } else {
            println(getClass(), "AbilityData doesn't exist: " + filePath, true, PRINT_DEBUG);
        }
    }

    public AbilityLoader.AbilityDataWrapper getAbilityData() {
        String filePath = FilePaths.COMBAT_ABILITIES.getFilePath();
        AbilityLoader.AbilityDataWrapper data = null;

        if (assetManager.isLoaded(filePath)) {
            data = assetManager.get(filePath, AbilityLoader.AbilityDataWrapper.class);
        } else {
            println(getClass(), "AbilityData not loaded: " + filePath, true, PRINT_DEBUG);
        }

        return data;
    }

    public void loadMusicData() {
        String filePath = FilePaths.GAME_MUSIC.getFilePath();

        // check if already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "MusicData already loaded: " + filePath, true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(filePath).exists()) {
            assetManager.setLoader(MusicDataLoader.MusicDataWrapper.class, new MusicDataLoader(filePathResolver));
            assetManager.load(filePath, MusicDataLoader.MusicDataWrapper.class);
        } else {
            println(getClass(), "MusicData doesn't exist: " + filePath, true, PRINT_DEBUG);
        }
    }

    public MusicDataLoader.MusicDataWrapper getMusicData() {
        String filePath = FilePaths.GAME_MUSIC.getFilePath();
        MusicDataLoader.MusicDataWrapper data = null;

        if (assetManager.isLoaded(filePath)) {
            data = assetManager.get(filePath, MusicDataLoader.MusicDataWrapper.class);
        } else {
            println(getClass(), "MusicData not loaded: " + filePath, true, PRINT_DEBUG);
        }

        return data;
    }

    public void loadSoundData() {
        String filePath = FilePaths.SOUND_FX.getFilePath();

        // check if already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "SoundData already loaded: " + filePath, true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(filePath).exists()) {
            assetManager.setLoader(SoundDataLoader.SoundDataWrapper.class, new SoundDataLoader(filePathResolver));
            assetManager.load(filePath, SoundDataLoader.SoundDataWrapper.class);
        } else {
            println(getClass(), "SoundData doesn't exist: " + filePath, true, PRINT_DEBUG);
        }
    }

    public SoundDataLoader.SoundDataWrapper getSoundData() {
        String filePath = FilePaths.SOUND_FX.getFilePath();
        SoundDataLoader.SoundDataWrapper data = null;

        if (assetManager.isLoaded(filePath)) {
            data = assetManager.get(filePath, SoundDataLoader.SoundDataWrapper.class);
        } else {
            println(getClass(), "SoundData not loaded: " + filePath, true, PRINT_DEBUG);
        }

        return data;
    }

    public void loadEntityShopData() {
        String filePath = FilePaths.ENTITY_SHOP.getFilePath();

        // check if already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "EntityShopData already loaded: " + filePath, true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(filePath).exists()) {
            assetManager.setLoader(EntityShopLoader.EntityShopDataWrapper.class, new EntityShopLoader(filePathResolver));
            assetManager.load(filePath, EntityShopLoader.EntityShopDataWrapper.class);
        } else {
            println(getClass(), "EntityShopData doesn't exist: " + filePath, true, PRINT_DEBUG);
        }
    }

    public EntityShopLoader.EntityShopDataWrapper getEntityShopData() {
        String filePath = FilePaths.ENTITY_SHOP.getFilePath();
        EntityShopLoader.EntityShopDataWrapper data = null;

        if (assetManager.isLoaded(filePath)) {
            data = assetManager.get(filePath, EntityShopLoader.EntityShopDataWrapper.class);
        } else {
            println(getClass(), "EntityShopData not loaded: " + filePath, true, PRINT_DEBUG);
        }

        return data;
    }

    public void loadTilePropertiesData() {
        String filePath = FilePaths.TILE_PROPERTIES.getFilePath();

        // check if already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "TilePropertiesData already loaded: " + filePath, true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(filePath).exists()) {
            assetManager.setLoader(TilePropertiesLoader.TilePropertiesDataWrapper.class, new TilePropertiesLoader(filePathResolver));
            assetManager.load(filePath, TilePropertiesLoader.TilePropertiesDataWrapper.class);
        } else {
            println(getClass(), "TilePropertiesData doesn't exist: " + filePath, true, PRINT_DEBUG);
        }
    }

    public TilePropertiesLoader.TilePropertiesDataWrapper getTilePropertiesData() {
        String filePath = FilePaths.TILE_PROPERTIES.getFilePath();
        TilePropertiesLoader.TilePropertiesDataWrapper data = null;

        if (assetManager.isLoaded(filePath)) {
            data = assetManager.get(filePath, TilePropertiesLoader.TilePropertiesDataWrapper.class);
        } else {
            println(getClass(), "TilePropertiesData not loaded: " + filePath, true, PRINT_DEBUG);
        }

        return data;
    }

    public void loadNetworkSettingsData() {
        String filePath = FilePaths.NETWORK_SETTINGS.getFilePath();

        // check if already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "NetworkSettingsData already loaded: " + filePath, true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(filePath).exists()) {
            assetManager.setLoader(NetworkSettingsLoader.NetworkSettingsData.class, new NetworkSettingsLoader(filePathResolver));
            assetManager.load(filePath, NetworkSettingsLoader.NetworkSettingsData.class);
        } else {
            println(getClass(), "NetworkSettingsData doesn't exist: " + filePath, true, PRINT_DEBUG);
        }
    }

    public NetworkSettingsLoader.NetworkSettingsData getNetworkSettingsData() {
        String filePath = FilePaths.NETWORK_SETTINGS.getFilePath();
        NetworkSettingsLoader.NetworkSettingsData data = null;

        if (assetManager.isLoaded(filePath)) {
            data = assetManager.get(filePath, NetworkSettingsLoader.NetworkSettingsData.class);
        } else {
            println(getClass(), "NetworkSettingsData not loaded: " + filePath, true, PRINT_DEBUG);
        }

        return data;
    }

    public void loadGameMapData() {
        String filePath = FilePaths.MAPS.getFilePath();

        // check if already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "GameMapData already loaded: " + filePath, true, PRINT_DEBUG);
            return;
        }

        // load asset
        if (filePathResolver.resolve(filePath).exists()) {
            assetManager.setLoader(GameMapLoader.GameMapDataWrapper.class, new GameMapLoader(filePathResolver));
            assetManager.load(filePath, GameMapLoader.GameMapDataWrapper.class);
        } else {
            println(getClass(), "GameMapData doesn't exist: " + filePath, true, PRINT_DEBUG);
        }
    }

    public GameMapLoader.GameMapDataWrapper getGameMapData() {
        String filePath = FilePaths.MAPS.getFilePath();
        GameMapLoader.GameMapDataWrapper data = null;

        if (assetManager.isLoaded(filePath)) {
            data = assetManager.get(filePath, GameMapLoader.GameMapDataWrapper.class);
        } else {
            println(getClass(), "GameMapData not loaded: " + filePath, true, PRINT_DEBUG);
        }

        return data;
    }

    public void loadRssFeedData() {
        String filePath = FilePaths.RSS_FEED.getFilePath();

        // check if already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "RssFeedData already loaded: " + filePath, true, true);
            return;
        }

        // load asset
        if (filePathResolver.resolve(filePath).exists()) {
            assetManager.setLoader(RssFeedLoader.RssFeedWrapper.class, new RssFeedLoader(filePathResolver));
            assetManager.load(filePath, RssFeedLoader.RssFeedWrapper.class);
        } else {
            println(getClass(), "RssFeedData doesn't exist: " + filePath, true, true);
        }
    }

    public RssFeedLoader.RssFeedWrapper getRssFeedData() {
        String filePath = FilePaths.RSS_FEED.getFilePath();
        RssFeedLoader.RssFeedWrapper data = null;

        if (assetManager.isLoaded(filePath)) {
            data = assetManager.get(filePath, RssFeedLoader.RssFeedWrapper.class);
        } else {
            println(getClass(), "RssFeedData not loaded: " + filePath, true, true);
        }

        return data;
    }
}
