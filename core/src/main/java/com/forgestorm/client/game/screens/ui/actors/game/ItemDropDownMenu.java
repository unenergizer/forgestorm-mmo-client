package com.forgestorm.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.LeftAlignTextButton;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.BagWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.BankWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.InventoryMoveActions;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.ItemStackSlot;
import com.forgestorm.client.game.world.item.inventory.InventoryActions;
import com.forgestorm.client.network.game.packet.out.InventoryPacketOut;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.item.ItemStackType;
import com.forgestorm.shared.game.world.item.inventory.InventoryType;
import com.kotcrab.vis.ui.widget.VisTable;
import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

public class ItemDropDownMenu extends HideableVisWindow implements Buildable {
    private final ClientMain clientMain;
    private final ItemDropDownMenu itemDropDownMenu;
    private StageHandler stageHandler;
    private final VisTable dropDownTable = new VisTable();

    @Getter
    private InventoryType inventoryType;
    private byte slotIndex;
    private ItemStackSlot sourceSlot;

    public ItemDropDownMenu(ClientMain clientMain) {
        super(clientMain, "Choose Option");
        this.clientMain = clientMain;
        this.itemDropDownMenu = this;
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;

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

        if (itemStack.getItemStackType() == ItemStackType.BOOK_SKILL) {
            addRemoveBookSkillButton(dropDownTable, itemStack, slotIndex);
        } else {
            addUnequip(dropDownTable, itemStack);
            addEquipOption(dropDownTable, itemStack);
            addDeposit(dropDownTable, itemStack);
            addWithdraw(dropDownTable, itemStack);
            addConsumeButton(dropDownTable, itemStack);
            addDropButton(dropDownTable, itemStack);
        }
        addCancelButton(dropDownTable);

        pack();
        ActorUtil.fadeInWindow(itemDropDownMenu);
        toFront();
    }

    private void addDeposit(VisTable visTable, final ItemStack itemStack) {
        if (!stageHandler.getBankWindow().isVisible()) return;
        if (inventoryType == InventoryType.BANK) return;

        LeftAlignTextButton depositItem = new LeftAlignTextButton("Deposit [YELLOW]" + itemStack.getName());
        visTable.add(depositItem).expand().fill().row();

        depositItem.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Check again, if player closes bank, the menu does not reflect that
                if (stageHandler.getBankWindow().isVisible()) {
                    BankWindow bankWindow = stageHandler.getBankWindow();
                    ItemStackSlot targetItemStackSlot = bankWindow.getItemSlotContainer().getFreeItemStackSlot(itemStack);
                    if (targetItemStackSlot != null) {
                        stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                        new InventoryMoveActions(clientMain).moveItems(sourceSlot, targetItemStackSlot, itemStack, null);
                    } else {
                        stageHandler.getChatWindow().appendChatMessage(ChatChannelType.GENERAL, "[RED]Your bank is full!");
                    }
                } else {
                    stageHandler.getChatWindow().appendChatMessage(ChatChannelType.GENERAL, "[RED]Your bank must be open to deposit items.");
                }
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addWithdraw(VisTable visTable, final ItemStack itemStack) {
        if (!stageHandler.getBankWindow().isVisible()) return;
        if (inventoryType != InventoryType.BANK) return;

        LeftAlignTextButton withdrawItem = new LeftAlignTextButton("Withdraw [YELLOW]" + itemStack.getName());
        visTable.add(withdrawItem).expand().fill().row();

        withdrawItem.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Check again, if player closes bank, the menu does not reflect that
                if (stageHandler.getBankWindow().isVisible()) {
                    BagWindow bagWindow = stageHandler.getBagWindow();
                    ItemStackSlot targetItemStackSlot = bagWindow.getItemSlotContainer().getFreeItemStackSlot(itemStack);
                    if (targetItemStackSlot != null) {
                        stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                        new InventoryMoveActions(clientMain).moveItems(sourceSlot, targetItemStackSlot, itemStack, null);
                    } else {
                        stageHandler.getChatWindow().appendChatMessage(ChatChannelType.GENERAL, "[RED]Your inventory is full!");
                    }
                } else {
                    stageHandler.getChatWindow().appendChatMessage(ChatChannelType.GENERAL, "[RED]Your bank must be open to withdraw items.");
                }
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addUnequip(VisTable visTable, final ItemStack itemStack) {
        if (inventoryType != InventoryType.EQUIPMENT) return;

        LeftAlignTextButton dropItemStackButton = new LeftAlignTextButton("UnEquip [YELLOW]" + itemStack.getName());
        visTable.add(dropItemStackButton).expand().fill().row();

        dropItemStackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                stageHandler.getEquipmentWindow().unequipItem(stageHandler.getBagWindow().getItemSlotContainer(), itemStack, sourceSlot);
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addEquipOption(VisTable visTable, final ItemStack itemStack) {
        if (inventoryType == InventoryType.EQUIPMENT) return;
        if (!itemStack.getItemStackType().isEquipable()) return;

        LeftAlignTextButton dropItemStackButton = new LeftAlignTextButton("Equip [YELLOW]" + itemStack.getName());
        visTable.add(dropItemStackButton).expand().fill().row();

        dropItemStackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                stageHandler.getEquipmentWindow().equipItem(itemStack, sourceSlot);
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addConsumeButton(VisTable visTable, ItemStack itemStack) {
        if (!itemStack.isConsumable()) return;

        LeftAlignTextButton dropItemStackButton = new LeftAlignTextButton("Consume [YELLOW]" + itemStack.getName());
        visTable.add(dropItemStackButton).expand().fill().row();

        dropItemStackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                new InventoryPacketOut(clientMain, new InventoryActions(
                        InventoryActions.ActionType.CONSUME,
                        inventoryType.getInventoryTypeIndex(),
                        slotIndex)).sendPacket();
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addDropButton(VisTable visTable, ItemStack itemStack) {
        if (inventoryType == InventoryType.EQUIPMENT || inventoryType == InventoryType.BANK) return;

        LeftAlignTextButton dropItemStackButton = new LeftAlignTextButton("Drop [YELLOW]" + itemStack.getName());
        visTable.add(dropItemStackButton).expand().fill().row();

        dropItemStackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                new InventoryPacketOut(clientMain, new InventoryActions(InventoryActions.ActionType.DROP, inventoryType.getInventoryTypeIndex(), slotIndex)).sendPacket();
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addRemoveBookSkillButton(VisTable visTable, final ItemStack itemStack, final byte slotIndex) {
        LeftAlignTextButton removeItemStackButton = new LeftAlignTextButton("Remove Skill [YELLOW]" + itemStack.getName());
        visTable.add(removeItemStackButton).expand().fill().row();

        removeItemStackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                sourceSlot.getItemSlotContainer().removeItemStack(slotIndex);
                // TODO: Tell server the skill (ItemStack) was removed? SEND REMOVE PACKET!
                println(ItemDropDownMenu.class, "Remove Skill Button clicked");

                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                new InventoryPacketOut(clientMain, new InventoryActions(InventoryActions.ActionType.REMOVE, inventoryType.getInventoryTypeIndex(), slotIndex)).sendPacket();
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addCancelButton(VisTable visTable) {
        LeftAlignTextButton cancelButton = new LeftAlignTextButton("Cancel");
        visTable.add(cancelButton).expand().fill().row();

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(ItemDropDownMenu.class, (short) 0);
                cleanUpDropDownMenu(true);
            }
        });
    }

    public void cleanUpDropDownMenu(boolean closeWindow) {
        if (closeWindow) ActorUtil.fadeOutWindow(itemDropDownMenu);
        dropDownTable.clearChildren();
    }
}
