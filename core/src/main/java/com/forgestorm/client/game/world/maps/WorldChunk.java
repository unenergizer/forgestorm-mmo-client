package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.game.world.maps.tile.Tile;
import com.forgestorm.client.game.world.maps.tile.TileImage;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.shared.game.world.maps.Floors;
import com.forgestorm.shared.game.world.maps.Warp;
import com.forgestorm.shared.game.world.maps.WarpLocation;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.io.type.GameAtlas;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.Getter;

public class WorldChunk {

    private final WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();

    @Getter
    private final String worldName;

    @Getter
    private final short chunkX, chunkY;

    @Getter
    private final Map<Floors, Map<LayerDefinition, Tile[]>> floorLayers = new HashMap<>();

    @Getter
    private final Map<WarpLocation, Warp> tileWarps = new HashMap<>();

    public WorldChunk(String worldName, short chunkX, short chunkY) {
        this.worldName = worldName;
        this.chunkX = chunkX;
        this.chunkY = chunkY;

        checkFloorAndInit(Floors.GROUND_FLOOR);
    }

    private void checkFloorAndInit(Floors floor) {

        // If this floor exists, do not regenerate
        if (floorLayers.containsKey(floor)) return;

        Map<LayerDefinition, Tile[]> layers = new HashMap<>();

        for (LayerDefinition layerDefinition : LayerDefinition.values()) {

            Tile[] tiles = new Tile[ClientConstants.CHUNK_SIZE * ClientConstants.CHUNK_SIZE];

            // Initialize all tiles
            for (int localX = 0; localX < ClientConstants.CHUNK_SIZE; localX++) {
                for (int localY = 0; localY < ClientConstants.CHUNK_SIZE; localY++) {

                    tiles[localX + localY * ClientConstants.CHUNK_SIZE] = new Tile(layerDefinition,
                            worldName,
                            this,
                            localX + chunkX * ClientConstants.CHUNK_SIZE,
                            localY + chunkY * ClientConstants.CHUNK_SIZE,
                            floor.getWorldZ());
                }
            }

            layers.put(layerDefinition, tiles);
        }

        floorLayers.put(floor, layers);
    }

    public void setChunkFromDisk(WorldChunk chunkFromDisk) {
        // Copy layers and floors
        for (Floors floor : Floors.values()) {

            Map<LayerDefinition, Tile[]> layerDefinitionMap = chunkFromDisk.floorLayers.get(floor);
            if (layerDefinitionMap == null) continue;

            for (Map.Entry<LayerDefinition, Tile[]> entry : layerDefinitionMap.entrySet()) {
                LayerDefinition layerDefinition = entry.getKey();
                Tile[] tiles = entry.getValue();

                for (Tile tileFromDisk : tiles) {
                    if (tileFromDisk.getTileImage() == null) continue;
                    int localTileX = tileFromDisk.getWorldX() - ClientConstants.CHUNK_SIZE * chunkX;
                    int localTileY = tileFromDisk.getWorldY() - ClientConstants.CHUNK_SIZE * chunkY;
                    Tile localTile = getTile(layerDefinition, localTileX, localTileY, floor);
                    localTile.setTileImage(tileFromDisk.getTileImage());
                }
            }
        }

        // Copy Warps
        for (Map.Entry<WarpLocation, Warp> entry : chunkFromDisk.getTileWarps().entrySet()) {
            WarpLocation warpLocation = entry.getKey();
            Warp warp = entry.getValue();

            addTileWarp(warpLocation, warp);
        }
    }

    public void setNetworkTiles(Floors floor, LayerDefinition layerDefinition, byte section, int[] tileImageIDs) {
        checkFloorAndInit(floor);

        for (int localX = 0; localX < tileImageIDs.length; localX++) {

            TileImage tileImage = worldBuilder.getTileImage(tileImageIDs[localX]);

            // Set TileImage if applicable.
            if (tileImage != null) {
                //noinspection UnnecessaryLocalVariable
                int localY = section; // Defined for readability...
                Tile tile = getTile(layerDefinition, localX, localY, floor);
                tile.setTileImage(new TileImage(tileImage));

            }
        }
    }

