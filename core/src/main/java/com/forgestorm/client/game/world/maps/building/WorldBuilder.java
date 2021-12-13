package com.forgestorm.client.game.world.maps.building;

import static com.forgestorm.client.util.Log.println;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.ui.actors.dev.world.BrushSize;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang.WangTile;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang.WangTile16Bit;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang.WangTile4Bit;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.Tile;
import com.forgestorm.client.game.world.maps.TileAnimation;
import com.forgestorm.client.game.world.maps.TileImage;
import com.forgestorm.client.network.game.packet.out.WorldBuilderPacketOut;
import com.forgestorm.shared.game.world.maps.Floors;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.io.type.GameAtlas;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
public class WorldBuilder {

    private final Map<Integer, TileAnimation> tileAnimationMap;
    private final WangTile4Bit wangTile16 = new WangTile4Bit();
    private final WangTile16Bit wangTile48 = new WangTile16Bit();
    private final Map<Integer, TileImage> tileImageMap;
    private final TextureAtlas worldTileImages;
    private final Array<TextureAtlas.AtlasRegion> regions;
    private final Map<LayerDefinition, Boolean> layerVisibilityMap;
    private final Map<Floors, Boolean> floorVisibilityMap;
    private final Map<Integer, WangTile> wangImageMap;

    private LayerDefinition currentLayer = LayerDefinition.WORLD_OBJECTS;
    @Setter
    private Integer currentTextureId = null;

    @Setter
    private Floors currentWorkingFloor = Floors.GROUND_FLOOR;

    @Setter
    private boolean useEraser = false;

    @Setter
    private boolean useWangTile = false;

    private WangTile wangTile;

    @Setter
    private boolean allowClickToMove = true;

    public WorldBuilder() {

        // Load Tiles atlas
        worldTileImages = ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.TILES);
        regions = worldTileImages.getRegions();

        // Load AbstractTileProperty.yaml
        tileImageMap = ClientMain.getInstance().getFileManager().getTilePropertiesData().getWorldImageMap();

        // Validate and remove any TileImages that are missing from the Tiles.Atlas file.
        // This will happen if graphics are removed or renamed in the Tiles.Atlas file.
        for (Iterator<Map.Entry<Integer, TileImage>> iterator = tileImageMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Integer, TileImage> entry = iterator.next();
            TileImage tileImage = entry.getValue();
            if (worldTileImages.findRegion(tileImage.getFileName()) == null) {
                println(getClass(), "TileImage Removed: " + tileImage.getFileName(), true);
                iterator.remove();
            }
        }

        // Load WangProperties.yaml
        wangImageMap = ClientMain.getInstance().getFileManager().getWangPropertiesData().getWangImageMap();

        // Load TileAnimations.yaml
        tileAnimationMap = ClientMain.getInstance().getFileManager().getTileAnimationData().getTileAnimationMap();

        // Removing broken animations
        for (Iterator<Map.Entry<Integer, TileAnimation>> iteratorEntry = tileAnimationMap.entrySet().iterator(); iteratorEntry.hasNext(); ) {
            Map.Entry<Integer, TileAnimation> tileAnimationEntry = iteratorEntry.next();
            int id = tileAnimationEntry.getKey();
            TileAnimation tileAnimation = tileAnimationEntry.getValue();

            boolean brokenAnimationFound = false;

            // Loop through and compare frame id with tileImage id's.
            for (TileAnimation.AnimationFrame animationFrame : tileAnimation.getAnimationFrames().values()) {
                boolean animationFrameImageMissing = true;
                for (TileImage tileImage : tileImageMap.values()) {
                    if (tileImage.getImageId() == animationFrame.getTileId()) {
                        animationFrameImageMissing = false;
                        break;
                    }
                }
                if (animationFrameImageMissing) {
                    brokenAnimationFound = true;
                    break;
                }
            }

            // Remove TileAnimations that can't be found.
            if (brokenAnimationFound) {
                println(getClass(), "TileAnimation Removed ID: " + id, true);
                iteratorEntry.remove();
            }
        }

        // Process remaining animations
        for (TileAnimation tileAnimation : tileAnimationMap.values()) {
            // Loop through TileImageMap and set animation ID's
            for (TileImage tileImage : tileImageMap.values()) {
                if (tileImage.getImageId() == tileAnimation.getAnimationFrames().get(0).getTileId()) {
                    // We found a match. Copy contents of original animation into a new instance
                    tileImage.setTileAnimation(new TileAnimation(tileAnimation));
                }
            }
        }

        // Setup layer visibility
        layerVisibilityMap = new HashMap<LayerDefinition, Boolean>();
        for (LayerDefinition layerDefinition : LayerDefinition.values()) {
            layerVisibilityMap.put(layerDefinition, true);
        }

