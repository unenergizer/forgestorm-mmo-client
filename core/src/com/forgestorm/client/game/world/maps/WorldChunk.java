package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.client.io.type.GameAtlas;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class WorldChunk {

    private final WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();

    @Getter
    private final String worldName;

    @Getter
    private final short chunkX, chunkY;

    @Getter
    private final Map<LayerDefinition, Tile[]> layers = new HashMap<LayerDefinition, Tile[]>();

    @Getter
    private final Map<WarpLocation, Warp> tileWarps = new HashMap<WarpLocation, Warp>();

    public WorldChunk(String worldName, short chunkX, short chunkY) {
        this.worldName = worldName;
        this.chunkX = chunkX;
        this.chunkY = chunkY;

        initTileLayers();
    }

    private void initTileLayers() {

        for (LayerDefinition layerDefinition : LayerDefinition.values()) {

            Tile[] tiles = new Tile[ClientConstants.CHUNK_SIZE * ClientConstants.CHUNK_SIZE];

            // Initialize all tiles
            for (int localX = 0; localX < ClientConstants.CHUNK_SIZE; localX++) {
                for (int localY = 0; localY < ClientConstants.CHUNK_SIZE; localY++) {

                    tiles[localX + localY * ClientConstants.CHUNK_SIZE] = new Tile(layerDefinition,
                            worldName,
                            localX + chunkX * ClientConstants.CHUNK_SIZE,
                            localY + chunkY * ClientConstants.CHUNK_SIZE);
                }
            }

            layers.put(layerDefinition, tiles);
        }
    }

    public void setChunkFromDisk(WorldChunk chunkFromDisk) {
        // Copy layers
        for (Map.Entry<LayerDefinition, Tile[]> entry : chunkFromDisk.getLayers().entrySet()) {
            LayerDefinition layerDefinition = entry.getKey();
            Tile[] tiles = entry.getValue();

            for (Tile tileFromDisk : tiles) {
                if (tileFromDisk.getTileImage() == null) continue;
                int localTileX = tileFromDisk.getWorldX() - ClientConstants.CHUNK_SIZE * chunkX;
                int localTileY = tileFromDisk.getWorldY() - ClientConstants.CHUNK_SIZE * chunkY;
                Tile localTile = getTile(layerDefinition, localTileX, localTileY);
                localTile.setTileImage(tileFromDisk.getTileImage());
            }
        }

        // Copy Warps
        for (Map.Entry<WarpLocation, Warp> entry : chunkFromDisk.getTileWarps().entrySet()) {
            WarpLocation warpLocation = entry.getKey();
            Warp warp = entry.getValue();

            addTileWarp(warpLocation, warp);
        }
    }

    public void setNetworkTiles(LayerDefinition layerDefinition, byte section, int[] tileImageIDs) {
        for (int localX = 0; localX < tileImageIDs.length; localX++) {

            TileImage tileImage = worldBuilder.getTileImage(tileImageIDs[localX]);

            // Set TileImage if applicable.
            if (tileImage != null) {
                //noinspection UnnecessaryLocalVariable
                int localY = section; // Defined for readability...
                Tile tile = getTile(layerDefinition, localX, localY);
                tile.setTileImage(new TileImage(tileImage));
            }
        }
    }

    public Tile getTile(LayerDefinition layerDefinition, int localX, int localY) {
        return layers.get(layerDefinition)[localX + localY * ClientConstants.CHUNK_SIZE];
    }

    public boolean isTraversable(int localX, int localY) {
        // We only have collision on two layers. (Removed looping through all tiles)
        if (!isTraversable(LayerDefinition.COLLIDABLES, localX, localY)) return false;
        return isTraversable(LayerDefinition.GROUND_DECORATION, localX, localY);
    }

    private boolean isTraversable(LayerDefinition layerDefinition, int localX, int localY) {
        Tile[] tiles = layers.get(layerDefinition);
        Tile tile = tiles[localX + localY * ClientConstants.CHUNK_SIZE];
        if (tile == null) return true;
        if (!ClientMain.getInstance().getDoorManager().isDoorwayTraversable(tile)) return false;
        return !tile.hasCollision();
    }

    public void clearTileWarps() {
        tileWarps.clear();
    }

    public void addTileWarp(int localX, int localY, Warp warp) {
        addTileWarp(new WarpLocation(localX, localY), warp);
    }

    public void addTileWarp(WarpLocation warpLocation, Warp warp) {
        tileWarps.put(warpLocation, warp);
    }

    Warp getWarp(int localX, int localY) {
        for (Map.Entry<WarpLocation, Warp> entry : tileWarps.entrySet()) {
            WarpLocation warpLocation = entry.getKey();
            if (warpLocation.getFromX() == localX && warpLocation.getFromY() == localY)
                return entry.getValue();
        }
        return null;
    }

    public int getNumberOfWarps() {
        return tileWarps.size();
    }

    void renderBottomLayers(Batch batch) {
        // Render layer from most bottom, going up.
        renderLayer(LayerDefinition.BACKGROUND, batch);
        renderLayer(LayerDefinition.GROUND, batch);
        renderLayer(LayerDefinition.GROUND_DECORATION, batch);
    }

    void renderDecorationLayer(Batch batch) {
        renderLayer(LayerDefinition.WALL_DECORATION, batch);
    }

    Tile[] getSortableTiles() {
        return layers.get(LayerDefinition.COLLIDABLES);
    }

    void renderOverheadLayer(Batch batch) {
        renderLayer(LayerDefinition.ROOF, batch);
    }

    private void renderLayer(LayerDefinition layerDefinition, Batch batch) {
        if (!worldBuilder.canDrawLayer(layerDefinition)) return;
        Tile[] layerTiles = layers.get(layerDefinition);
        if (layerTiles == null) return;

        // Make the width and height of a given tile just a tad bit larger
        // than it normally would be to prevent most tearing issues.
        final float TILE_SIZE_FIX = 0.005F;

        // Draw Y down to sort tiles correctly on screen.
        for (int y = ClientConstants.CHUNK_SIZE - 1; y >= 0; y--) {
            for (int x = 0; x < ClientConstants.CHUNK_SIZE; x++) {

                Tile tile = layerTiles[x + y * ClientConstants.CHUNK_SIZE];
                if (tile == null) continue;

                TileImage tileImage = tile.getTileImage();
                if (tileImage == null) continue;

                RegionManager regionManager = ClientMain.getInstance().getRegionManager();
                Region region = regionManager.getRegionToEdit();

                if (region.getRegionType() == RegionManager.RegionType.BUILDING) {
                    Location playerLocation = EntityManager.getInstance().getPlayerClient().getCurrentMapLocation();
                    boolean playerIntersect = region.doesIntersect(playerLocation.getX(), playerLocation.getY());
                    boolean tileIntersect = region.doesIntersect(tile.getWorldX(), tile.getWorldY());
                    if (playerIntersect && tileIntersect && layerDefinition == LayerDefinition.ROOF)
                        continue;
                }

                FileManager fileManager = ClientMain.getInstance().getFileManager();
                TextureAtlas atlas = fileManager.getAtlas(GameAtlas.TILES);
                TextureRegion textureRegion = atlas.findRegion(tileImage.getAnimationFrame().getFileName());

                float rx = (x + chunkX * ClientConstants.CHUNK_SIZE) * ClientConstants.TILE_SIZE;
                float ry = (y + chunkY * ClientConstants.CHUNK_SIZE) * ClientConstants.TILE_SIZE;

                batch.draw(textureRegion,
                        rx,
                        ry,
                        textureRegion.getRegionWidth() + TILE_SIZE_FIX,
                        textureRegion.getRegionHeight() + TILE_SIZE_FIX);
            }
        }
    }
}
