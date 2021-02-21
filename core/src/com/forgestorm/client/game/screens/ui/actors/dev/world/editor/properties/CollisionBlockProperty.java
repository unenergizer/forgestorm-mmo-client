package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.window.CollisionWindow;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.Map;

import static com.forgestorm.client.util.Log.println;

public class CollisionBlockProperty extends AbstractTileProperty {

    private final transient CollisionWindow collisionWindow = new CollisionWindow();

    public CollisionBlockProperty() {
        super(TilePropertyTypes.COLLISION_BLOCK);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);

        VisLabel visLabel1 = new VisLabel("[RED]Contains Collision:");
        VisLabel visLabel2 = new VisLabel("[RED]Movement to this tile will be blocked.");
        VisTextButton visTextButton = new VisTextButton("Edit Collisions");
        mainTable.add(visLabel1).row();
        mainTable.add(visLabel2).row();
        mainTable.add(visTextButton);

        visTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
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
        return this;
    }
}
