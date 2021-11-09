package com.forgestorm.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.game.ExperienceBar;
import com.forgestorm.client.game.screens.ui.actors.game.ItemDropDownMenu;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.item.ItemStackType;
import com.forgestorm.shared.game.world.item.inventory.InventoryConstants;
import com.forgestorm.shared.game.world.item.inventory.InventoryType;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class BankWindow extends ItemSlotContainerWindow implements Buildable {

    private StageHandler stageHandler;

    public BankWindow() {
        super("Bank", InventoryConstants.BANK_SIZE, InventoryType.BANK);
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        DragAndDrop dragAndDrop = stageHandler.getDragAndDrop();
        addCloseButton(new CloseButtonCallBack() {
            @Override
            public void closeButtonClicked() {
                ItemDropDownMenu itemDropDownMenu = ClientMain.getInstance().getStageHandler().getItemDropDownMenu();
                if (itemDropDownMenu.getInventoryType() == getInventoryType()) itemDropDownMenu.cleanUpDropDownMenu(true);
            }
        });
        setResizable(false);

        VisTable slotTable = new VisTable();

        int columnCount = 0;
        for (byte i = 0; i < InventoryConstants.BANK_SIZE; i++) {

            // Create a slot for items
            ItemStackSlot itemStackSlot = new ItemStackSlot(getItemSlotContainer(), InventoryType.BANK, 32, i);
            itemStackSlot.build(stageHandler);

            slotTable.add(itemStackSlot); // Add slot to BagWindow
            dragAndDrop.addSource(new ItemStackSource(stageHandler, dragAndDrop, itemStackSlot));
            dragAndDrop.addTarget(new ItemStackTarget(itemStackSlot));

            getItemSlotContainer().itemStackSlots[i] = itemStackSlot;
            columnCount++;

            if (columnCount == InventoryConstants.BANK_WIDTH) {
                slotTable.row();
                columnCount = 0;
            }
        }

        VisTextButton depositHotBarItems = new VisTextButton("Deposit Tool Bar");
        VisTextButton depositBagItems = new VisTextButton("Deposit Bag");
        VisTextButton depositWornItems = new VisTextButton("Deposit Equipment");

        VisTable buttonTable = new VisTable();
        buttonTable.add(depositHotBarItems).pad(2).align(Alignment.RIGHT.getAlignment());
        buttonTable.add(depositBagItems).pad(2).align(Alignment.RIGHT.getAlignment());
        buttonTable.add(depositWornItems).pad(2).align(Alignment.RIGHT.getAlignment());

        depositHotBarItems.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                depositItems(stageHandler.getHotBar().getItemSlotContainer());
            }
        });

        depositBagItems.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                depositItems(stageHandler.getBagWindow().getItemSlotContainer());
            }
        });

        depositWornItems.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                depositItems(stageHandler.getEquipmentWindow().getItemSlotContainer());
            }
        });

        add(slotTable).row();
        add(buttonTable).align(Alignment.RIGHT.getAlignment());

        stopWindowClickThrough();

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {

            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                findWindowPosition(false);
            }
        });

        pack();
        findWindowPosition(false);
        setVisible(false);
        return this;
    }

    private void depositItems(ItemSlotContainer itemSlotContainer) {
        for (ItemStackSlot itemStackSlot : itemSlotContainer.itemStackSlots) {
            if (itemStackSlot.getItemStack() == null) continue;
            if (itemStackSlot.getItemStack().getItemStackType() == ItemStackType.BOOK_SKILL) continue;

            ItemStackSlot targetItemStackSlot = getItemSlotContainer().getFreeItemStackSlot(itemStackSlot.getItemStack());

            if (targetItemStackSlot != null) {
                ItemStack targetItemStack = targetItemStackSlot.getItemStack();
                new InventoryMoveActions().moveItems(itemStackSlot, targetItemStackSlot, itemStackSlot.getItemStack(), targetItemStack);
            } else {
                new InventoryMoveActions().moveItems(itemStackSlot, null, itemStackSlot.getItemStack(), null);
            }
        }
    }

    public void openWindow() {
        BagWindow bagWindow = stageHandler.getBagWindow();
        if (!bagWindow.isVisible()) ActorUtil.fadeInWindow(bagWindow);
        ActorUtil.fadeInWindow(this);
        findWindowPosition(false);
    }

    void findWindowPosition(boolean ignoreBagVisible) {
        BagWindow bagWindow = stageHandler.getBagWindow();
        float bagWindowY = bagWindow.getY() + bagWindow.getHeight();
        float bankWindowX = stageHandler.getStage().getViewport().getScreenWidth() - getWidth() - StageHandler.WINDOW_PAD_X;

        if (bagWindow.isVisible() && !ignoreBagVisible) {
            setPosition(bankWindowX, bagWindowY + StageHandler.WINDOW_PAD_Y);
        } else {

            HotBar hotBar = stageHandler.getHotBar();
            float hotBarEnd = hotBar.getX() + hotBar.getWidth() + hotBar.getPadLeft();

            if (hotBarEnd > bankWindowX) {
                ExperienceBar experienceBar = stageHandler.getExperienceBar();
                float expBarEnd = experienceBar.getX() + experienceBar.getWidth() + experienceBar.getPadLeft();

                if (expBarEnd > bankWindowX) {
                    setPosition(bankWindowX, experienceBar.getHeight() + experienceBar.getY() + StageHandler.WINDOW_PAD_Y);
                } else {
                    setPosition(bankWindowX, hotBar.getHeight() + hotBar.getY() + StageHandler.WINDOW_PAD_Y);
                }
            } else {
                setPosition(bankWindowX, StageHandler.WINDOW_PAD_Y);
            }
        }
    }

    @Override
    protected void close() {
        EntityManager.getInstance().getPlayerClient().closeBankWindow();
    }
}