        // Setup floor visibility
        floorVisibilityMap = new HashMap<Floors, Boolean>();
        for (Floors floors : Floors.values()) {
            floorVisibilityMap.put(floors, true);
        }
    }

    public void setCurrentWangId(Integer selectedWangTile) {
        if (selectedWangTile == null) return;
        wangTile = wangImageMap.get(selectedWangTile);

        println(getClass(), "WangType: " + wangTile.getWangType());
        println(getClass(), "SelectedWangTile: " + wangTile.getWangId());
        println(getClass(), "WangRegionNamePrefix: " + wangTile.getWangRegionNamePrefix());
    }

    public WangTile findWangTile(TileImage tileImage) {
        for (WangTile wangTile : wangImageMap.values()) {
            if (tileImage.getFileName().contains(wangTile.getFileName())) return wangTile;
        }
        return null;
    }

    public boolean toggleLayerVisibility(LayerDefinition layerDefinition) {
        boolean visibility = !layerVisibilityMap.get(layerDefinition);
        layerVisibilityMap.put(layerDefinition, visibility);
        return visibility;
    }

    public boolean toggleFloorVisibility(Floors floors) {
        boolean visibility = !floorVisibilityMap.get(floors);
        floorVisibilityMap.put(floors, visibility);
        return visibility;
    }

    public boolean isFloorVisible(Floors floor) {
        return floorVisibilityMap.get(floor);
    }

    public boolean canDrawLayer(LayerDefinition layerDefinition) {
        return layerVisibilityMap.get(layerDefinition);
    }

    public void setCurrentLayer(LayerDefinition layerDefinition) {
        this.currentLayer = layerDefinition;
        ClientMain.getInstance().getStageHandler().getTileBuildMenu().setSelectedLayerButton(layerDefinition);
    }

    public TileImage getTileImage(int tileImageID) {
        return tileImageMap.get(tileImageID);
    }

    public TileImage getTileImage(String regionName) {
        TileImage tileImage = null;
        for (TileImage entry : tileImageMap.values()) {
            if (entry.getFileName().equals(regionName)) {
                tileImage = entry;
                break;
            }
        }
        return tileImage;
    }

    public void placeTile(int worldX, int worldY) {

        // Only allow tile place if the World Builder is open
        if (!ClientMain.getInstance().getStageHandler().getTileBuildMenu().isVisible()) return;

        if (useWangTile) {
            switch (wangTile.getWangType()) {
                ////// TYPE 16 ///////////////////////////////////////////////////////////////////
                case TYPE_16:
                    switch (wangTile.getBrushSize()) {
                        case SIX:
                            println(getClass(), "PLACING WANG 16 - BRUSH 6");
                            // Column 1
                            placeWangTile(worldX, worldY + 2, 5); // Top left
                            placeWangTile(worldX, worldY + 1, 13);
                            placeWangTile(worldX, worldY, 12); // Bottom left

                            // Column 2
                            placeWangTile(worldX + 1, worldY + 2, 7); // Top Middle
                            placeWangTile(worldX + 1, worldY + 1, 15); // Center
                            placeWangTile(worldX + 1, worldY, 14); // Bottom Middle

                            // Column 3
                            placeWangTile(worldX + 2, worldY + 2, 3); // Top Right
                            placeWangTile(worldX + 2, worldY + 1, 11);
                            placeWangTile(worldX + 2, worldY, 10); // Bottom Right
                            updateAroundWangTile(worldX, worldY);
                            break;
                        case FOUR:
                            println(getClass(), "PLACING WANG 16 - BRUSH 4");
                            // Column 1
                            placeWangTile(worldX, worldY + 1, 5); // Top Left
                            placeWangTile(worldX, worldY, 12); // Bottom Left

                            // Column 2
                            placeWangTile(worldX + 1, worldY + 1, 3); // Top Right
                            placeWangTile(worldX + 1, worldY, 10); // Bottom Right
                            updateAroundWangTile(worldX, worldY);
                            break;
                        case ONE:
                        default:
                            println(getClass(), "PLACING WANG 16 - BRUSH 1");
                            placeWangTile(worldX, worldY, 0); // Single
                            wangTile16.updateAroundTile(currentLayer, worldX, worldY, currentWorkingFloor.getWorldZ());
                            break;
                    }
                    break;
                ////// TYPE 48 ///////////////////////////////////////////////////////////////////
                case TYPE_48:
                    switch (wangTile.getBrushSize()) {
                        case SIX:
                            println(getClass(), "PLACING WANG 48 - BRUSH 6");
                            // Column 1
                            placeWangTile(worldX, worldY + 2, 22); // Top left
                            placeWangTile(worldX, worldY + 1, 214);
                            placeWangTile(worldX, worldY, 208); // Bottom left

                            // Column 2
                            placeWangTile(worldX + 1, worldY + 2, 31); // Top Middle
                            placeWangTile(worldX + 1, worldY + 1, 255); // Center
                            placeWangTile(worldX + 1, worldY, 248); // Bottom Middle

                            // Column 3
                            placeWangTile(worldX + 2, worldY + 2, 11); // Top Right
                            placeWangTile(worldX + 2, worldY + 1, 107);
                            placeWangTile(worldX + 2, worldY, 104); // Bottom Right
                            updateAroundWangTile(worldX, worldY);
                            break;
                        case FOUR:
                            println(getClass(), "PLACING WANG 48 - BRUSH 4");
                            // Column 1
                            placeWangTile(worldX, worldY + 1, 22); // Top Left
                            placeWangTile(worldX, worldY, 208); // Bottom Left

                            // Column 2
                            placeWangTile(worldX + 1, worldY + 1, 11); // Top Right
                            placeWangTile(worldX + 1, worldY, 104); // Bottom Right
                            updateAroundWangTile(worldX, worldY);
                            break;
                        case ONE:
                        default:
                            println(getClass(), "PLACING WANG 48 - BRUSH 1");
                            placeWangTile(worldX, worldY, 0); // Single
                            wangTile48.updateAroundTile(currentLayer, worldX, worldY, currentWorkingFloor.getWorldZ());
                            break;
                    }
                    break;
            }
        } else {
            // BUILDING USING DRAW BRUSH, USE USER SELECTED TILE!
            placeTile(currentLayer, currentTextureId, worldX, worldY, currentWorkingFloor.getWorldZ(), true);
        }
    }

    public void placeWangTile(int worldX, int worldY, int autoTileID) {
        // BUILDING USING WANG BRUSH, AUTO SELECT THE TILE!

        // If working with a brush size of one, enable the auto-tile surrounding that tile
        if (wangTile.getBrushSize() == BrushSize.ONE) {
            switch (wangTile.getWangType()) {
                case TYPE_16:
                    autoTileID = wangTile16.autoTile(currentLayer, worldX, worldY, currentWorkingFloor.getWorldZ());
                    break;
                case TYPE_48:
                    autoTileID = wangTile48.autoTile(currentLayer, worldX, worldY, currentWorkingFloor.getWorldZ());
                    break;
            }
        }

        TileImage tileImage = getTileImage(wangTile.getWangRegionNamePrefix() + autoTileID);

        // If the tile is null, display an error message before we crash
        if (tileImage == null)
            System.err.println("Has this tile been added to TileProperties.yaml????");

        placeTile(currentLayer, tileImage.getImageId(), worldX, worldY, currentWorkingFloor.getWorldZ(), true);
    }

    private void updateAroundWangTile(int worldX, int worldY) {
        for (int x = 0; x < wangTile.getBrushSize().getSize() / 2; x++) {
            for (int y = 0; y < wangTile.getBrushSize().getSize() / 2; y++) {
                switch (wangTile.getWangType()) {
                    case TYPE_16:
                        wangTile16.updateAroundTile(currentLayer, worldX + x, worldY + y, currentWorkingFloor.getWorldZ());
                        break;
                    case TYPE_48:
                        wangTile48.updateAroundTile(currentLayer, worldX + x, worldY + y, currentWorkingFloor.getWorldZ());
                        break;
                }
            }
        }
    }

    public void placeTile(LayerDefinition layerDefinition, Integer textureId, int worldX, int worldY, short worldZ, boolean sendPacket) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        Tile tile = gameWorld.getTile(layerDefinition, worldX, worldY, worldZ);

        if (tile == null) return;
        if (textureId == null && !useEraser) return;
        if (useEraser) {
            // Local user delete tile
            tile.removeTileImage();
            textureId = ClientConstants.BLANK_TILE_ID; // Set texture to erase
        } else if (textureId == ClientConstants.BLANK_TILE_ID) {
            // Network delete tile
            tile.removeTileImage();
        } else {
            // Local && Network set tile
            tile.setTileImage(new TileImage(tileImageMap.get(textureId)));
        }

        if (sendPacket) {
            new WorldBuilderPacketOut(currentLayer, textureId, worldX, worldY, worldZ).sendPacket();
        }
    }

    public void drawMouse(SpriteBatch spriteBatch) {
        if (!ClientMain.getInstance().getStageHandler().getTileBuildMenu().isVisible()) return;
        if (currentTextureId == null && !useEraser) return;

        MouseManager mouseManager = ClientMain.getInstance().getMouseManager();
        int x = mouseManager.getMouseTileX() * 16;
        int y = mouseManager.getMouseTileY() * 16;

        // Set alpha to .5
        Color color = spriteBatch.getColor();
        spriteBatch.setColor(color.r, color.g, color.b, .5f);

        if (useEraser) {
            spriteBatch.draw(ClientMain.getInstance().getGameScreen().getInvalidTileLocationTexture(), x, y);
        } else {
            TextureAtlas.AtlasRegion region = worldTileImages.findRegion(tileImageMap.get(currentTextureId).getFileName());
            spriteBatch.draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
        }

        // Reset alpha
        spriteBatch.setColor(color.r, color.g, color.b, 1f);
    }

    public void addNewTile(TileImage newTileImage) {
        // Search for existing entry...
        for (TileImage tileImage : tileImageMap.values()) {
            if (tileImage.getFileName().equals(newTileImage.getFileName())) return;
        }

        // if no entry found, create a new one
        tileImageMap.put(newTileImage.getImageId(), newTileImage);
    }

    public int getTileImageMapSize() {
        return tileImageMap.size();
    }
}
