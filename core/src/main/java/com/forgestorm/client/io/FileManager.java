package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.*;
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
import com.forgestorm.client.io.type.GameFont;
import com.forgestorm.client.io.type.GamePixmap;
import com.forgestorm.client.io.type.GameSkin;
import com.forgestorm.client.io.type.GameTexture;
import com.forgestorm.client.util.file.FindDesktopDirectoryUtil;
import com.forgestorm.shared.io.*;
import com.forgestorm.shared.io.type.GameAtlas;
import lombok.Getter;

import java.io.*;

import static com.forgestorm.client.util.Log.println;

public class FileManager {

    private static final boolean PRINT_DEBUG = false;

    @Getter
    private final String clientFilesDirectoryPath;
    @Getter
    private final String clientUpdaterJar;

    @Getter
    private final String worldDirectory;

    private final AssetManager assetManager = new AssetManager();
    private final FileHandleResolver internalResolver = new InternalFileHandleResolver();
    private final FileHandleResolver absoluteResolver = new AbsoluteFileHandleResolver();

    public FileManager() {
        // Set the client home directory
        File homeDirectory = FindDesktopDirectoryUtil.getDirectory();

        if (!homeDirectory.exists()) {
            if (homeDirectory.mkdirs()) {
                println(getClass(), "Created a ForgeStorm folder in the users home directory!", true);
            } else {
                throw new RuntimeException("Couldn't create the ForgeStorm in the home directory!");
            }
        } else {
            println(getClass(), "Home directory: " + homeDirectory.getAbsolutePath(), true);
        }
        clientFilesDirectoryPath = homeDirectory.getAbsolutePath();


        // Create the World Directory if it doesn't exist.
        File worldDirectory = new File(clientFilesDirectoryPath + "/worldDirectory/");
        if (!worldDirectory.exists()) {
            if (worldDirectory.mkdir()) {
                println(getClass(), "The World directory didn't exist so one was created.", true);
            } else {
                throw new RuntimeException("Couldn't create the World directory!");
            }
        }

        File gameWorldDirectory = new File(clientFilesDirectoryPath + "/worldDirectory/game_start/");
        if (!gameWorldDirectory.exists()) {
            if (gameWorldDirectory.mkdir()) {
                println(getClass(), "The game_start directory didn't exist so one was created.", true);
            } else {
                throw new RuntimeException("Couldn't create the game_start directory!");
            }
        }

        // Copy assets to client files directory
        try {
            copyResourceToFile("/tools/client-updater.jar", clientFilesDirectoryPath + "/client-updater.jar");
            copyResourceToFile("/data/maps/game_start/62.64.json", gameWorldDirectory.getAbsoluteFile() + "/62.64.json");
            copyResourceToFile("/data/maps/game_start/62.65.json", gameWorldDirectory.getAbsoluteFile() + "/62.65.json");
            copyResourceToFile("/data/maps/game_start/63.64.json", gameWorldDirectory.getAbsoluteFile() + "/63.64.json");
            copyResourceToFile("/data/maps/game_start/63.65.json", gameWorldDirectory.getAbsoluteFile() + "/63.65.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clientUpdaterJar = clientFilesDirectoryPath + "/client-updater.jar";

        this.worldDirectory = worldDirectory.getAbsolutePath();

        // Create Revision document and set the build to 0;
//        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return;
        println(getClass(), "ClientFilesDirectory: " + clientFilesDirectoryPath);
        String path = clientFilesDirectoryPath + "/Revision.txt";
        File revision = new File(path);
        if (!revision.exists()) {
            println(getClass(), "Creating Revision.txt document since it does not exist.");
            try {
                FileWriter myWriter = new FileWriter(path);
                myWriter.write("0");
                myWriter.close();
                println(getClass(), "Successfully wrote to the file.", false, PRINT_DEBUG);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void copyResourceToFile(String resourcePath, String outputFile) throws IOException {
        println(getClass(), "Copying file : " + resourcePath + " to: " + outputFile);
        try (InputStream in = FileManager.class.getResourceAsStream(resourcePath);
             OutputStream out = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * Wrapper method to dispose of all assets. Free's system resources.
     */
    public void dispose() {
        assetManager.dispose();
    }

    public boolean update() {
        return assetManager.update();
    }

    public float getProgress() {
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
        String filePath = clientFilesDirectoryPath + "/Revision.txt";
        abstractedLoad(filePath, true, true, RevisionDocumentLoader.RevisionDocumentWrapper.class, new RevisionDocumentLoader(absoluteResolver));
    }

    public RevisionDocumentLoader.RevisionDocumentWrapper getRevisionDocumentData() {
        String filePath = clientFilesDirectoryPath + "/Revision.txt";
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
        abstractedLoad(gameTexture.getFilePath(), false, false, Texture.class, new TextureLoader(internalResolver));
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

    public void loadAtlas(GameAtlas gameAtlas, boolean forceFinishLoading) {
        abstractedLoad(gameAtlas.getFilePath(), forceFinishLoading, false, TextureAtlas.class, new TextureAtlasLoader(internalResolver));
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
        abstractedLoad(FilePaths.ITEM_STACK.getInternalFilePath(), false, false, ItemStackLoader.ItemStackData.class, new ItemStackLoader(internalResolver));
    }

    public ItemStackLoader.ItemStackData getItemStackData() {
        return abstractGet(FilePaths.ITEM_STACK.getInternalFilePath(), false, ItemStackLoader.ItemStackData.class);
    }

    public void loadFactionData() {
        abstractedLoad(FilePaths.FACTIONS.getInternalFilePath(), false, false, FactionLoader.FactionDataWrapper.class, new FactionLoader(internalResolver));
    }

    public FactionLoader.FactionDataWrapper getFactionData() {
        return abstractGet(FilePaths.FACTIONS.getInternalFilePath(), false, FactionLoader.FactionDataWrapper.class);
    }

    public void loadAbilityData() {
        abstractedLoad(FilePaths.COMBAT_ABILITIES.getInternalFilePath(), false, false, AbilityLoader.AbilityDataWrapper.class, new AbilityLoader(internalResolver));
    }

    public AbilityLoader.AbilityDataWrapper getAbilityData() {
        return abstractGet(FilePaths.COMBAT_ABILITIES.getInternalFilePath(), false, AbilityLoader.AbilityDataWrapper.class);
    }

    public void loadMusicData() {
        abstractedLoad(FilePaths.GAME_MUSIC.getInternalFilePath(), false, false, MusicDataLoader.MusicDataWrapper.class, new MusicDataLoader(internalResolver));
    }

    public MusicDataLoader.MusicDataWrapper getMusicData() {
        return abstractGet(FilePaths.GAME_MUSIC.getInternalFilePath(), false, MusicDataLoader.MusicDataWrapper.class);
    }

    public void loadSoundData() {
        abstractedLoad(FilePaths.SOUND_FX.getInternalFilePath(), false, false, SoundDataLoader.SoundDataWrapper.class, new SoundDataLoader(internalResolver));
    }

    public SoundDataLoader.SoundDataWrapper getSoundData() {
        return abstractGet(FilePaths.SOUND_FX.getInternalFilePath(), false, SoundDataLoader.SoundDataWrapper.class);
    }

    public void loadEntityShopData() {
        abstractedLoad(FilePaths.ENTITY_SHOP.getInternalFilePath(), false, false, EntityShopLoader.EntityShopDataWrapper.class, new EntityShopLoader(internalResolver));
    }

    public EntityShopLoader.EntityShopDataWrapper getEntityShopData() {
        return abstractGet(FilePaths.ENTITY_SHOP.getInternalFilePath(), false, EntityShopLoader.EntityShopDataWrapper.class);
    }

    public void loadTilePropertiesData() {
        abstractedLoad(FilePaths.TILE_PROPERTIES.getInternalFilePath(), false, false, TilePropertiesLoader.TilePropertiesDataWrapper.class, new TilePropertiesLoader(internalResolver));
    }

    public TilePropertiesLoader.TilePropertiesDataWrapper getTilePropertiesData() {
        return abstractGet(FilePaths.TILE_PROPERTIES.getInternalFilePath(), false, TilePropertiesLoader.TilePropertiesDataWrapper.class);
    }

    public void loadTileAnimationData() {
        abstractedLoad(FilePaths.TILE_ANIMATIONS.getInternalFilePath(), false, false, TileAnimationsLoader.TileAnimationsDataWrapper.class, new TileAnimationsLoader(internalResolver));
    }

    public TileAnimationsLoader.TileAnimationsDataWrapper getTileAnimationData() {
        return abstractGet(FilePaths.TILE_ANIMATIONS.getInternalFilePath(), false, TileAnimationsLoader.TileAnimationsDataWrapper.class);
    }

    public void loadRegionData(String filePath) {
        abstractedLoad(filePath, true, false, RegionLoader.RegionDataWrapper.class, new RegionLoader(internalResolver));
    }

    public RegionLoader.RegionDataWrapper getRegionData(String filePath) {
        return abstractGet(filePath, false, RegionLoader.RegionDataWrapper.class);
    }

    public void loadNetworkSettingsData() {
        abstractedLoad(FilePaths.NETWORK_SETTINGS.getInternalFilePath(), false, false, NetworkSettingsLoader.NetworkSettingsData.class, new NetworkSettingsLoader(internalResolver));
    }

    public NetworkSettingsLoader.NetworkSettingsData getNetworkSettingsData() {
        return abstractGet(FilePaths.NETWORK_SETTINGS.getInternalFilePath(), false, NetworkSettingsLoader.NetworkSettingsData.class);
    }

    public void loadRssFeedData() {
        abstractedLoad(FilePaths.RSS_FEED.getInternalFilePath(), false, false, RssFeedLoader.RssFeedWrapper.class, new RssFeedLoader(internalResolver));
    }

    public RssFeedLoader.RssFeedWrapper getRssFeedData() {
        return abstractGet(FilePaths.RSS_FEED.getInternalFilePath(), false, RssFeedLoader.RssFeedWrapper.class);
    }

    public void loadGameWorldListData() {
        abstractedLoad(FilePaths.MAP_LIST.getInternalFilePath(), true, false, GameWorldListLoader.GameWorldListDataWrapper.class, new GameWorldListLoader(internalResolver));
    }

    public GameWorldListLoader.GameWorldListDataWrapper getGameWorldListData() {
        return abstractGet(FilePaths.MAP_LIST.getInternalFilePath(), false, GameWorldListLoader.GameWorldListDataWrapper.class);
    }

    public void loadGameWorldData(String worldName) {
        String filePath = FilePaths.MAP_DIRECTORY.getInternalFilePath() + "/" + worldName;
        abstractedLoad(filePath, false, false, GameWorldLoader.GameWorldDataWrapper.class, new GameWorldLoader(internalResolver));
    }

    public GameWorldLoader.GameWorldDataWrapper getGameWorldData(String worldName) {
        String filePath = FilePaths.MAP_DIRECTORY.getInternalFilePath() + "/" + worldName;
        return abstractGet(filePath, false, GameWorldLoader.GameWorldDataWrapper.class);
    }

    public void loadMapChunkData(String worldName, short chunkX, short chunkY, boolean forceFinishLoading) {
        String filePath = clientFilesDirectoryPath + File.separator + "worldDirectory" + File.separator + worldName + File.separator + chunkX + "." + chunkY + ".json";
        abstractedLoad(filePath, forceFinishLoading, true, ChunkLoader.WorldChunkDataWrapper.class, new ChunkLoader(absoluteResolver, worldName));
    }

    public ChunkLoader.WorldChunkDataWrapper getMapChunkData(String worldName, short chunkX, short chunkY) {
        String filePath = clientFilesDirectoryPath + File.separator + "worldDirectory" + File.separator + worldName + File.separator + chunkX + "." + chunkY + ".json";
        return abstractGet(filePath, true, ChunkLoader.WorldChunkDataWrapper.class);
    }

    public void unloadMapChunkData(String worldName, short chunkX, short chunkY) {
        String filePath = clientFilesDirectoryPath + File.separator + "worldDirectory" + File.separator + worldName + File.separator + chunkX + "." + chunkY + ".json";

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
            println(getClass(), "Loading File: " + filePath, false, PRINT_DEBUG);
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