    public Tile getTile(LayerDefinition layerDefinition, int localX, int localY, Floors floor) {
        checkFloorAndInit(floor);
        return floorLayers.get(floor).get(layerDefinition)[localX + localY * ClientConstants.CHUNK_SIZE];
    }

    public boolean isTraversable(int localX, int localY) {
        Location location = EntityManager.getInstance().getPlayerClient().getCurrentMapLocation();
        return isTraversable(Floors.getFloor(location.getZ()), localX, localY);
    }

    private boolean isTraversable(Floors floor, int localX, int localY) {

        Map<LayerDefinition, Tile[]> layerDefinitionMap = floorLayers.get(floor);
        if (layerDefinitionMap == null) return false;

        Tile[] tiles = layerDefinitionMap.get(LayerDefinition.WORLD_OBJECTS);
        Tile tile = tiles[localX + localY * ClientConstants.CHUNK_SIZE];
        if (tile == null) return true;
        if (!ClientMain.getInstance().getDoorManager().isDoorwayTraversable(tile)) return false;
        return !tile.hasCollision();
    }

    public void clearTileWarps() {
        tileWarps.clear();
    }

    public void removeTileWarp(int localX, int localY, short localZ) {
        for (Iterator<Map.Entry<WarpLocation, Warp>> iterator = tileWarps.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<WarpLocation, Warp> entry = iterator.next();
            WarpLocation warpLocation = entry.getKey();

            if (warpLocation.getFromX() == localX
                    && warpLocation.getFromY() == localY
                    && warpLocation.getFromZ() == localZ) {
                iterator.remove();
                return;
            }
        }
    }

    public void addTileWarp(int localX, int localY, short localZ, Warp warp) {
        addTileWarp(new WarpLocation(localX, localY, localZ), warp);
    }

    public void addTileWarp(WarpLocation warpLocation, Warp warp) {
        tileWarps.put(warpLocation, warp);
    }

    Warp getWarp(int localX, int localY, short localZ) {
        for (Map.Entry<WarpLocation, Warp> entry : tileWarps.entrySet()) {
            WarpLocation warpLocation = entry.getKey();
            if (warpLocation.getFromX() == localX
                    && warpLocation.getFromY() == localY
                    && warpLocation.getFromZ() == localZ)
                return entry.getValue();
        }
        return null;
    }

    public int getNumberOfWarps() {
        return tileWarps.size();
    }

    void renderBottomLayers(Batch batch, Floors floor) {
        // Render layer from most bottom, going up.
        renderLayer(floor, LayerDefinition.BACKGROUND, batch);
        renderLayer(floor, LayerDefinition.GROUND, batch);
        renderLayer(floor, LayerDefinition.GROUND_DECORATION, batch);
    }

    void renderDecorationLayer(Batch batch, Floors floor) {
        renderLayer(floor, LayerDefinition.WORLD_OBJECT_DECORATION, batch);
    }

    void renderOverheadLayer(Batch batch, Floors floor) {
        renderLayer(floor, LayerDefinition.OVERHEAD, batch);
    }

    Tile[] getSortableTiles(Floors floor) {
        Map<LayerDefinition, Tile[]> layerDefinitionMap = floorLayers.get(floor);
        if (layerDefinitionMap == null) return null;

        return floorLayers.get(floor).get(LayerDefinition.WORLD_OBJECTS);
    }

    private void renderLayer(Floors floor, LayerDefinition layerDefinition, Batch batch) {
        if (!worldBuilder.canDrawLayer(layerDefinition)) return;

        Map<LayerDefinition, Tile[]> layerDefinitionMap = floorLayers.get(floor);
        if (layerDefinitionMap == null) return;

        Tile[] layerTiles = layerDefinitionMap.get(layerDefinition);
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
                    if (playerIntersect && tileIntersect && layerDefinition == LayerDefinition.OVERHEAD)
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
