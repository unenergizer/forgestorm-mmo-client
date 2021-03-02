package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.window.CollisionWindow;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class CollisionBlockProperty extends AbstractTileProperty {

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
            println(getClass(), "Value: " + collisionList);
            setCollisionList(collisionList);
        }

        return this;
    }
}
