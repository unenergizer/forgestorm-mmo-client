package com.forgestorm.client.game.world.maps.building;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.autotile.AutoTiler;
import com.forgestorm.autotile.TileGetterSetter;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.tile.Tile;
import com.forgestorm.client.game.world.maps.tile.TileAnimation;
import com.forgestorm.client.game.world.maps.tile.TileImage;
import com.forgestorm.client.game.world.maps.tile.properties.TileWalkOverSoundProperty;
import com.forgestorm.client.game.world.maps.tile.properties.WangTileProperty;
import com.forgestorm.client.network.game.packet.out.WorldBuilderPacketOut;
import com.forgestorm.shared.game.world.maps.Floors;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;
import com.forgestorm.shared.game.world.maps.tile.wang.WangType;
import com.forgestorm.shared.io.type.GameAtlas;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.forgestorm.client.util.Log.println;

@Getter
public class WorldBuilder implements TileGetterSetter {

    private static final boolean PRINT_DEBUG = false;

    private final AutoTiler autoTiler = new AutoTiler(this);

    private final Map<Integer, TileAnimation> tileAnimationMap;
    private final Map<Integer, TileImage> tileImageMap;
    private final TextureAtlas worldTileImages;
    private final Array<TextureAtlas.AtlasRegion> regions;
    private final Map<LayerDefinition, Boolean> layerVisibilityMap;
    private final Map<Floors, Boolean> floorVisibilityMap;

    private LayerDefinition currentLayer = LayerDefinition.WORLD_OBJECTS;
    @Setter
    private Integer currentTextureId = null;

    @Setter
    private Floors currentWorkingFloor = Floors.GROUND_FLOOR;

    @Setter
    private boolean useEraser = false;

    @Setter
    private boolean useWangTile = false;

    private WangTileProperty selectedWangTile;

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

        // Process all wang tiles and dynamically apply the correct properties
        parseWangTiles();

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
        layerVisibilityMap = new HashMap<>();
        for (LayerDefinition layerDefinition : LayerDefinition.values()) {
            layerVisibilityMap.put(layerDefinition, true);
        }

