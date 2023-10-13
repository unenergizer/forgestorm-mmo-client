package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.WorldObject;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.tile.Tile;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.client.io.RegionLoader;
import com.forgestorm.shared.game.world.maps.Floors;
import com.forgestorm.shared.game.world.maps.MoveDirection;
import com.forgestorm.shared.game.world.maps.Warp;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.io.ChunkLoader;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static com.forgestorm.client.util.Log.println;

@Getter
@Setter
public class GameWorld {

    private static final boolean PRINT_DEBUG = false;

    private final ClientMain clientMain;
    private final FileManager fileManager;

    private final String worldName;
    private final Color backgroundColor;

    private final Map<Integer, WorldChunk> worldChunkDrawMap = new HashMap<>();

    private Texture parallaxBackground;
    private int parallaxX, parallaxY;

    public GameWorld(ClientMain clientMain, String worldName, Color backgroundColor) {
        this.clientMain = clientMain;
        this.fileManager = clientMain.getFileManager();
        this.worldName = worldName;
        this.backgroundColor = backgroundColor;
    }

    public void loadAroundPlayer(PlayerClient playerClient) {
        int clientX = playerClient.getFutureMapLocation().getX();
        int clientY = playerClient.getFutureMapLocation().getY();

        int clientChunkX = (int) Math.floor(clientX / (float) ClientConstants.CHUNK_SIZE);
        int clientChunkY = (int) Math.floor(clientY / (float) ClientConstants.CHUNK_SIZE);

        for (int chunkY = clientChunkY - ClientConstants.CHUNK_RADIUS; chunkY < clientChunkY + ClientConstants.CHUNK_RADIUS + 1; chunkY++) {
            for (int chunkX = clientChunkX - ClientConstants.CHUNK_RADIUS; chunkX < clientChunkX + ClientConstants.CHUNK_RADIUS + 1; chunkX++) {
                fileManager.loadMapChunkData(worldName, (short) chunkX, (short) chunkY, true);
                ChunkLoader.WorldChunkDataWrapper mapChunkData = fileManager.getMapChunkData(worldName, (short) chunkX, (short) chunkY);
                if (mapChunkData != null) addChunkFromDisk(mapChunkData.getWorldChunkFromDisk());
            }
        }
    }

    public void playerChunkChange(PlayerClient playerClient) {
        MoveDirection moveDirection = playerClient.getFacingDirection();
        int clientX = playerClient.getFutureMapLocation().getX();
        int clientY = playerClient.getFutureMapLocation().getY();

        int clientChunkX = (int) Math.floor(clientX / (float) ClientConstants.CHUNK_SIZE);
        int clientChunkY = (int) Math.floor(clientY / (float) ClientConstants.CHUNK_SIZE);

        // Load and unload chunks based on direction
        switch (moveDirection) {
            case NORTH:
                for (int x = clientChunkX - ClientConstants.CHUNK_RADIUS; x <= clientChunkX + ClientConstants.CHUNK_RADIUS; x++) {
                    short chunkUnloadY = (short) (clientChunkY - ClientConstants.CHUNK_RADIUS - 1);
                    short chunkLoadY = (short) (clientChunkY + ClientConstants.CHUNK_RADIUS);
                    unloadChunk((short) x, chunkUnloadY);
                    loadChunk((short) x, chunkLoadY);
                    println(getClass(), "Direction: NORTH, PlayerChunkY: " + clientChunkY + ", ChunkUnloadY: " + chunkUnloadY + ", ChunkLoadY: " + chunkLoadY, false, PRINT_DEBUG);
                }
                break;
            case SOUTH:
                for (int x = clientChunkX - ClientConstants.CHUNK_RADIUS; x <= clientChunkX + ClientConstants.CHUNK_RADIUS; x++) {
                    short chunkUnloadY = (short) (clientChunkY + ClientConstants.CHUNK_RADIUS + 1);
                    short chunkLoadY = (short) (clientChunkY - ClientConstants.CHUNK_RADIUS);
                    unloadChunk((short) x, chunkUnloadY);
                    loadChunk((short) x, chunkLoadY);
                    println(getClass(), "Direction: SOUTH, PlayerChunkY: " + clientChunkY + ", ChunkUnloadY: " + chunkUnloadY + ", ChunkLoadY: " + chunkLoadY, false, PRINT_DEBUG);
                }
                break;
            case WEST:
                for (int y = clientChunkY - ClientConstants.CHUNK_RADIUS; y <= clientChunkY + ClientConstants.CHUNK_RADIUS; y++) {
                    short chunkUnloadX = (short) (clientChunkX + ClientConstants.CHUNK_RADIUS + 1);
                    short chunkLoadX = (short) (clientChunkX - ClientConstants.CHUNK_RADIUS);
                    unloadChunk(chunkUnloadX, (short) y);
                    loadChunk(chunkLoadX, (short) y);
                    println(getClass(), "Direction: WEST, PlayerChunkX: " + clientChunkX + ", ChunkUnloadX: " + chunkUnloadX + ", ChunkLoadX: " + chunkLoadX, false, PRINT_DEBUG);
                }
                break;
            case EAST:
                for (int y = clientChunkY - ClientConstants.CHUNK_RADIUS; y <= clientChunkY + ClientConstants.CHUNK_RADIUS; y++) {
                    short chunkUnloadX = (short) (clientChunkX - ClientConstants.CHUNK_RADIUS - 1);
                    short chunkLoadX = (short) (clientChunkX + ClientConstants.CHUNK_RADIUS);
                    unloadChunk(chunkUnloadX, (short) y);
                    loadChunk(chunkLoadX, (short) y);
                    println(getClass(), "Direction: EAST, PlayerChunkX: " + clientChunkX + ", ChunkUnloadX: " + chunkUnloadX + ", ChunkLoadX: " + chunkLoadX, false, PRINT_DEBUG);
                }
                break;
        }

    }

