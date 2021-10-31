package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.Tile;
import com.forgestorm.client.game.world.maps.TileImage;
import com.forgestorm.client.game.world.maps.WorldChunk;
import com.forgestorm.shared.game.world.maps.Floors;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.game.world.tile.properties.TilePropertyTypes;
import com.forgestorm.shared.game.world.tile.properties.WorldEdit;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
@Setter
public class InteriorStairsProperty extends AbstractTileProperty implements WorldEdit {

    private Integer stairsDownImageID;

    public InteriorStairsProperty() {
        super(TilePropertyTypes.INTERIOR_STAIRS_PROPERTY);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);

        VisLabel fieldLabel = new VisLabel("Stairs Down Image ID: ");
        final VisTextField stairsDownField = new VisTextField();
        if (stairsDownImageID != null) stairsDownField.setText(stairsDownImageID.toString());
        mainTable.add(fieldLabel).row();
        mainTable.add(stairsDownField);

        stairsDownField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (stairsDownField.isEmpty()) {
                    stairsDownImageID = 0;
                } else {
                    stairsDownImageID = Integer.valueOf(stairsDownField.getText());
                }
            }
        });

        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {

        Integer stairsDownImageID = (Integer) tileProperties.get("stairsDownImageID");
        if (stairsDownImageID != null) setStairsDownImageID(stairsDownImageID);

        println(getClass(), "stairsDownImageID: " + stairsDownImageID, false, printDebugMessages);
        return this;
    }

    @Override
    public void applyPropertyToWorld(WorldChunk worldChunk, TileImage tileImage, LayerDefinition layerDefinition, String worldName, int worldX, int worldY, short worldZ) {
        TileImage stairsDownImage = ClientMain.getInstance().getWorldBuilder().getTileImage(stairsDownImageID);
        short worldYup = (short) (worldY + 1);
        short worldZup = (short) (worldZ + 1);

        if (stairsDownImage == null) {
            println(getClass(), "StairsDown TileImage was null. Fix this for TileProperty: " + tileImage.getFileName() + ", ID: " + tileImage.getImageId());
            return;
        }

        // Auto set the next floors TileImage
        Tile targetTile = worldChunk.getTile(layerDefinition, worldX, worldYup, Floors.getFloor(worldZup));
        targetTile.setTileImage(stairsDownImage);

        // Now create a warp so the player can change floors
//        Warp stairsUp = new Warp(new Location(worldName, worldX - 1, worldY, worldZ), MoveDirection.WEST);
//        worldChunk.addTileWarp(
//                new WarpLocation(worldX, worldY, worldZ), // From location
//                new Warp(new Location(worldName, worldX + 1, worldYup, worldZup), MoveDirection.WEST)); // To location

//        Warp stairsDown = new Warp(new Location(worldName, worldX + 1, worldYup, worldZup), MoveDirection.EAST);
//        worldChunk.addTileWarp(new WarpLocation(worldX, worldY, worldZ), stairsDown);
    }

    @Override
    public void removePropertyFromWorld(WorldChunk worldChunk, TileImage tileImage, LayerDefinition layerDefinition, String worldName, int worldX, int worldY, short worldZ) {
        TileImage stairsDownImage = ClientMain.getInstance().getWorldBuilder().getTileImage(stairsDownImageID);
        short worldYup = (short) (worldY + 1);
        short worldZup = (short) (worldZ + 1);

        if (stairsDownImage == null) {
            println(getClass(), "StairsDown TileImage was null. Fix this for TileProperty: " + tileImage.getFileName() + ", ID: " + tileImage.getImageId());
            return;
        }

        // Get the tile and remove the TileImage
        Tile targetTile = worldChunk.getTile(layerDefinition, worldX, worldYup, Floors.getFloor(worldZup));
        targetTile.removeTileImage();

        // Remove the tile warps
        worldChunk.removeTileWarp(worldX, worldY, worldZ);
        worldChunk.removeTileWarp(worldX, worldYup, worldZup);
    }
}
