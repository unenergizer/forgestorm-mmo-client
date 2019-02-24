package com.valenguard.client.game.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityShopManager {

    private Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();

    public EntityShopManager() {
        init();
    }

    private void init() {
        map = EntityShopLoader.loadFromFile();
    }

    public Integer getItemForShop(int shopID, int itemStackShopSlot) {
        if (!map.containsKey(shopID)) return null;
        if (itemStackShopSlot > map.get(shopID).size() - 1) return null;
        return map.get(shopID).get(itemStackShopSlot);
    }
}
