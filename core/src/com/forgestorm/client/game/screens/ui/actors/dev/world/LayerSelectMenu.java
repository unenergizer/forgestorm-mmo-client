package com.forgestorm.client.game.screens.ui.actors.dev.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.HashMap;
import java.util.Map;

public class LayerSelectMenu extends HideableVisWindow implements Buildable {

    private final Map<LayerDefinition, VisTextButton> layerButtonMap = new HashMap<LayerDefinition, VisTextButton>();

    public LayerSelectMenu() {
        super("");
    }

    @Override
    public Actor build(StageHandler stageHandler) {
        final WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
        for (final LayerDefinition layerDefinition : LayerDefinition.values()) {
            VisTextButton visTextButton = new VisTextButton(layerDefinition.getLayerName());
            add(visTextButton).row();
            visTextButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    worldBuilder.setCurrentLayer(layerDefinition);
                    resetButtons();
                    setSelectedLayerButton(layerDefinition);
                }
            });

            layerButtonMap.put(layerDefinition, visTextButton);
        }

        // Now get the active layer, and disable that button (to indicate it's being used).
        // WorldBuilder class is setup first and the layer is decided then. Update the UI here.
        layerButtonMap.get(worldBuilder.getCurrentLayer()).setDisabled(true);

        addCloseButton();
        pack();
        stopWindowClickThrough();

        setPosition(Gdx.graphics.getWidth() - getWidth() - StageHandler.WINDOW_PAD_X,
                Gdx.graphics.getHeight() - getHeight() - StageHandler.WINDOW_PAD_Y);
        return this;
    }

    private void resetButtons() {
        for (VisTextButton visTextButton : layerButtonMap.values()) {
            visTextButton.setDisabled(false);
        }
    }

    public void setSelectedLayerButton(LayerDefinition layerDefinition) {
        resetButtons();
        layerButtonMap.get(layerDefinition).setDisabled(true);
    }
}
