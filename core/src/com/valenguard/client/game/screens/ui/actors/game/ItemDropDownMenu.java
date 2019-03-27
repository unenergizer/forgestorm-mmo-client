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
import com.valenguard.client.game.world.item.inventory.InventoryType;
import com.valenguard.client.network.game.packet.out.InventoryPacketOut;

import static com.valenguard.client.util.Log.println;

public class ItemDropDownMenu extends HideableVisWindow implements Buildable {

    private VisTable dropDownTable = new VisTable();
    private byte slotIndex;

    public ItemDropDownMenu() {
        super("Choose Option");
    }

    @Override
    public Actor build() {

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

    public void toggleMenu(ItemStack itemStack, byte slotIndex, float x, float y) {
        cleanUpDropDownMenu(false);
        dropDownTable = new VisTable();
        setPosition(x, y);
        this.slotIndex = slotIndex;

        addDropButton(dropDownTable);
        addCancelButton(dropDownTable);

        add(dropDownTable).expand().fill();

        pack();
        ActorUtil.fadeInWindow(this);
    }

    private void addDropButton(VisTable visTable) {
        VisTextButton dropItemStackButton = new VisTextButton("Drop");
        visTable.add(dropItemStackButton).expand().fill().row();

        dropItemStackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new InventoryPacketOut(new InventoryActions(InventoryActions.DROP, InventoryType.BAG_1.getInventoryTypeIndex(), slotIndex)).sendPacket();
                ActorUtil.getStageHandler().getBagWindow().getItemStackSlot(slotIndex).setMoveSlotLocked(true);
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
        if (closeWindow) ActorUtil.fadeOutWindow(this);
        boolean removed = dropDownTable.remove();
        println(getClass(), "dropDownTable: " + removed);
    }
}
