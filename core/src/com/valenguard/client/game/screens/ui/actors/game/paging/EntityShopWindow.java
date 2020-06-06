package com.valenguard.client.game.screens.ui.actors.game.paging;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.rpg.EntityShopAction;
import com.valenguard.client.game.rpg.EntityShopManager;
import com.valenguard.client.game.rpg.ShopOpcodes;
import com.valenguard.client.game.world.entities.MovingEntity;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackManager;
import com.valenguard.client.game.world.item.inventory.ShopItemStackInfo;
import com.valenguard.client.network.game.packet.out.EntityShopPacketOut;

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
        new EntityShopPacketOut(new EntityShopAction(ShopOpcodes.STOP_SHOPPING)).sendPacket();
        shopOwnerEntity = null;
        shopID = -1;
    }

    @Override
    List<PagedWindowSlot> loadPagedWindowSlots() {
        EntityShopManager entityShopManager = Valenguard.getInstance().getEntityShopManager();
        ItemStackManager itemStackManager = Valenguard.getInstance().getItemStackManager();

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
