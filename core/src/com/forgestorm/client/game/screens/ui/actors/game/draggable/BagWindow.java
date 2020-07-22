package com.forgestorm.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.game.ItemDropDownMenu;
import com.forgestorm.client.game.world.item.inventory.InventoryConstants;
import com.forgestorm.client.game.world.item.inventory.InventoryType;

public class BagWindow extends ItemSlotContainerWindow implements Buildable {

    private StageHandler stageHandler;

    public BagWindow() {
        super("Inventory", InventoryConstants.BAG_SIZE, InventoryType.BAG_1);
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        DragAndDrop dragAndDrop = stageHandler.getDragAndDrop();
        addCloseButton(new CloseButtonCallBack() {
            @Override
            public void closeButtonClicked() {
                closeWindow();
            }
        });
        setResizable(false);

        int columnCount = 0;
        for (byte i = 0; i < InventoryConstants.BAG_SIZE; i++) {

            // Create a slot for items
            ItemStackSlot itemStackSlot = new ItemStackSlot(getItemSlotContainer(), InventoryType.BAG_1, 32, i);
            itemStackSlot.build(stageHandler);

            add(itemStackSlot); // Add slot to BagWindow
            dragAndDrop.addSource(new ItemStackSource(stageHandler, dragAndDrop, itemStackSlot));
            dragAndDrop.addTarget(new ItemStackTarget(itemStackSlot));

            getItemSlotContainer().itemStackSlots[i] = itemStackSlot;
            columnCount++;

            if (columnCount == InventoryConstants.BAG_WIDTH) {
                row();
                columnCount = 0;
            }
        }

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

    public void openWindow() {
        ActorUtil.fadeInWindow(this);
        findPosition();
        stageHandler.getBankWindow().findWindowPosition(false);
    }

    public void closeWindow() {
        ActorUtil.fadeOutWindow(this);
        stageHandler.getBankWindow().findWindowPosition(true);
        ItemDropDownMenu itemDropDownMenu = ClientMain.getInstance().getStageHandler().getItemDropDownMenu();
        if (itemDropDownMenu.getInventoryType() == getInventoryType()) {
            itemDropDownMenu.cleanUpDropDownMenu(true);
        }
    }

    private void findPosition() {
        HotBar hotBar = stageHandler.getHotBar();
        float endPosition = hotBar.getX() + hotBar.getWidth() + hotBar.getPadLeft();
        float bagX = stageHandler.getStage().getViewport().getScreenWidth() - getWidth() - StageHandler.WINDOW_PAD_X;

        if (endPosition > bagX) {
            setPosition(bagX, hotBar.getHeight() + hotBar.getY() + StageHandler.WINDOW_PAD_Y);
        } else {
            setPosition(bagX, StageHandler.WINDOW_PAD_Y);
        }
    }
}
