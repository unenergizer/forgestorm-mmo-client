package com.forgestorm.client.game.screens.ui.actors.game.paging;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.rpg.EntityShopManager;
import com.forgestorm.client.game.rpg.ShopOpcodes;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.item.ItemStackManager;
import com.forgestorm.client.game.world.item.inventory.ShopItemStackInfo;
import com.forgestorm.client.network.game.packet.out.EntityShopPacketOut;
import com.forgestorm.shared.game.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class EntityShopWindow extends PagedWindow {

    @Getter
    private MovingEntity shopOwnerEntity;
    private short shopID;

    public EntityShopWindow() {
        super("NULL", 2, 6);
    }

    public void openWindow(MovingEntity movingEntity, short shopID) {
        this.shopID = shopID;
        getTitleLabel().setText(movingEntity.getEntityName() + "'s Shop");
        loadPagedWindow();
    }

    @Override
    void windowClosedAction() {
        new EntityShopPacketOut(ShopOpcodes.STOP_SHOPPING).sendPacket();
        shopOwnerEntity = null;
        shopID = -1;
    }

    @Override
    List<PagedWindowSlot> loadPagedWindowSlots() {
        EntityShopManager entityShopManager = ClientMain.getInstance().getEntityShopManager();
        ItemStackManager itemStackManager = ClientMain.getInstance().getItemStackManager();

        // Generate shop slots
        List<PagedWindowSlot> windowSlots = new ArrayList<PagedWindowSlot>();
        for (int i = 0; i < entityShopManager.getShopItemList(shopID).size(); i++) {
            ShopItemStackInfo shopItemStackInfo = entityShopManager.getShopItemStackInfo(shopID, i);
            ItemStack itemStack = itemStackManager.makeItemStack(entityShopManager.getItemIdForShop(shopID, i), 1);
            windowSlots.add(new EntityShopSlot(stageHandler, itemStack, shopItemStackInfo.getPrice(), (short) i));
        }

        return windowSlots;
    }
}
