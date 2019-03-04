package com.valenguard.client.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.valenguard.client.game.inventory.ShopItemStackInfo;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EntityShopLoader {

    @SuppressWarnings("unchecked")
    static Map<Short, List<ShopItemStackInfo>> loadFromFile() {
        FileHandle fileHandle = Gdx.files.internal("data" + File.separator + "item" + File.separator + "ShopItems.yaml");
        Yaml yaml = new Yaml();
        Map<Integer, Map<Integer, Map<String, Object>>> root = yaml.load(fileHandle.read());

        Map<Short, List<ShopItemStackInfo>> map = new HashMap<Short, List<ShopItemStackInfo>>();

        short count = 0;
        for (Map<Integer, Map<String, Object>> shopObject : root.values()) {
            List<ShopItemStackInfo> shopItemStackInfos = new ArrayList<ShopItemStackInfo>();
            for (Map<String, Object> shopItemObject : shopObject.values()) {
                ShopItemStackInfo itemStackInfo = new ShopItemStackInfo(
                        (Integer) shopItemObject.get("id"),
                        (Integer) shopItemObject.get("price"));
                shopItemStackInfos.add(itemStackInfo);
            }
            map.put(count++, shopItemStackInfos);
        }

        return map;
    }
}


