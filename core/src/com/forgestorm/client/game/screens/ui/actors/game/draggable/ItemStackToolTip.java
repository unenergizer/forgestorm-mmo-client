package com.forgestorm.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextArea;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.world.item.ItemStack;
import com.forgestorm.client.game.world.item.inventory.InventoryType;

public class ItemStackToolTip extends HideableVisWindow {

    private final StageHandler stageHandler;
    private final ItemStackSlot itemStackSlot;
    private final ItemStackToolTip itemStackToolTip;

    private VisTable toolTipTable;
    private VisLabel nameLabel;
    private VisLabel typeLabel;
    private VisTextArea descTextArea;
    private VisLabel equipInfo;
    private VisLabel debugInfo;
    private InputListener inputListener;
    private final boolean showEquipHowTo;

    public ItemStackToolTip(StageHandler stageHandler, ItemStackSlot itemStackSlot, ItemStack itemStack, Actor itemStackActor, boolean showEquipHowTo) {
        super("");
        this.stageHandler = stageHandler;
        this.itemStackSlot = itemStackSlot;
        this.itemStackToolTip = this;
        this.showEquipHowTo = showEquipHowTo;
        build(itemStack, itemStackActor);
    }

    /**
     * Registers this {@link ItemStackToolTip} with the {@link com.forgestorm.client.game.screens.ui.StageHandler}
     */
    public void registerToolTip() {
        stageHandler.getStage().addActor(itemStackToolTip);
    }

    /**
     * Removes this {@link ItemStackToolTip} from the {@link com.forgestorm.client.game.screens.ui.StageHandler}
     */
    public void unregisterToolTip() {
        removeActor(toolTipTable);
        removeActor(nameLabel);
        removeActor(typeLabel);
        removeActor(descTextArea);
        removeListener(inputListener);
        remove();
    }

    /**
     * Builds this {@link ItemStackToolTip} for viewing later.
     *
     * @param itemStack      The item we will use to populate text fields.
     * @param itemStackActor The actor that this {@link ItemStackToolTip} will attach to.
     */
    private void build(ItemStack itemStack, Actor itemStackActor) {
        pad(3);

        toolTipTable = new VisTable();
        nameLabel = new VisLabel("", stageHandler.getMarkupStyle());
        typeLabel = new VisLabel("", stageHandler.getMarkupStyle());
        descTextArea = new VisTextArea();
        equipInfo = new VisLabel("", stageHandler.getMarkupStyle());
        debugInfo = new VisLabel("", stageHandler.getMarkupStyle());

        setToolTipText(itemStack);
        addToolTipListener(itemStackActor);

        toolTipTable.add(nameLabel).padBottom(3).row();
        toolTipTable.add(typeLabel).left().row();
        toolTipTable.add(descTextArea).left().row();
        if (showEquipHowTo) toolTipTable.add(equipInfo).left().row();
        toolTipTable.add(debugInfo).left().row();

        add(toolTipTable);
        pack();
        setVisible(false);
    }

    /**
     * Generate a tool tip to display {@link ItemStack} specific information to the player
     *
     * @param itemStack The {@link ItemStack} to get tool tip information for.
     */
    private void setToolTipText(ItemStack itemStack) {
        nameLabel.setText("[YELLOW]" + itemStack.getName());
        typeLabel.setText("[GRAY]" + itemStack.getItemStackType().name());
        descTextArea.setText(itemStack.getDescription());
        descTextArea.setPrefRows(3);

        // Show tool-tip help info for equipping and uneqipping items.
        if (showEquipHowTo) {
            if (itemStackSlot != null && itemStack.getItemStackType().isEquipable()) {
                InventoryType inventoryType = itemStackSlot.getInventoryType();

                if (inventoryType == InventoryType.BAG_1) {
                    equipInfo.setText("[GRAY]CTRL+Click item to equip");
                } else if (inventoryType == InventoryType.EQUIPMENT) {
                    equipInfo.setText("[GRAY]CTRL+Click item to unequip");
                }
            }
        }

        if (ClientMain.getInstance().isAdmin() || ClientMain.getInstance().isContentDeveloper())
            debugInfo.setText("[GRAY]ItemStackID: " + itemStack.getItemId() + "");
    }

    /**
     * Adds a ToolTip for an {@link ItemStack}. ToolTips contain information about the {@link ItemStack}.
     */
    private void addToolTipListener(final Actor itemStackActor) {
        itemStackActor.addListener(inputListener = new InputListener() {

            /** Called any time the mouse cursor or a finger touch is moved over an actor. On the desktop, this event occurs even when no
             * mouse buttons are pressed (pointer will be -1).
             * @param fromActor May be null.
             * @see InputEvent */
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (itemStackToolTip.isVisible()) return;
                Vector2 stageLocation = ActorUtil.getStageLocation(itemStackActor);
                itemStackToolTip.toFront();
                ActorUtil.fadeInWindow(itemStackToolTip);

                // Setting X location
                if (stageLocation.x > Gdx.graphics.getWidth() / 2f) {
                    itemStackToolTip.setX(stageLocation.x - itemStackToolTip.getWidth());
                } else {
                    itemStackToolTip.setX(stageLocation.x + itemStackActor.getWidth());
                }

                // Setting Y location
                if (stageLocation.y > Gdx.graphics.getHeight() / 2f) {
                    itemStackToolTip.setY(stageLocation.y - itemStackToolTip.getHeight());
                } else {
                    itemStackToolTip.setY(stageLocation.y + itemStackActor.getHeight());
                }
            }

            /** Called any time the mouse cursor or a finger touch is moved out of an actor. On the desktop, this event occurs even when no
             * mouse buttons are pressed (pointer will be -1).
             * @param toActor May be null.
             * @see InputEvent */
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (itemStackToolTip.isVisible()) ActorUtil.fadeOutWindow(itemStackToolTip);
            }
        });
    }
}
