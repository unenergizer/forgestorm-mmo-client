package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.shared.game.world.tile.properties.TilePropertyTypes;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
@Setter
public class InteractDamageProperty extends AbstractTileProperty {

    private InteractType interactType;
    private Integer interactDamage;

    public InteractDamageProperty() {
        super(TilePropertyTypes.INTERACT_DAMAGE);
    }

    @Override
    VisTable buildActors() {
        VisTable mainTable = new VisTable(true);

        // Tile deals damage
        final VisSelectBox<InteractType> interactTypeVisSelectBox = new VisSelectBox<InteractType>();
        interactTypeVisSelectBox.setItems(InteractType.values());
        if (interactType != null) interactTypeVisSelectBox.setSelected(interactType);

        final VisTextField amountOfDamage = new VisTextField();
        amountOfDamage.setDisabled(true);
        if (interactDamage != null) amountOfDamage.setText(interactDamage.toString());

        mainTable.add(interactTypeVisSelectBox);
        mainTable.add(amountOfDamage);

        amountOfDamage.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                interactDamage = Integer.valueOf(amountOfDamage.getText());
            }
        });

        return mainTable;
    }

    @Override
    public AbstractTileProperty load(Map<String, Object> tileProperties, boolean printDebugMessages) {

        // Take damage from walking over tile
        String interactType = (String) tileProperties.get("cursorDrawType");
        if (interactType != null) setInteractType(InteractType.valueOf(interactType));

        Integer walkOverDamage = (Integer) tileProperties.get("interactDamage");
        if (walkOverDamage != null) setInteractDamage(walkOverDamage);

        println(getClass(), "cursorDrawType: " + interactType, false, printDebugMessages);
        println(getClass(), "interactDamage: " + walkOverDamage, false, printDebugMessages);

        return this;
    }

    private enum InteractType {
        BUTTON_CLICK,
        WALK_OVER
    }
}
