package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.io.ChunkLoader;
import com.forgestorm.client.io.FileManager;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameWorld {

    private final String worldName;
    private final int worldWidthInChunks;
    private final int worldHeightInChunks;
    private final Color backgroundColor;

    private final Map<Integer, WorldChunk> worldChunkMap = new HashMap<Integer, WorldChunk>();

    private Texture parallaxBackground;
    private int parallaxX, parallaxY;

    public GameWorld(String worldName, int worldWidthInChunks, int worldHeightInChunks, Color backgroundColor) {
        this.worldName = worldName;
        this.worldWidthInChunks = worldWidthInChunks;
        this.worldHeightInChunks = worldHeightInChunks;
        this.backgroundColor = backgroundColor;
    }

    public void loadAroundPlayer(PlayerClient client) {
        FileManager fileManager = ClientMain.getInstance().getFileManager();

        int clientX = client.getFutureMapLocation().getX();
        int clientY = client.getFutureMapLocation().getY();

        int clientChunkX = (int) Math.floor(clientX / (float) ClientConstants.CHUNK_SIZE);
        int clientChunkY = (int) Math.floor(clientY / (float) ClientConstants.CHUNK_SIZE);

        for (int chunkY = clientChunkY - ClientConstants.CHUNK_RADIUS; chunkY < clientChunkY + ClientConstants.CHUNK_RADIUS + 1; chunkY++) {
            for (int chunkX = clientChunkX - ClientConstants.CHUNK_RADIUS; chunkX < clientChunkX + ClientConstants.CHUNK_RADIUS + 1; chunkX++) {
                fileManager.loadMapChunkData(worldName, (short) chunkX, (short) chunkY, true);
                ChunkLoader.MapChunkDataWrapper mapChunkData = fileManager.getMapChunkData(worldName, (short) chunkX, (short) chunkY);
                if (mapChunkData != null) setChunk(mapChunkData.getWorldChunk());
            }
        }
    }

    public void setChunk(WorldChunk worldChunk) {
        worldChunkMap.put((worldChunk.getChunkX() << 16) | (worldChunk.getChunkY() & 0xFFFF), worldChunk);
    }

    public void setTile(LayerDefinition layerDefinition, Tile tile, int worldX, int worldY) {
        WorldChunk worldChunk = findChunk(worldX, worldY);
        if (worldChunk == null) return;

        int localX = worldX - worldChunk.getChunkX() * ClientConstants.CHUNK_SIZE;
        int localY = worldY - worldChunk.getChunkY() * ClientConstants.CHUNK_SIZE;

        worldChunk.setTile(layerDefinition, tile, localX, localY);
    }

    public Tile getTile(LayerDefinition layerDefinition, int worldX, int worldY) {
        WorldChunk worldChunk = findChunk(worldX, worldY);
        if (worldChunk == null) return null;

        int localX = worldX - worldChunk.getChunkX() * ClientConstants.CHUNK_SIZE;
        int localY = worldY - worldChunk.getChunkY() * ClientConstants.CHUNK_SIZE;

        return worldChunk.getTile(layerDefinition, localX, localY);
    }

    Warp getWarp(int entityX, int entityY) {
        WorldChunk worldChunk = findChunk(entityX, entityY);
        if (worldChunk == null) return null;

        int localX = entityX - worldChunk.getChunkX() * ClientConstants.CHUNK_SIZE;
        int localY = entityY - worldChunk.getChunkY() * ClientConstants.CHUNK_SIZE;

        return worldChunk.getWarp((short) localX, (short) localY);
    }

    public WorldChunk findChunk(int entityX, int entityY) {

        // Convert world coordinates to chunk location
        int chunkX = (int) Math.floor(entityX / (float) ClientConstants.CHUNK_SIZE);
        int chunkY = (int) Math.floor(entityY / (float) ClientConstants.CHUNK_SIZE);

        for (WorldChunk worldChunk : worldChunkMap.values()) {
            if (worldChunk.getChunkX() == chunkX && worldChunk.getChunkY() == chunkY) {
                return worldChunk;
            }
        }
        return null;
    }

    public WorldChunk getChunk(short chunkX, short chunkY) {
        return worldChunkMap.get((chunkX << 16) | (chunkY & 0xFFFF));
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

    public void renderBottomLayers(Batch batch) {
        // TODO: Check against camera before trying to render
        // Render layer from most bottom, going up.
        for (WorldChunk chunk : worldChunkMap.values()) {
            chunk.renderBottomLayers(batch);
        }
    }

    public void renderDecorationLayer(Batch batch) {
        for (WorldChunk chunk : worldChunkMap.values()) {
            chunk.renderDecorationLayer(batch);
        }
    }

    public void renderOverheadLayer(Batch batch) {
        for (WorldChunk chunk : worldChunkMap.values()) {
            chunk.renderOverheadLayer(batch);
        }
    }

    void clearData() {
        // Remove world chunks from asset manager
        for (WorldChunk worldChunk : worldChunkMap.values()) {
            ClientMain.getInstance().getFileManager().unloadMapChunkData(worldName, worldChunk.getChunkX(), worldChunk.getChunkY());
        }

        // Clear arrays and maps
        worldChunkMap.clear();
    }
}