    public void loadRegions() {
        // Load game world regions
        String removeMe = "data/";
        String filePath = removeMe + "maps/" + worldName + "/Regions.yaml";
        fileManager.loadRegionData(filePath);

        // Set the gameworld regions
        RegionManager regionManager = clientMain.getRegionManager();
        RegionLoader.RegionDataWrapper regionDataWrapper = fileManager.getRegionData(filePath);
        Map<Integer, Region> regionMap = regionDataWrapper.getRegionMap();
        regionManager.setRegionMap(regionMap);
    }

    public void loadChunk(short chunkX, short chunkY) {
        fileManager.loadMapChunkData(worldName, chunkX, chunkY, true);
        ChunkLoader.WorldChunkDataWrapper mapChunkData = fileManager.getMapChunkData(worldName, chunkX, chunkY);
        if (mapChunkData != null) addChunkFromDisk(mapChunkData.getWorldChunkFromDisk());
    }

    public void unloadChunk(short chunkX, short chunkY) {
        // Remove from file manager
        fileManager.unloadMapChunkData(worldName, chunkX, chunkY);

        // Clear the chunk from the draw hashmap
        worldChunkDrawMap.remove((chunkX << 16) | (chunkY & 0xFFFF));
    }

    public Tile getTile(LayerDefinition layerDefinition, int worldX, int worldY, short worldZ) {
        WorldChunk worldChunk = findChunk(worldX, worldY);
        if (worldChunk == null) return null;

        int localX = worldX - worldChunk.getChunkX() * ClientConstants.CHUNK_SIZE;
        int localY = worldY - worldChunk.getChunkY() * ClientConstants.CHUNK_SIZE;

        return worldChunk.getTile(layerDefinition, localX, localY, Floors.getFloor(worldZ));
    }

    Warp getWarp(int entityX, int entityY, short entityZ) {
        WorldChunk worldChunk = findChunk(entityX, entityY);
        if (worldChunk == null) return null;

        int localX = entityX - worldChunk.getChunkX() * ClientConstants.CHUNK_SIZE;
        int localY = entityY - worldChunk.getChunkY() * ClientConstants.CHUNK_SIZE;

        return worldChunk.getWarp((short) localX, (short) localY, entityZ);
    }

