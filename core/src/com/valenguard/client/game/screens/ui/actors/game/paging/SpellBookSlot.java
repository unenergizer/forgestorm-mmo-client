package com.valenguard.client.game.screens.ui.actors.game.paging;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.game.draggable.ItemStackToolTip;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.io.type.GameAtlas;

class SpellBookSlot extends PagedWindowSlot {

    private final StageHandler stageHandler;
    private final ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);

    /**
     * The {@link ItemStack} that is being sold by the vendor
     */
    private final ItemStack itemStack;

    /**
     * The price of the {@link ItemStack}
     */
    private final int price;

    private ItemStackToolTip itemStackToolTip;

    private short slotID;

    SpellBookSlot(StageHandler stageHandler, ItemStack itemStack, int price, short slotID) {
        this.stageHandler = stageHandler;
        this.itemStack = itemStack;
        this.price = price;
        this.slotID = slotID;
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

            VisTable priceTable = new VisTable();
            VisTextButton button = new VisTextButton("Buy");
            priceTable.add(button);
            priceTable.add(new ImageBuilder(GameAtlas.ITEMS, 16).setRegionName("drops_44").buildVisImage());
            priceTable.add(new VisLabel(Integer.toString(price)));
            slotTable.add(priceTable).growX().align(Alignment.BOTTOM_RIGHT.getAlignment());

            add(slotTable).growX();

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(PagedWindow.class, (short) 0);
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
