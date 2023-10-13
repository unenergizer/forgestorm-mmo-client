package com.forgestorm.client.game.screens.ui.actors.game.paging;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.rpg.EntityShopManager;
import com.forgestorm.client.game.rpg.ShopOpcodes;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.item.ItemStackManager;
import com.forgestorm.client.game.world.item.inventory.ShopItemStackInfo;
import com.forgestorm.client.network.game.packet.out.EntityShopPacketOut;
import com.forgestorm.shared.game.world.item.ItemStack;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class EntityShopWindow extends PagedWindow {

    private final ClientMain clientMain;
    @Getter
    private MovingEntity shopOwnerEntity;
    private short shopID;

    public EntityShopWindow(ClientMain clientMain) {
        super(clientMain, "NULL", 2, 6);
        this.clientMain = clientMain;
    }

    public void openWindow(MovingEntity movingEntity, short shopID) {
        this.shopID = shopID;
        getTitleLabel().setText(movingEntity.getEntityName() + "'s Shop");
        loadPagedWindow();
    }

    @Override
    void windowClosedAction() {
        new EntityShopPacketOut(clientMain, ShopOpcodes.STOP_SHOPPING).sendPacket();
        shopOwnerEntity = null;
        shopID = -1;
    }

    @Override
    List<PagedWindowSlot> loadPagedWindowSlots() {
        EntityShopManager entityShopManager = stageHandler.getClientMain().getEntityShopManager();
        ItemStackManager itemStackManager = stageHandler.getClientMain().getItemStackManager();

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
