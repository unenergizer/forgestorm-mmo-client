package com.forgestorm.client.game.screens.ui.actors.game.paging;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.rpg.ShopOpcodes;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.ItemStackToolTip;
import com.forgestorm.client.network.game.packet.out.EntityShopPacketOut;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.io.type.GameAtlas;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

class EntityShopSlot extends PagedWindowSlot {

    private final ClientMain clientMain;
    private final StageHandler stageHandler;
    private final ImageBuilder imageBuilder;

    /**
     * The {@link ItemStack} that is being sold by the vendor
     */
    private final ItemStack itemStack;

    /**
     * The price of the {@link ItemStack}
     */
    private final int price;

    private ItemStackToolTip itemStackToolTip;

    private final short slotID;

    EntityShopSlot(StageHandler stageHandler, ItemStack itemStack, int price, short slotID) {
        this.clientMain = stageHandler.getClientMain();
        this.stageHandler = stageHandler;
        this.imageBuilder = new ImageBuilder(clientMain, GameAtlas.ITEMS, 32);
        this.itemStack = itemStack;
        this.price = price;
        this.slotID = slotID;
    }

    @Override
    void buildSlot() {
        VisImage itemStackImage;
        if (itemStack != null) {
            // Here were setting up the slot and locking the ItemStackSlot
            itemStackImage = imageBuilder.setRegionName(itemStack.getTextureRegionName()).buildVisImage();
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
            priceTable.add(new ImageBuilder(clientMain, GameAtlas.ITEMS, 16).setRegionName("drops_44").buildVisImage());
            priceTable.add(new VisLabel(Integer.toString(price)));
            slotTable.add(priceTable).growX().align(Alignment.BOTTOM_RIGHT.getAlignment());

            add(slotTable).growX();

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    clientMain.getAudioManager().getSoundManager().playSoundFx(PagedWindow.class, (short) 0);
                    // Send packet here
                    new EntityShopPacketOut(clientMain, ShopOpcodes.BUY, slotID).sendPacket();
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
