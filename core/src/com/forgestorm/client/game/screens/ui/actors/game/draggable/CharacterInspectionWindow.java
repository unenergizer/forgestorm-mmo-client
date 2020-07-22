package com.forgestorm.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatWindow;
import com.forgestorm.client.game.world.entities.Player;
import com.forgestorm.client.game.world.item.ItemStack;
import com.forgestorm.client.game.world.item.inventory.InventoryType;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CharacterInspectionWindow extends ItemSlotContainerWindow implements Buildable {

    private StageHandler stageHandler;
    private EquipmentPreview equipmentPreview = new EquipmentPreview();

    @Setter
    private Player playerToInspect;

    public CharacterInspectionWindow() {
        super("Inspect Character", ClientConstants.EQUIPMENT_INVENTORY_SIZE, InventoryType.EQUIPMENT);
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        addCloseButton();
        setResizable(false);

        Actor equipmentSlotsTable = equipmentPreview.build(stageHandler, getItemSlotContainer(), getItemSlotContainer().itemStackSlots);
        add(equipmentSlotsTable).grow().align(Align.top).padRight(10);

        stopWindowClickThrough();

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {

            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                findPosition();
            }
        });

        pack();
        findPosition();
        setVisible(false);
        return this;
    }

    private void findPosition() {
        ChatWindow chatWindow = stageHandler.getChatWindow();
        float y = chatWindow.getY() + chatWindow.getHeight() + 15;
        setPosition(StageHandler.WINDOW_PAD_X, y);
    }

    public void inspectCharacter(int[] itemIds) {
        getTitleLabel().setText("Inspecting: " + playerToInspect.getEntityName());
        equipmentPreview.resetFacingDirection();
        equipmentPreview.rebuildPreviewTable(playerToInspect);

        // Clear old items
        getItemSlotContainer().resetItemSlotContainer();

        // Set Items
        for (int itemId : itemIds) {
            if (itemId == -1) continue;
            ItemStack itemStack = ClientMain.getInstance().getItemStackManager().makeItemStack(itemId, 0);
            ItemStackSlot targetSlot = equipmentPreview.getItemStackSlot(itemStack.getItemStackType());
            targetSlot.setCharacterInspectionSlot(true); // Prevent Item from being moved
            targetSlot.setItemStack(itemStack);
        }

        if (!isVisible()) ActorUtil.fadeInWindow(this);
    }
}
