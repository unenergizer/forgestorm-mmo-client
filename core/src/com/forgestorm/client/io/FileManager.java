package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

public class FileManager {

    private static final boolean PRINT_DEBUG = false;

    @Getter
    private final String clientFilesDirectory;
    @Getter
    private final String worldDirectory;

    @Getter
    private final AssetManager assetManager = new AssetManager();
    private final FileHandleResolver internalResolver = new InternalFileHandleResolver();
    private final FileHandleResolver absoluteResolver = new AbsoluteFileHandleResolver();

    public FileManager() {
        // Get the path of the jar file.
        String jarPath = FileManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        println(getClass(), jarPath);
        String decodedPath = "";
        try {
            decodedPath = URLDecoder.decode(jarPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Create the client files directory
        File clientFilesDirectory = new File(decodedPath + "clientFiles").getParentFile();
        if (!clientFilesDirectory.exists()) {
            if (clientFilesDirectory.mkdir()) {
                println(getClass(), "Created the ClientFilesOnly directory!", true);
            } else {
                throw new RuntimeException("Couldn't create the ClientFilesOnly directory!");
            }
        }
        this.clientFilesDirectory = clientFilesDirectory.getAbsolutePath();

        // Create the World Directory if it doesn't exist.
        File worldDirectory = new File(clientFilesDirectory + "/worldDirectory");
        if (!worldDirectory.exists()) {
            if (worldDirectory.mkdir()) {
                println(getClass(), "The World directory didn't exist so one was created.", true);
            } else {
                throw new RuntimeException("Couldn't create the World directory!");
            }
        }

        this.worldDirectory = worldDirectory.getAbsolutePath();

        // Create Revision document and set the build to 0;
//        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return;
        println(getClass(), "ClientFilesDirectory: " + clientFilesDirectory);
        String path = clientFilesDirectory + "/Revision.txt";
        File revision = new File(path);
        if (!revision.exists()) {
            println(getClass(), "Creating Revision.txt document since it does not exist.");
            try {
                FileWriter myWriter = new FileWriter(path);
                myWriter.write("0");
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

    public void loadRevisionDocumentData() {
        String filePath = clientFilesDirectory + "/Revision.txt";
        abstractedLoad(filePath, true, true, RevisionDocumentLoader.RevisionDocumentWrapper.class, new RevisionDocumentLoader(absoluteResolver));
    }

    public RevisionDocumentLoader.RevisionDocumentWrapper getRevisionDocumentData() {
        String filePath = clientFilesDirectory + "/Revision.txt";
        return abstractGet(filePath, true, RevisionDocumentLoader.RevisionDocumentWrapper.class);
    }

    /**
     * Attempts to load a music file.
     *
     * @param audioData The music file to load.
     */
    public void loadMusic(AudioData audioData) {
        String filePath = audioData.getAudioType().getFilePath() + audioData.getFileName();
        abstractedLoad(filePath, true, false, Music.class, new MusicLoader(internalResolver));
    }

    /**
     * Attempts to get a loaded music file from the asset manager.
     *
     * @param audioData The music to retrieve.
     * @return A music file.
     */
    public Music getMusic(AudioData audioData) {
        String filePath = audioData.getAudioType().getFilePath() + audioData.getFileName();
        return abstractGet(filePath, false, Music.class);
    }

    /**
     * Attempts to load a sound file.
     *
     * @param audioData The sound file to load.
     */
    public void loadSound(AudioData audioData) {
        String filePath = audioData.getAudioType().getFilePath() + audioData.getFileName();
        abstractedLoad(filePath, true, false, Sound.class, new SoundLoader(internalResolver));
    }

    /**
     * Attempts to get a loaded game sound from the asset manager.
     *
     * @param audioData The sound to retrieve.
     * @return A game sound.
     */
    public Sound getSound(AudioData audioData) {
        String filePath = audioData.getAudioType().getFilePath() + audioData.getFileName();
        return abstractGet(filePath, false, Sound.class);
    }

    /**
     * Attempts to load a texture file.
     *
     * @param gameTexture The texture file to load.
     */
    public void loadTexture(GameTexture gameTexture) {
        abstractedLoad(gameTexture.getFilePath(), true, false, Texture.class, new TextureLoader(internalResolver));
    }

    /**
     * Attempts to get a loaded texture from the asset manager.
     *
     * @param gameTexture The graphic to retrieve.
     * @return A texture.
     */
    public Texture getTexture(GameTexture gameTexture) {
        return abstractGet(gameTexture.getFilePath(), false, Texture.class);
    }

    public void loadFont(GameFont gameFont) {
        abstractedLoad(gameFont.getFilePath(), true, false, BitmapFont.class, new BitmapFontLoader(internalResolver));
    }

    public BitmapFont getFont(GameFont gameFont) {
        return abstractGet(gameFont.getFilePath(), false, BitmapFont.class);
    }

    public void loadAtlas(GameAtlas gameAtlas) {
        abstractedLoad(gameAtlas.getFilePath(), true, false, TextureAtlas.class, new TextureAtlasLoader(internalResolver));
    }

    public TextureAtlas getAtlas(GameAtlas gameAtlas) {
        return abstractGet(gameAtlas.getFilePath(), false, TextureAtlas.class);
    }

    public void loadPixmap(GamePixmap gamePixmap) {
        abstractedLoad(gamePixmap.getFilePath(), true, false, Pixmap.class, new PixmapLoader(internalResolver));
    }

    public Pixmap getPixmap(GamePixmap gamePixmap) {
        return abstractGet(gamePixmap.getFilePath(), false, Pixmap.class);
    }

    public void loadSkin(GameSkin gameSkin) {
        abstractedLoad(gameSkin.getFilePath(), true, false, Skin.class, new SkinLoader(internalResolver));
    }

    public Skin getSkin(GameSkin gameSkin) {
        return abstractGet(gameSkin.getFilePath(), false, Skin.class);
    }

    public void loadItemStackData() {
        abstractedLoad(FilePaths.ITEM_STACK.getFilePath(), false, false, ItemStackLoader.ItemStackData.class, new ItemStackLoader(internalResolver));
    }

    public ItemStackLoader.ItemStackData getItemStackData() {
        return abstractGet(FilePaths.ITEM_STACK.getFilePath(), false, ItemStackLoader.ItemStackData.class);
    }

    public void loadFactionData() {
        abstractedLoad(FilePaths.FACTIONS.getFilePath(), false, false, FactionLoader.FactionDataWrapper.class, new FactionLoader(internalResolver));
    }

    public FactionLoader.FactionDataWrapper getFactionData() {
        return abstractGet(FilePaths.FACTIONS.getFilePath(), false, FactionLoader.FactionDataWrapper.class);
    }

    public void loadAbilityData() {
        abstractedLoad(FilePaths.COMBAT_ABILITIES.getFilePath(), false, false, AbilityLoader.AbilityDataWrapper.class, new AbilityLoader(internalResolver));
    }

    public AbilityLoader.AbilityDataWrapper getAbilityData() {
        return abstractGet(FilePaths.COMBAT_ABILITIES.getFilePath(), false, AbilityLoader.AbilityDataWrapper.class);
    }

    public void loadMusicData() {
        abstractedLoad(FilePaths.GAME_MUSIC.getFilePath(), false, false, MusicDataLoader.MusicDataWrapper.class, new MusicDataLoader(internalResolver));
    }

    public MusicDataLoader.MusicDataWrapper getMusicData() {
        return abstractGet(FilePaths.GAME_MUSIC.getFilePath(), false, MusicDataLoader.MusicDataWrapper.class);
    }

    public void loadSoundData() {
        abstractedLoad(FilePaths.SOUND_FX.getFilePath(), false, false, SoundDataLoader.SoundDataWrapper.class, new SoundDataLoader(internalResolver));
    }

    public SoundDataLoader.SoundDataWrapper getSoundData() {
        return abstractGet(FilePaths.SOUND_FX.getFilePath(), false, SoundDataLoader.SoundDataWrapper.class);
    }

    public void loadEntityShopData() {
        abstractedLoad(FilePaths.ENTITY_SHOP.getFilePath(), false, false, EntityShopLoader.EntityShopDataWrapper.class, new EntityShopLoader(internalResolver));
    }

    public EntityShopLoader.EntityShopDataWrapper getEntityShopData() {
        return abstractGet(FilePaths.ENTITY_SHOP.getFilePath(), false, EntityShopLoader.EntityShopDataWrapper.class);
    }

    public void loadTilePropertiesData() {
        abstractedLoad(FilePaths.TILE_PROPERTIES.getFilePath(), false, false, TilePropertiesLoader.TilePropertiesDataWrapper.class, new TilePropertiesLoader(internalResolver));
    }

    public TilePropertiesLoader.TilePropertiesDataWrapper getTilePropertiesData() {
        return abstractGet(FilePaths.TILE_PROPERTIES.getFilePath(), false, TilePropertiesLoader.TilePropertiesDataWrapper.class);
    }

    public void loadWangPropertiesData() {
        abstractedLoad(FilePaths.WANG_PROPERTIES.getFilePath(), false, false, WangPropertiesLoader.WangPropertiesDataWrapper.class, new WangPropertiesLoader(internalResolver));
    }

    public WangPropertiesLoader.WangPropertiesDataWrapper getWangPropertiesData() {
        return abstractGet(FilePaths.WANG_PROPERTIES.getFilePath(), false, WangPropertiesLoader.WangPropertiesDataWrapper.class);
    }

    public void loadNetworkSettingsData() {
        abstractedLoad(FilePaths.NETWORK_SETTINGS.getFilePath(), false, false, NetworkSettingsLoader.NetworkSettingsData.class, new NetworkSettingsLoader(internalResolver));
    }

    public NetworkSettingsLoader.NetworkSettingsData getNetworkSettingsData() {
        return abstractGet(FilePaths.NETWORK_SETTINGS.getFilePath(), false, NetworkSettingsLoader.NetworkSettingsData.class);
    }

    public void loadRssFeedData() {
        abstractedLoad(FilePaths.RSS_FEED.getFilePath(), false, false, RssFeedLoader.RssFeedWrapper.class, new RssFeedLoader(internalResolver));
    }

    public RssFeedLoader.RssFeedWrapper getRssFeedData() {
        return abstractGet(FilePaths.RSS_FEED.getFilePath(), false, RssFeedLoader.RssFeedWrapper.class);
    }

    public void loadGameWorldListData() {
        abstractedLoad(FilePaths.MAP_LIST.getFilePath(), true, false, GameWorldListLoader.GameWorldListDataWrapper.class, new GameWorldListLoader(internalResolver));
    }

    public GameWorldListLoader.GameWorldListDataWrapper getGameWorldListData() {
        return abstractGet(FilePaths.MAP_LIST.getFilePath(), false, GameWorldListLoader.GameWorldListDataWrapper.class);
    }

    public void loadGameWorldData(String worldName) {
        String filePath = FilePaths.MAP_DIRECTORY.getFilePath() + "/" + worldName;
        abstractedLoad(filePath, true, false, GameWorldLoader.GameWorldDataWrapper.class, new GameWorldLoader(internalResolver));
    }

    public GameWorldLoader.GameWorldDataWrapper getGameWorldData(String worldName) {
        String filePath = FilePaths.MAP_DIRECTORY.getFilePath() + "/" + worldName;
        return abstractGet(filePath, false, GameWorldLoader.GameWorldDataWrapper.class);
    }

    public void loadMapChunkData(String worldName, short chunkX, short chunkY, boolean forceFinishLoading) {
        String filePath = FilePaths.MAP_DIRECTORY.getFilePath() + "/" + worldName + "/" + chunkX + "." + chunkY + ".json";
        abstractedLoad(filePath, forceFinishLoading, false, ChunkLoader.MapChunkDataWrapper.class, new ChunkLoader(internalResolver));
    }

    public ChunkLoader.MapChunkDataWrapper getMapChunkData(String worldName, short chunkX, short chunkY) {
        String filePath = FilePaths.MAP_DIRECTORY.getFilePath() + "/" + worldName + "/" + chunkX + "." + chunkY + ".json";
        return abstractGet(filePath, false, ChunkLoader.MapChunkDataWrapper.class);
    }

    public void unloadMapChunkData(String worldName, short chunkX, short chunkY) {
        String filePath = FilePaths.MAP_DIRECTORY.getFilePath() + "/" + worldName + "/" + chunkX + "." + chunkY + ".json";

        if (!isFileLoaded(filePath)) {
            println(getClass(), "MapChunkData does not exist for this chunk. ChunkX: " + chunkX + ", ChunkY: " + chunkY, true, PRINT_DEBUG);
            return;
        }

        assetManager.unload(filePath);
        if (assetManager.isLoaded(filePath)) {
            println(getClass(), "MapChunkData was not unloaded: " + filePath, true, PRINT_DEBUG);
        } else {
            println(getClass(), "MapChunkData was unloaded successfully! FilePath: " + filePath, true, PRINT_DEBUG);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private <T, P extends AssetLoaderParameters<T>> String abstractedLoad(File file, boolean forceFinishLoading, boolean useAbsolutePath, Class<T> type, AssetLoader<T, P> loader) {
        String path;
        if (useAbsolutePath) {
            path = getCanonicalPath(file);
        } else {
            path = file.getPath().replace("\\", "/");
        }

        return abstractedLoad(path, forceFinishLoading, useAbsolutePath, type, loader);
    }

    private <T, P extends AssetLoaderParameters<T>> String abstractedLoad(String filePath, boolean forceFinishLoading, boolean useAbsolutePath, Class<T> type, AssetLoader<T, P> loader) {
        FileHandleResolver fileHandleResolver;

        if (useAbsolutePath) {
            fileHandleResolver = absoluteResolver;
        } else {
            fileHandleResolver = internalResolver;
        }

        // Check if the file is already loaded
        if (isFileLoaded(filePath)) {
            println(getClass(), "File already loaded: " + filePath, true, PRINT_DEBUG);
            return null;
        }

        // Load the asset
        if (fileHandleResolver.resolve(filePath).exists()) {
            assetManager.setLoader(type, loader);
            assetManager.load(filePath, type);
            if (forceFinishLoading) assetManager.finishLoading();
        } else {
            println(getClass(), "File doesn't exist: " + filePath, true, PRINT_DEBUG);
        }
        return filePath;
    }

    @SuppressWarnings("SameParameterValue")
    private <T> T abstractGet(String path, boolean useAbsolutePath, Class<T> type) {
        String pathFixed = path;

        if (useAbsolutePath) {
            // Fixes problems with loading files from an "absolute path"...
            pathFixed = path.replace("\\", "/");
        }

        if (assetManager.isLoaded(pathFixed)) {
            return assetManager.get(pathFixed, type);
        } else {
            println(getClass(), "File not loaded: " + pathFixed, true, PRINT_DEBUG);
            return null;
        }
    }

    private String getCanonicalPath(File file) {
        String path = null;
        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
