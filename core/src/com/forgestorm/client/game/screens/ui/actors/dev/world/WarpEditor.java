package com.forgestorm.client.game.screens.ui.actors.dev.world;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.MoveDirection;
import com.forgestorm.client.network.game.packet.out.TileWarpPacketOut;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

public class WarpEditor extends HideableVisWindow implements Buildable {

    private final VisValidatableTextField fromWorldX = new VisValidatableTextField();
    private final VisValidatableTextField fromWorldY = new VisValidatableTextField();
    private final VisValidatableTextField toWorldName = new VisValidatableTextField();
    private final VisValidatableTextField toWorldX = new VisValidatableTextField();
    private final VisValidatableTextField toWorldY = new VisValidatableTextField();
    private final VisSelectBox<MoveDirection> facingDirection = new VisSelectBox<MoveDirection>();

    public WarpEditor() {
        super("Warp Editor");
    }

    private void resetFields() {
        fromWorldX.setText("");
        fromWorldY.setText("");
        toWorldName.setText("");
        toWorldX.setText("");
        toWorldY.setText("");
        facingDirection.setSelected(MoveDirection.NONE);

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        if (playerClient != null) {
            Location location = playerClient.getCurrentMapLocation();
            if (location != null) {
                toWorldName.setText(location.getWorldName());
            }
        }
    }

    @Override
    public Actor build(StageHandler stageHandler) {
        VisTable contentTable = new VisTable(true);
        VisTextButton saveButton = new VisTextButton("Save");
        final VisLabel errorLabel = new VisLabel();
        FormValidator validator = new FormValidator(saveButton, errorLabel);

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (facingDirection.getSelected() == MoveDirection.NONE) {
                    errorLabel.setText("[RED]Facing direction can not be NONE!");
                } else {
                    new TileWarpPacketOut(
                            Integer.parseInt(fromWorldX.getText()),
                            Integer.parseInt(fromWorldY.getText()),
                            toWorldName.getText(),
                            Integer.parseInt(toWorldX.getText()),
                            Integer.parseInt(toWorldY.getText()),
                            facingDirection.getSelected()).sendPacket();
                    resetFields();
                    errorLabel.setText("[GREEN]Sent warp data to server!");
                }
            }
        });

        validator.notEmpty(fromWorldX, "This field must not be empty.");
        validator.integerNumber(fromWorldX, "Must contain an integer value.");
        validator.notEmpty(fromWorldY, "This field must not be empty.");
        validator.integerNumber(fromWorldY, "Must contain an integer value.");
        validator.notEmpty(toWorldName, "This field must not be empty.");
        validator.notEmpty(toWorldX, "This field must not be empty.");
        validator.integerNumber(toWorldX, "Must contain an integer value.");
        validator.notEmpty(toWorldY, "This field must not be empty.");
        validator.integerNumber(toWorldY, "Must contain an integer value.");

        // Map location warp field exists at
        VisTable fromTable = new VisTable();
        fromTable.add(new VisLabel("From Location: ")).colspan(2).row();
        fromTable.add(new VisLabel("X: "));
        fromTable.add(fromWorldX).row();
        fromTable.add(new VisLabel("Y: "));
        fromTable.add(fromWorldY).row();
        contentTable.add(fromTable).row();

        // Location to go to when player enters the warp
        // x, y, map name, facing direction
        VisTable toTable = new VisTable();
        facingDirection.setItems(MoveDirection.values());
        facingDirection.setSelected(MoveDirection.NONE);

        toTable.add(new VisLabel("To Location: ")).colspan(2).row();
        toTable.add(new VisLabel("World: "));
        toTable.add(toWorldName).row();
        toTable.add(new VisLabel("X: "));
        toTable.add(toWorldX).row();
        toTable.add(new VisLabel("Y: "));
        toTable.add(toWorldY).row();
        toTable.add(new VisLabel("Facing: "));
        toTable.add(facingDirection).row();
        contentTable.add(toTable).row();

        // Save button and error text
        contentTable.add(errorLabel).row();

        VisTable buttonTable = new VisTable();
        VisTextButton resetButton = new VisTextButton("Reset");
        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetFields();
            }
        });

        buttonTable.add(saveButton);
        buttonTable.add(resetButton);
        contentTable.add(buttonTable).row();

        add(contentTable);

        pack();
        centerWindow();
        addCloseButton();
        stopWindowClickThrough();
        return this;
    }
}
