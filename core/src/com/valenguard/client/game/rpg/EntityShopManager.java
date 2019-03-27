package com.valenguard.client.game.rpg;

import com.valenguard.client.game.world.item.inventory.ShopItemStackInfo;
import com.valenguard.client.io.EntityShopLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityShopManager {

    private Map<Short, List<ShopItemStackInfo>> map = new HashMap<Short, List<ShopItemStackInfo>>();

    public EntityShopManager() {
        init();
    }

    private void init() {
        map = EntityShopLoader.loadFromFile();
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
