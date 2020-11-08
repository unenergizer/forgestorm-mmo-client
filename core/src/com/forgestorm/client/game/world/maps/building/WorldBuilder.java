package com.forgestorm.client.game.world.maps.building;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.world.maps.GameMap;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.network.game.packet.out.WorldBuilderPacketOut;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
public class WorldBuilder {

    private Map<Integer, TileImage> tileImageMap;
    private TextureAtlas textureAtlas;
    private Array<TextureAtlas.AtlasRegion> regions;

    @Setter
    private LayerDefinition currentLayer = LayerDefinition.GROUND_DECORATION;
    @Setter
    private int currentTextureId = 0;


    public WorldBuilder() {
        // Load AbstractTileProperty.yaml
        tileImageMap = ClientMain.getInstance().getFileManager().getTilePropertiesData().getWorldImageMap();

        // Load Tiles atlas
        textureAtlas = ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.TILES);
        regions = textureAtlas.getRegions();
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

    public void placeTile(int tileX, int tileY) {
        // Only allow tile place if the World Builder is open
        if (!ClientMain.getInstance().getStageHandler().getTileBuildMenu().isVisible()) return;
        placeTile(currentLayer, currentTextureId, tileX, tileY);
        new WorldBuilderPacketOut(currentLayer, currentTextureId, (short) tileX, (short) tileY).sendPacket();
    }

    public void placeTile(LayerDefinition layerDefinition, int textureId, int tileX, int tileY) {
        GameMap gameMap = ClientMain.getInstance().getMapManager().getCurrentGameMap();
        gameMap.setTileImage(layerDefinition, tileImageMap.get(textureId), tileX, tileY);
    }

    public void drawMouse(SpriteBatch spriteBatch) {
        if (!ClientMain.getInstance().getStageHandler().getTileBuildMenu().isVisible()) return;
        MouseManager mouseManager = ClientMain.getInstance().getMouseManager();
        int x = mouseManager.getMouseTileX() * 16;
        int y = mouseManager.getMouseTileY() * 16;

        TextureAtlas.AtlasRegion region = textureAtlas.findRegion(tileImageMap.get(currentTextureId).getFileName());
        spriteBatch.draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
    }

    public void addNewTile(TileImage newTileImage) {
        // Search for existing entry...
        for (TileImage tileImage : tileImageMap.values()) {
            if (tileImage.getFileName().equals(newTileImage.getFileName())) {
                tileImage.setBuildCategory(newTileImage.getBuildCategory());

                if (newTileImage.getLayerDefinition() != null) {
                    tileImage.setLayerDefinition(newTileImage.getLayerDefinition());
                }

                if (newTileImage.getTileProperties() != null) {
                    // TODO: FIX ME .get(0)!!!
                    tileImage.setCustomTileProperty(newTileImage.getTileProperties().get(0));
                }

                return;
            }
        }

        // if no entry found, create a new one
        tileImageMap.put(newTileImage.getImageId(), newTileImage);
    }
}
