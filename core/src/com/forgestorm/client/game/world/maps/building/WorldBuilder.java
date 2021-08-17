package com.forgestorm.client.game.world.maps.building;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang.WangTile;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang.WangTile16Bit;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang.WangTile4Bit;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang.WangType;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.Tile;
import com.forgestorm.client.game.world.maps.TileAnimation;
import com.forgestorm.client.game.world.maps.TileImage;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.network.game.packet.out.WorldBuilderPacketOut;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
public class WorldBuilder {

    private final Map<Integer, TileAnimation> tileAnimationMap;
    private final WangTile4Bit wangTile16 = new WangTile4Bit();
    private final WangTile16Bit wangTile48 = new WangTile16Bit();
    private final Map<Integer, TileImage> tileImageMap;
    private final TextureAtlas worldTileImages;
    private final Array<TextureAtlas.AtlasRegion> regions;
    private final Map<LayerDefinition, Boolean> layerVisibilityMap;
    private final Map<Integer, WangTile> wangImageMap;

    private LayerDefinition currentLayer = LayerDefinition.GROUND_DECORATION;
    @Setter
    private Integer currentTextureId = null;

    @Setter
    private boolean useEraser = false;

    @Setter
    private boolean useWangTile = false;

    private Integer currentWangId = null;
    private String wangRegionNamePrefix;
    private WangType wangType;

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
    }

    public void setCurrentWangId(Integer selectedWangTile) {
        currentWangId = selectedWangTile;
        if (selectedWangTile == null) return;
        WangTile wangTile = wangImageMap.get(currentWangId);
        wangType = wangTile.getWangType();
        wangRegionNamePrefix = wangType.getPrefix() + "-" + wangTile.getFileName() + "-";

        System.out.println("WangType: " + wangType);
        System.out.println("SelectedWangTile: " + selectedWangTile);
        System.out.println("WangRegionNamePrefix: " + wangRegionNamePrefix);
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
            // BUILDING USING WANG BRUSH, AUTO SELECT THE TILE!
            Integer autoTileID = null;
            switch (wangType) {
                case TYPE_16:
                    autoTileID = wangTile16.autoTile(currentLayer, worldX, worldY);
                    break;
                case TYPE_48:
                    autoTileID = wangTile48.autoTile(currentLayer, worldX, worldY);
                    break;
            }

            TileImage tileImage = getTileImage(wangRegionNamePrefix + autoTileID);

            System.out.println("CurrentLayer: " + currentLayer);
            System.out.println("TileImageName: " + wangRegionNamePrefix + autoTileID);
            System.out.println("TileImage: " + tileImage.getImageId());
            System.out.println("worldX: " + worldX);
            System.out.println("worldY: " + worldY);

            placeTile(currentLayer, tileImage.getImageId(), worldX, worldY, true);

            switch (wangType) {
                case TYPE_16:
                    wangTile16.updateAroundTile(currentLayer, worldX, worldY);
                    break;
                case TYPE_48:
                    wangTile48.updateAroundTile(currentLayer, worldX, worldY);
                    break;
            }
        } else {
            // BUILDING USING DRAW BRUSH, USE USER SELECTED TILE!
            placeTile(currentLayer, currentTextureId, worldX, worldY, true);
        }
    }

    public void placeTile(LayerDefinition layerDefinition, Integer textureId, int worldX, int worldY, boolean sendPacket) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        Tile tile = gameWorld.getTile(layerDefinition, worldX, worldY);

        if (tile == null) return;

        if (useEraser) {
            tile.removeTileImage();
            textureId = ClientConstants.BLANK_TILE_ID; // Set texture to erase
        } else {
            if (textureId == null) return;
            tile.setTileImage(new TileImage(tileImageMap.get(textureId)));
        }

        if (sendPacket) {
            new WorldBuilderPacketOut(currentLayer, textureId, worldX, worldY).sendPacket();
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
