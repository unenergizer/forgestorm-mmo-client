package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.window.CollisionWindow;
import com.forgestorm.client.game.world.maps.Tile;
import com.forgestorm.client.game.world.maps.TileImage;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class CollisionBlockProperty extends AbstractTileProperty implements WorldEdit {

    private final transient CollisionWindow collisionWindow = new CollisionWindow();

    @Getter
    @Setter
    private List<Boolean> collisionList;

    public CollisionBlockProperty() {
        super(TilePropertyTypes.COLLISION_BLOCK);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);

        VisLabel visLabel1 = new VisLabel("[RED]Contains Collision:");
        VisTextButton visTextButton = new VisTextButton("Edit Collisions");
        mainTable.add(visLabel1);
        mainTable.add(visTextButton);

        final CollisionBlockProperty collisionBlockProperty = this;

        visTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                collisionWindow.loadTileImage(collisionBlockProperty, collisionList, ClientMain.getInstance().getWorldBuilder().getTextureAtlas().findRegion(getTileImage().getFileName()));
                collisionWindow.setVisible(true);
            }
        });

        // Add collisionWindow to stage
        ClientMain.getInstance().getStageHandler().getStage().addActor(collisionWindow.build());

        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {
        println(getClass(), "Tile not traversable.", false, printDebugMessages);

        @SuppressWarnings("unchecked")
        List<Boolean> collisionList = (List<Boolean>) tileProperties.get("collisionList");
        if (collisionList != null) {
            println(getClass(), "Collision Value(s): " + collisionList, false, printDebugMessages);
            setCollisionList(collisionList);
        }

        return this;
    }

    @Override
    public void applyPropertyToWorld(TileImage tileImage, LayerDefinition layerDefinition, int worldX, int worldY) {
        processCollisionTiles(tileImage, layerDefinition, worldX, worldY, false);
    }

    @Override
    public void removePropertyToWorld(TileImage tileImage, LayerDefinition layerDefinition, int worldX, int worldY) {
        processCollisionTiles(tileImage, layerDefinition, worldX, worldY, true);
    }

    private void processCollisionTiles(TileImage tileImage, LayerDefinition layerDefinition, int worldX, int worldY, boolean useEraser) {
        int tilesWide = tileImage.getWidth() / ClientConstants.TILE_SIZE;
        int tilesTall = tileImage.getHeight() / ClientConstants.TILE_SIZE;

        for (int row = 0; row < tilesTall; row++) {
            for (int column = 0; column < tilesWide; column++) {
                int index = row + column * tilesTall;

                if (collisionList.get(index)) {
                    // Convert the coordinates of the collision areas to world coordinates.
                    // Then add this TileImage as a collision parent in the given
                    // TileImage from the world coordinates found above.

                    int tileX = worldX + column;
                    int tileY = worldY + tilesTall - row - 1;
                    Tile tileParent = ClientMain.getInstance().getWorldManager()
                            .getGameWorld(ClientMain.getInstance().getWorldManager().getCurrentGameWorld().getWorldName())
                            .getTile(layerDefinition, tileX, tileY);

                    if (tileParent == null) continue;
                    if (useEraser) {
                        tileParent.removeCollision(tileImage);
                    } else {
                        tileParent.addCollision(tileImage);
                    }
                }
            }
        }
    }
}