        // Setup floor visibility
        floorVisibilityMap = new HashMap<>();
        for (Floors floors : Floors.values()) {
            floorVisibilityMap.put(floors, true);
        }
    }

    private void parseWangTiles() {
        int wangId = 0;
        for (TileImage tileImage : tileImageMap.values()) {
            if (tileImage.getFileName().startsWith(WangType.TYPE_16.getPrefix()) || tileImage.getFileName().startsWith(WangType.TYPE_48.getPrefix())) {
                if (!tileImage.containsProperty(TilePropertyTypes.WANG_TILE)) {
                    println(getClass(), "WANG TILE PROPERTY MISSING FOR : " + tileImage.getFileName());
                    continue;
                }

                WangTileProperty wangTileProperty = (WangTileProperty) tileImage.getProperty(TilePropertyTypes.WANG_TILE);
                WangType wangType = wangTileProperty.getWangType();

                // Only get the "DefaultWangTileImageId" as this TileImage will have all the details
                // for it filled out on the TileProperties.yaml file. Other wang tiles will not
                // have the wang info filled out to save file space and time.
                // The "DefaultWangTileImageId" is the ID of the TileImage shown in the
                // TileBuildMenu class (other wang images of the same type not shown).
                if (wangType == null) continue;
                if (tileImage.getFileName().endsWith(wangType.getDefaultWangTileImageId())) {
                    // Set transient values
                    String rootFileName = tileImage.getFileName().replace(wangType.getPrefix(), "").replace(wangType.getDefaultWangTileImageId(), "");
                    String wangRegionNamePrefix = wangType.getPrefix() + rootFileName + "=";

                    wangTileProperty.setTemporaryWangId(wangId);
                    wangTileProperty.setWangRegionNamePrefix(wangRegionNamePrefix);

                    println(getClass(), "///////////////////////////////////////////////////////////", false, PRINT_DEBUG);
                    println(getClass(), "Setting wang tile: " + tileImage.getFileName(), false, PRINT_DEBUG);
                    if (PRINT_DEBUG) wangTileProperty.printDebug(getClass());

                    println(getClass(), "Applying wang properties to:", false, PRINT_DEBUG);
                    applyWangIdNumberToTiles(wangTileProperty);


                    if (tileImage.containsProperty(TilePropertyTypes.WALK_OVER_SOUND)) {
                        TileWalkOverSoundProperty tileWalkOverSoundProperty = (TileWalkOverSoundProperty) tileImage.getProperty(TilePropertyTypes.WALK_OVER_SOUND);
                        println(getClass(), "Applying sound properties to" + tileWalkOverSoundProperty.getTileWalkSound() + " sound to: ", false, PRINT_DEBUG);
                        applyWalkingSounds(wangRegionNamePrefix, tileWalkOverSoundProperty);
                    }

                    wangId++;
                }
            }
        }
    }

    /**
     * This will apply the current wang property to other wang tiles with the same name prefix.
     *
     * @param wangTileProperty The property we intend to copy.
     */
    private void applyWangIdNumberToTiles(WangTileProperty wangTileProperty) {
        for (TileImage tileImage : tileImageMap.values()) {
            if (!tileImage.getFileName().contains(wangTileProperty.getWangRegionNamePrefix()))
                continue;
            if (!tileImage.containsProperty(TilePropertyTypes.WANG_TILE)) {
                println(getClass(), "[WANG] POSSIBLE WANG TILE FOUND BUT IT HAS NO WANG TILE PROPERTY? " + tileImage.getFileName(), true, PRINT_DEBUG);
                continue;
            }
            println(getClass(), " -> " + tileImage.getFileName(), false, PRINT_DEBUG);
            WangTileProperty wangTilePropertyToUpdate = (WangTileProperty) tileImage.getProperty(TilePropertyTypes.WANG_TILE);
            wangTilePropertyToUpdate.setTemporaryWangId(wangTileProperty.getTemporaryWangId());
            wangTilePropertyToUpdate.setWangRegionNamePrefix(wangTileProperty.getWangRegionNamePrefix());
            wangTilePropertyToUpdate.setWangType(wangTileProperty.getWangType());
            wangTilePropertyToUpdate.setMinimalBrushSize(wangTileProperty.getMinimalBrushSize());
        }
    }

    /**
     * This will apply any applied walk over sound from the default wang tile to each wang tile
     * in a wang tile set.
     *
     * @param wangRegionNamePrefix      The prefix name to look for in the loop below
     * @param tileWalkOverSoundProperty The sound properties we intend to copy
     */
    private void applyWalkingSounds(String wangRegionNamePrefix, TileWalkOverSoundProperty tileWalkOverSoundProperty) {
        for (TileImage tileImage : tileImageMap.values()) {
            if (!tileImage.getFileName().contains(wangRegionNamePrefix))
                continue;
            if (!tileImage.containsProperty(TilePropertyTypes.WANG_TILE)) {
                println(getClass(), "[WALK OVER SOUND] POSSIBLE WANG TILE FOUND BUT IT HAS NO WANG TILE PROPERTY? " + tileImage.getFileName(), true, PRINT_DEBUG);
                continue;
            }
            println(getClass(), " -> " + tileImage.getFileName(), false, PRINT_DEBUG);
            TileWalkOverSoundProperty walkOverSoundProperty = new TileWalkOverSoundProperty();
            walkOverSoundProperty.setTileWalkSound(tileWalkOverSoundProperty.getTileWalkSound());
            tileImage.setCustomTileProperty(walkOverSoundProperty);
        }
    }

    public void setCurrentWangId(WangTileProperty selectedWangTile) {
        if (selectedWangTile == null) return;
        this.selectedWangTile = selectedWangTile;

        println(getClass(), "WangType: " + selectedWangTile.getWangType());
        println(getClass(), "SelectedWangTile: " + selectedWangTile.getTemporaryWangId());
        println(getClass(), "WangRegionNamePrefix: " + selectedWangTile.getWangRegionNamePrefix());
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

    @Override
    public String getTile(int x, int y) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        Tile tile = gameWorld.getTile(currentLayer, x, y, currentWorkingFloor.getWorldZ());

        if (tile == null) return null;
        if (tile.getTileImage() == null) return null;
        return tile.getTileImage().getFileName();
    }

    @Override
    public void setTile(String tileName, int x, int y) {
        TileImage tileImage = getTileImage(tileName);
        if (tileImage == null) {
            println(getClass(), "AutoTiler tile does not exist: " + tileName);
            return;
        }
        placeTile(currentLayer, tileImage.getImageId(), x, y, currentWorkingFloor.getWorldZ(), true);
    }

    public void placeTile(int worldX, int worldY) {

        // Only allow tile place if the World Builder is open
        if (!ClientMain.getInstance().getStageHandler().getTileBuildMenu().isVisible()) return;

        if (useWangTile) {
            TileImage tileImage = new TileImage(tileImageMap.get(currentTextureId));
            boolean tileSet = autoTiler.autoTile(tileImage.getFileName(), worldX, worldY);
            println(getClass(), "AutoTiler place tile: " + tileSet);
        } else {
            // BUILDING USING DRAW BRUSH, USE USER SELECTED TILE!
            placeTile(currentLayer, currentTextureId, worldX, worldY, currentWorkingFloor.getWorldZ(), true);
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
