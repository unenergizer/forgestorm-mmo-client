package com.forgestorm.client.game.world.maps.building;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang.WangTile;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang.WangTile16Bit;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.network.game.packet.out.WorldBuilderPacketOut;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
public class WorldBuilder {

    private final WangTile16Bit wangTile16Bit = new WangTile16Bit();
    private final Map<Integer, TileImage> tileImageMap;
    private final TextureAtlas textureAtlas;
    private final Array<TextureAtlas.AtlasRegion> regions;
    private final Map<LayerDefinition, Boolean> layerVisibilityMap;
    private final Map<Integer, WangTile> wangImageMap;

    private LayerDefinition currentLayer = LayerDefinition.GROUND_DECORATION;
    @Setter
    private int currentTextureId = 1;

    @Setter
    private boolean useEraser = false;

    @Setter
    private boolean useWangTile = false;

    private int currentWangId = 1;
    @Getter
    private String wangRegionNamePrefix;

    @Getter
    @Setter
    private boolean allowClickToMove = true;

    public WorldBuilder() {
        // Load AbstractTileProperty.yaml
        tileImageMap = ClientMain.getInstance().getFileManager().getTilePropertiesData().getWorldImageMap();

        // Load WangProperties.yaml
        wangImageMap = ClientMain.getInstance().getFileManager().getWangPropertiesData().getWangImageMap();

        // Load Tiles atlas
        textureAtlas = ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.TILES);
        regions = textureAtlas.getRegions();

        // Setup layer visibility
        layerVisibilityMap = new HashMap<LayerDefinition, Boolean>();
        for (LayerDefinition layerDefinition : LayerDefinition.values()) {
            layerVisibilityMap.put(layerDefinition, true);
        }
    }

    public void setCurrentWangId(int selectedWangTile) {
        currentWangId = selectedWangTile;
        WangTile wangTile = wangImageMap.get(currentWangId);
        wangRegionNamePrefix = wangTile.getWangType().getPrefix() + "-" + wangTile.getFileName() + "-";
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
        if (useEraser) {

            placeTile(currentLayer, ClientConstants.BLANK_TILE_ID, worldX, worldY, true);
        } else {
            // NOT USING ERASER
            if (useWangTile) {
                // BUILDING USING WANG BRUSH, AUTO SELECT THE TILE!
                int autoTileID = wangTile16Bit.autoTile(currentLayer, worldX, worldY);
                TileImage tileImage = getTileImage(wangRegionNamePrefix + autoTileID);
                placeTile(currentLayer, tileImage.getImageId(), worldX, worldY, true);
                wangTile16Bit.updateAroundTile(currentLayer, worldX, worldY);
            } else {
                // BUILDING USING DRAW BRUSH, USE USER SELECTED TILE!
                placeTile(currentLayer, currentTextureId, worldX, worldY, true);
            }
        }
    }

    public void placeTile(LayerDefinition layerDefinition, int textureId, int worldX, int worldY, boolean sendPacket) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        gameWorld.setTileImage(layerDefinition, tileImageMap.get(textureId), worldX, worldY);

        if (sendPacket) {
            new WorldBuilderPacketOut(currentLayer, textureId, worldX, worldY).sendPacket();
        }
    }

    public void drawMouse(SpriteBatch spriteBatch) {
        if (!ClientMain.getInstance().getStageHandler().getTileBuildMenu().isVisible()) return;
        MouseManager mouseManager = ClientMain.getInstance().getMouseManager();
        int x = mouseManager.getMouseTileX() * 16;
        int y = mouseManager.getMouseTileY() * 16;

        // Set alpha to .5
        Color color = spriteBatch.getColor();
        spriteBatch.setColor(color.r, color.g, color.b, .5f);

        if (useEraser) {
            spriteBatch.draw(ClientMain.getInstance().getGameScreen().getInvalidTileLocationTexture(), x, y);
        } else {
            TextureAtlas.AtlasRegion region = textureAtlas.findRegion(tileImageMap.get(currentTextureId).getFileName());
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
