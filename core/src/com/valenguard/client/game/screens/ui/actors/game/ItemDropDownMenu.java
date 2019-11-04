package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.screens.ui.actors.game.draggable.BagWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.EquipmentWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.InventoryMoveActions;
import com.valenguard.client.game.screens.ui.actors.game.draggable.ItemStackSlot;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackType;
import com.valenguard.client.game.world.item.inventory.InventoryActions;
import com.valenguard.client.game.world.item.inventory.InventoryType;
import com.valenguard.client.network.game.packet.out.InventoryPacketOut;

public class ItemDropDownMenu extends HideableVisWindow implements Buildable {

    private final ItemDropDownMenu itemDropDownMenu;
    private VisTable dropDownTable = new VisTable();
    private InventoryType inventoryType;
    private byte slotIndex;
    private ItemStackSlot sourceSlot;

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

    public void toggleMenu(ItemStack itemStack, InventoryType inventoryType, ItemStackSlot sourceSlot, byte slotIndex, float x, float y) {
        cleanUpDropDownMenu(false);
        setPosition(x, y);
        this.inventoryType = inventoryType;
        this.slotIndex = slotIndex;
        this.sourceSlot = sourceSlot;

        addUnequip(dropDownTable, itemStack);
        addEquipOption(dropDownTable, itemStack);
        addConsumeButton(dropDownTable, itemStack);
        addDropButton(dropDownTable);
        addCancelButton(dropDownTable);

        pack();
        ActorUtil.fadeInWindow(itemDropDownMenu);
        this.setZIndex(Integer.MAX_VALUE);
    }

    private void addUnequip(VisTable visTable, final ItemStack itemStack) {
        if (inventoryType != InventoryType.EQUIPMENT) return;

        VisTextButton dropItemStackButton = new VisTextButton("UnEquip");
        visTable.add(dropItemStackButton).expand().fill().row();

        dropItemStackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                BagWindow bagWindow = ActorUtil.getStageHandler().getBagWindow();
                if (bagWindow.isInventoryFull()) {
                    ActorUtil.getStageHandler().getChatWindow().appendChatMessage("Cannot unequip because your bag is full!");
                    ActorUtil.fadeOutWindow(itemDropDownMenu);
                    return;
                }

                ItemStackSlot targetSlot = bagWindow.getFreeItemStackSlot();

                if (targetSlot.isTradeSlotLocked() || sourceSlot.isTradeSlotLocked()
                        || sourceSlot.isMoveSlotLocked() || Valenguard.getInstance().getMoveInventoryEvents().isSyncingInventory())
                    return;

                new InventoryMoveActions().moveItems(sourceSlot, targetSlot, itemStack, targetSlot.getItemStack());
                Valenguard.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(ItemDropDownMenu.class, itemStack);

                sourceSlot.setEmptyCellImage();

                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addEquipOption(VisTable visTable, final ItemStack itemStack) {
        if (inventoryType == InventoryType.EQUIPMENT) return;
        if (!itemStack.getItemStackType().isEquipable()) return;

        VisTextButton dropItemStackButton = new VisTextButton("Equip");
        visTable.add(dropItemStackButton).expand().fill().row();

        dropItemStackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                EquipmentWindow equipmentWindow = ActorUtil.getStageHandler().getEquipmentWindow();

                ItemStackSlot targetSlot;
                if (itemStack.getItemStackType() == ItemStackType.RING) {
                    // RING 0 -> SLOT 6
                    // RING 1 -> SLOT 7

                    boolean ring0Taken = equipmentWindow.getItemStack((byte) 6) != null;
                    targetSlot = ring0Taken ? equipmentWindow.getItemStackSlot((byte) 7) : equipmentWindow.getItemStackSlot((byte) 6);
                } else {
                    targetSlot = equipmentWindow.getItemStackSlot(itemStack.getItemStackType());
                }

                if (targetSlot.isTradeSlotLocked() || sourceSlot.isTradeSlotLocked()
                        || sourceSlot.isMoveSlotLocked() || Valenguard.getInstance().getMoveInventoryEvents().isSyncingInventory())
                    return;

                boolean targetContainsItem = targetSlot.getItemStack() != null;
                new InventoryMoveActions().moveItems(sourceSlot, targetSlot, itemStack, targetSlot.getItemStack());
                Valenguard.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(ItemDropDownMenu.class, itemStack);

                if (!targetContainsItem) {
                    sourceSlot.setEmptyCellImage();
                }

                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addConsumeButton(VisTable visTable, ItemStack itemStack) {
        if (!itemStack.isConsumable()) return;

        VisTextButton dropItemStackButton = new VisTextButton("Consume");
        visTable.add(dropItemStackButton).expand().fill().row();

        dropItemStackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                new InventoryPacketOut(new InventoryActions(
                        InventoryActions.ActionType.CONSUME,
                        inventoryType.getInventoryTypeIndex(),
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
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                new InventoryPacketOut(new InventoryActions(InventoryActions.ActionType.DROP, inventoryType.getInventoryTypeIndex(), slotIndex)).sendPacket();
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
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void cleanUpDropDownMenu(boolean closeWindow) {
        if (closeWindow) ActorUtil.fadeOutWindow(itemDropDownMenu);
        dropDownTable.clearChildren();
    }
}
