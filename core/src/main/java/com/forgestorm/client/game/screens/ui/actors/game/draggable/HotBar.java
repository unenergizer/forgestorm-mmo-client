package com.forgestorm.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.KeyBinds;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.game.EscapeWindow;
import com.forgestorm.client.game.screens.ui.actors.game.paging.SkillBookWindow;
import com.forgestorm.client.game.world.item.inventory.InventoryActions;
import com.forgestorm.client.network.game.packet.out.InventoryPacketOut;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.item.ItemStackType;
import com.forgestorm.shared.game.world.item.inventory.InventoryConstants;
import com.forgestorm.shared.game.world.item.inventory.InventoryType;
import com.forgestorm.shared.io.type.GameAtlas;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import lombok.Getter;

public class HotBar extends VisTable implements Buildable {

    private static final int BUTTON_PADDING = 3;
    private static final int BUTTON_TO_BUTTON_SPACE = 5;

    private final ClientMain clientMain;
    private StageHandler stageHandler;

    @Getter
    private ItemSlotContainer itemSlotContainer;

    @Getter
    private float itemStackTableWidth;

    public HotBar(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        itemSlotContainer = new ItemSlotContainer(stageHandler.getClientMain(), this, InventoryConstants.HOT_BAR_SIZE);
        DragAndDrop dragAndDrop = stageHandler.getDragAndDrop();
        final VisTable otherTable = buildOtherButtons(stageHandler);

        VisTable itemStackTable = new VisTable();
        int columnCount = 0;
        for (byte i = 0; i < InventoryConstants.HOT_BAR_SIZE; i++) {

            // Create a slot for items
            ItemStackSlot itemStackSlot = new ItemStackSlot(stageHandler.getClientMain(), itemSlotContainer, InventoryType.HOT_BAR, 48, i);
            itemStackSlot.build(stageHandler);

            itemStackTable.add(itemStackSlot); // Add slot to BagWindow
            dragAndDrop.addSource(new ItemStackSource(stageHandler, dragAndDrop, itemStackSlot));
            dragAndDrop.addTarget(new ItemStackTarget(stageHandler.getClientMain(), itemStackSlot));

            itemSlotContainer.itemStackSlots[i] = itemStackSlot;
            columnCount++;

            if (columnCount == InventoryConstants.HOT_BAR_WIDTH) {
                itemStackTable.row();
                columnCount = 0;
            }
        }


        add(itemStackTable).padRight(1);
        add(otherTable).align(Alignment.CENTER.getAlignment());

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {

            }
        });

        pack();

        findPosition(otherTable.getWidth());
        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                findPosition(otherTable.getWidth());
            }
        });

        itemStackTableWidth = itemStackTable.getWidth();
        pack();
        setVisible(false);
        return this;
    }

    private void findPosition(float otherTableWidth) {
        final float x = (Gdx.graphics.getWidth() / 2f) - (getWidth() / 2) + (otherTableWidth / 2);
        final float y = StageHandler.WINDOW_PAD_Y;

        setPosition(x, y);
    }

    private VisTable buildOtherButtons(final StageHandler stageHandler) {
        VisTable buttonTable = new VisTable();

        ImageBuilder imageBuilder = new ImageBuilder(stageHandler.getClientMain(), GameAtlas.ITEMS, 28);
        VisImageButton escMenuButton = new VisImageButton(imageBuilder.setRegionName("drops_50").buildTextureRegionDrawable(), "Main Menu (" + KeyBinds.printKey(KeyBinds.ESCAPE_ACTION) + ")");
        VisImageButton spellBookButton = new VisImageButton(imageBuilder.setRegionName("quest_165").buildTextureRegionDrawable(), "Spell Book & Abilities (" + KeyBinds.printKey(KeyBinds.SPELL_BOOK) + ")");
        VisImageButton characterButton = new VisImageButton(imageBuilder.setRegionName("skill_168").buildTextureRegionDrawable(), "Character (" + KeyBinds.printKey(KeyBinds.EQUIPMENT_WINDOW) + ")");
        VisImageButton inventoryButton = new VisImageButton(imageBuilder.setRegionName("quest_121").buildTextureRegionDrawable(), "Inventory (" + KeyBinds.printKey(KeyBinds.INVENTORY_WINDOW) + ")");

        buttonTable.add(escMenuButton).padRight(BUTTON_PADDING).padLeft(BUTTON_TO_BUTTON_SPACE);
        buttonTable.add(spellBookButton).padRight(BUTTON_PADDING);
        buttonTable.add(characterButton).padRight(BUTTON_PADDING);
        buttonTable.add(inventoryButton);

        escMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(HotBar.class, (short) 0);
                EscapeWindow escapeWindow = stageHandler.getEscapeWindow();
                if (!escapeWindow.isVisible()) {

                    // Close all open windows
                    ActorUtil.fadeOutWindow(stageHandler.getMainSettingsWindow());
                    ActorUtil.fadeOutWindow(stageHandler.getBagWindow());
                    ActorUtil.fadeOutWindow(stageHandler.getEquipmentWindow());
                    clientMain.getEntityManager().getPlayerClient().closeBankWindow();
                    stageHandler.getPagedItemStackWindow().closePagedWindow(false);
                    ActorUtil.fadeOutWindow(stageHandler.getHelpWindow());
                    ActorUtil.fadeOutWindow(stageHandler.getCreditsWindow());
                    ActorUtil.fadeOutWindow(stageHandler.getEntityDropDownMenu());
                    ActorUtil.fadeOutWindow(stageHandler.getItemDropDownMenu());
                    ActorUtil.fadeOutWindow(stageHandler.getSpellBookWindow());

                    ActorUtil.fadeInWindow(escapeWindow);
                } else {
                    ActorUtil.fadeOutWindow(escapeWindow);
                }
            }
        });

        spellBookButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(HotBar.class, (short) 0);
                SkillBookWindow skillBookWindow = stageHandler.getSpellBookWindow();
                if (!skillBookWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    skillBookWindow.openWindow();
                } else if (skillBookWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    ActorUtil.fadeOutWindow(skillBookWindow);
                }
            }
        });

        characterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(HotBar.class, (short) 0);
                EquipmentWindow equipmentWindow = stageHandler.getEquipmentWindow();
                if (!equipmentWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    equipmentWindow.openWindow();
                } else if (equipmentWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    ActorUtil.fadeOutWindow(equipmentWindow);
                }
            }
        });


        inventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(HotBar.class, (short) 0);
                BagWindow bagWindow = stageHandler.getBagWindow();
                if (!bagWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    stageHandler.getBagWindow().openWindow();
                } else if (bagWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    stageHandler.getBagWindow().closeWindow();
                }
            }
        });

        return buttonTable;
    }

    public void hotBarInteract(byte slotIndex) {
        ItemStackSlot sourceSlot = itemSlotContainer.getItemStackSlot(slotIndex);
        ItemStack itemStack = sourceSlot.getItemStack();


        if (itemStack == null) return;

        ItemStackType itemStackType = itemStack.getItemStackType();

        if (itemStackType.isEquipable()) {
            stageHandler.getClientMain().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), itemStack);
            stageHandler.getEquipmentWindow().equipItem(itemStack, sourceSlot);
        } else if (itemStackType.isConsumable()) {
            stageHandler.getClientMain().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), itemStack);
            new InventoryPacketOut(clientMain, new InventoryActions(
                    InventoryActions.ActionType.CONSUME,
                    InventoryType.HOT_BAR.getInventoryTypeIndex(),
                    slotIndex)).sendPacket();
        } else if (itemStack.getSkillID() != null) {
            // Magic and/or Abilities
            itemSlotContainer.magicItemInteract(sourceSlot, itemStack);
        }
    }
}
