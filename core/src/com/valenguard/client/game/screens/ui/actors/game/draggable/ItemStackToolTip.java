package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextArea;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;

public class ItemStackToolTip extends HideableVisWindow implements Buildable {

    private final VisTable toolTipTable = new VisTable();
    private final VisLabel nameLabel = new VisLabel();
    private final VisLabel typeLabel = new VisLabel();
    private final VisTextArea descTextArea = new VisTextArea();

    ItemStackToolTip() {
        super("");
    }

    @Override
    public Actor build() {
        pad(3);
        toolTipTable.add(nameLabel).padBottom(3).row();
        toolTipTable.add(typeLabel).left().row();
        toolTipTable.add(descTextArea).left().row();
        add(toolTipTable);
        pack();
        setVisible(false);
        return this;
    }

    /**
     * An {@link ItemStack} has been dropped into a {@link ItemStackSlot}. Now generate a tool tip
     * to display {@link ItemStack} specific information to the player
     *
     * @param itemStack The {@link ItemStack} to get tool tip information for.
     */
    void updateToolTipText(ItemStack itemStack) {
        nameLabel.setText("[ID: " + Integer.toString(itemStack.getItemId()) + "] " + itemStack.getName());
        typeLabel.setText(itemStack.getItemStackType().name());
        descTextArea.setText(itemStack.getDescription());
        descTextArea.setPrefRows(3);
        pack();
    }
}
