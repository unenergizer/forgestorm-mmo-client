package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextArea;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;

public class ItemStackToolTip extends HideableVisWindow implements Buildable {

    private VisTable toolTipTable = new VisTable();
    private VisLabel uuidLabel = new VisLabel();
    private VisLabel typeLabel = new VisLabel();
    private VisLabel nameLabel = new VisLabel();
    private VisTextArea descTextArea = new VisTextArea();

    public ItemStackToolTip() {
        super("");
    }

    @Override
    public Actor build() {
        pad(3);
        toolTipTable.add(uuidLabel).row();
        toolTipTable.add(typeLabel).row();
        toolTipTable.add(nameLabel).row();
        toolTipTable.add(descTextArea).row();
        add(toolTipTable);
        pack();
        setVisible(false);
        return this;
    }

    void updateToolTipText(ItemStack itemStack) {
        uuidLabel.setText("ID: " + Integer.toString(itemStack.getItemId()));
        typeLabel.setText("Type: " + itemStack.getItemStackType().name());
        nameLabel.setText("Name: " + itemStack.getName());
        descTextArea.setText("Description: " + itemStack.getDescription());
        descTextArea.setPrefRows(3);
        pack();
    }
}