    public WorldChunk findChunk(int entityX, int entityY) {

        // Convert world coordinates to chunk location
        int chunkX = (int) Math.floor(entityX / (float) ClientConstants.CHUNK_SIZE);
        int chunkY = (int) Math.floor(entityY / (float) ClientConstants.CHUNK_SIZE);

        for (WorldChunk worldChunk : worldChunkDrawMap.values()) {
            if (worldChunk.getChunkX() == chunkX && worldChunk.getChunkY() == chunkY) {
                return worldChunk;
            }
        }
        return null;
    }

    public void addChunkFromDisk(WorldChunk chunkFromDisk) {
        WorldChunk worldChunk = getChunk(chunkFromDisk.getChunkX(), chunkFromDisk.getChunkY());
        worldChunk.setChunkFromDisk(chunkFromDisk);
    }

    public WorldChunk getChunk(short chunkX, short chunkY) {
        WorldChunk worldChunk = worldChunkDrawMap.get((chunkX << 16) | (chunkY & 0xFFFF));

        // Create the chunk if it doesn't exist
        if (worldChunk == null) {
            worldChunk = new WorldChunk(clientMain, worldName, chunkX, chunkY);
            setChunk(worldChunk);
        }

        return worldChunk;
    }

    public boolean isSameChunk(WorldChunk chunk1, WorldChunk chunk2) {
        if (chunk1 == null) println(getClass(), "Chunk 1 null", false, PRINT_DEBUG);
        if (chunk2 == null) println(getClass(), "Chunk 2 null", false, PRINT_DEBUG);
        if (chunk1 == null || chunk2 == null) return false;
        return ((chunk1.getChunkX() << 16) | (chunk1.getChunkY() & 0xFFFF)) == ((chunk2.getChunkX() << 16) | (chunk2.getChunkY() & 0xFFFF));
    }

    private void setChunk(WorldChunk worldChunk) {
        worldChunkDrawMap.put((worldChunk.getChunkX() << 16) | (worldChunk.getChunkY() & 0xFFFF), worldChunk);
    }

    public void drawParallax(SpriteBatch spriteBatch) {
        if (parallaxBackground == null) return;
        parallaxX += 2;
        parallaxY -= 3;
        if (parallaxX >= parallaxBackground.getWidth()) parallaxX = 0;
        if (parallaxY <= -parallaxBackground.getHeight()) parallaxY = 0;
        spriteBatch.draw(parallaxBackground, -parallaxBackground.getWidth(), -parallaxBackground.getHeight(),
                parallaxX, parallaxY,
                Gdx.graphics.getWidth() + parallaxBackground.getWidth() * 2,
                Gdx.graphics.getHeight() + parallaxBackground.getHeight() * 2);
    }

    public void renderBottomLayers(Batch batch, Floors floor) {
        // TODO: Check against camera before trying to render
        // Render layer from most bottom, going up.
        for (WorldChunk chunk : worldChunkDrawMap.values()) {
            chunk.renderBottomLayers(batch, floor);
        }
    }

    public void renderDecorationLayer(Batch batch, Floors floor) {
        for (WorldChunk chunk : worldChunkDrawMap.values()) {
            chunk.renderDecorationLayer(batch, floor);
        }
    }

    public void renderOverheadLayer(Batch batch, Floors floor) {
        for (WorldChunk chunk : worldChunkDrawMap.values()) {
            chunk.renderOverheadLayer(batch, floor);
        }
    }

    public void getSortableWorldObjects(PriorityQueue<WorldObject> worldObjectList, Floors floor) {
        for (WorldChunk chunk : worldChunkDrawMap.values()) {
            Tile[] sortableTiles = chunk.getSortableTiles(floor);
            if (sortableTiles == null) continue;

            worldObjectList.addAll(Arrays.asList(sortableTiles));
        }
    }

    void clearData() {
        // Remove world chunks from asset manager
        for (WorldChunk worldChunk : worldChunkDrawMap.values()) {
            getFileManager().unloadMapChunkData(worldName, worldChunk.getChunkX(), worldChunk.getChunkY());
        }

        // Clear arrays and maps
        worldChunkDrawMap.clear();
    }
}
