package com.forgestorm.client.game.rpg;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.item.inventory.ShopItemStackInfo;

import java.util.List;
import java.util.Map;

public class EntityShopManager {

    private final Map<Short, List<ShopItemStackInfo>> map;

    public EntityShopManager(ClientMain clientMain) {
        map = clientMain.getFileManager().getEntityShopData().getShopItemListMap();
    }

    public Integer getItemIdForShop(short shopID, int itemStackShopSlot) {
        if (!map.containsKey(shopID)) return null;
        if (itemStackShopSlot > map.get(shopID).size() - 1) return null;
        return map.get(shopID).get(itemStackShopSlot).getItemId();
    }

    public List<ShopItemStackInfo> getShopItemList(short shopID) {
        if (!map.containsKey(shopID)) return null;
        return map.get(shopID);
    }

    public ShopItemStackInfo getShopItemStackInfo(short shopID, int itemStackShopSlot) {
        if (!map.containsKey(shopID)) return null;
        if (itemStackShopSlot > map.get(shopID).size() - 1) return null;
        return map.get(shopID).get(itemStackShopSlot);
    }
}
