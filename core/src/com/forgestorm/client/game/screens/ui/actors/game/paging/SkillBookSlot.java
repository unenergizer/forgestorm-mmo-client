package com.forgestorm.client.game.screens.ui.actors.game.paging;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.ItemStackToolTip;
import com.forgestorm.client.game.world.item.ItemStack;
import com.forgestorm.client.io.type.GameAtlas;

class SkillBookSlot extends PagedWindowSlot {

    private final StageHandler stageHandler;
    private final ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);
    private final ItemStack itemStack;

    private ItemStackToolTip itemStackToolTip;

    SkillBookSlot(StageHandler stageHandler, ItemStack itemStack) {
        this.stageHandler = stageHandler;
        this.itemStack = itemStack;
    }

    @Override
    void buildSlot() {
        VisImage itemStackImage;
        if (itemStack != null) {
            // Here were setting up the slot and locking the ItemStackSlot
            itemStackImage = imageBuilder.setRegionName(itemStack.getTextureRegion()).buildVisImage();
        } else {
            // Set empty slot
            itemStackImage = imageBuilder.setRegionName("clear").buildVisImage();
        }

        add(itemStackImage).align(Alignment.LEFT.getAlignment()); // Set next image

        if (itemStack != null) {
            // Setup name tag and price
            VisTable slotTable = new VisTable();
            slotTable.add(itemStack.getName()).align(Alignment.TOP_LEFT.getAlignment()).row();

            VisTable visTable = new VisTable();
            VisTextButton button = new VisTextButton("Add to HotBar");
            visTable.add(button);
            slotTable.add(visTable).growX().align(Alignment.BOTTOM_RIGHT.getAlignment());

            add(slotTable).growX();

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(PagedWindow.class, (short) 0);
                    // Send packet here
                    // TODO: Send spell_book slot update here
//                    new EntityShopPacketOut(new EntityShopAction(ShopOpcodes.BUY, slotID)).sendPacket();
                }
            });

            // Setup tool tips
            if (itemStackToolTip != null) {
                itemStackToolTip.unregisterToolTip();
                itemStackToolTip = null;
            }
            itemStackToolTip = new ItemStackToolTip(stageHandler, null, itemStack, this, false);
            itemStackToolTip.registerToolTip();
            pack();
        }
    }
}
