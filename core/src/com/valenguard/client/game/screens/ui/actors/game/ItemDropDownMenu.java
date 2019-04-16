package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.inventory.InventoryActions;
import com.valenguard.client.network.game.packet.out.InventoryPacketOut;

public class ItemDropDownMenu extends HideableVisWindow implements Buildable {

    private final ItemDropDownMenu itemDropDownMenu;
    private VisTable dropDownTable = new VisTable();
    private byte inventoryIndex;
    private byte slotIndex;

    public ItemDropDownMenu() {
        super("Choose Option");
        this.itemDropDownMenu = this;
    }

    @Override
    public Actor build() {

        add(dropDownTable).grow();

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {
                cleanUpDropDownMenu(true);
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        setVisible(false);
        return this;
    }

    public void toggleMenu(ItemStack itemStack, byte inventoryIndex, byte slotIndex, float x, float y) {
        cleanUpDropDownMenu(false);
        setPosition(x, y);
        this.inventoryIndex = inventoryIndex;
        this.slotIndex = slotIndex;

        addConsumeButton(dropDownTable, itemStack);
        addDropButton(dropDownTable);
        addCancelButton(dropDownTable);

        pack();
        ActorUtil.fadeInWindow(itemDropDownMenu);
        this.setZIndex(Integer.MAX_VALUE);
    }

    private void addConsumeButton(VisTable visTable, ItemStack itemStack) {
        if (!itemStack.isConsumable()) return;

        VisTextButton dropItemStackButton = new VisTextButton("Consume");
        visTable.add(dropItemStackButton).expand().fill().row();

        dropItemStackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new InventoryPacketOut(new InventoryActions(
                        InventoryActions.ActionType.CONSUME,
                        inventoryIndex,
                        slotIndex)).sendPacket();
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addDropButton(VisTable visTable) {
        VisTextButton dropItemStackButton = new VisTextButton("Drop");
        visTable.add(dropItemStackButton).expand().fill().row();

        dropItemStackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new InventoryPacketOut(new InventoryActions(InventoryActions.ActionType.DROP, inventoryIndex, slotIndex)).sendPacket();
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addCancelButton(VisTable visTable) {
        VisTextButton cancelButton = new VisTextButton("Cancel");
        visTable.add(cancelButton).expand().fill().row();

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void cleanUpDropDownMenu(boolean closeWindow) {
        if (closeWindow) ActorUtil.fadeOutWindow(itemDropDownMenu);
        dropDownTable.clearChildren();
    }
}
