package com.forgestorm.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.forgestorm.client.game.world.item.inventory.ShopItemStackInfo;

import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityShopLoader {

    @SuppressWarnings("unchecked")
    public static Map<Short, List<ShopItemStackInfo>> loadFromFile() {
        FileHandle fileHandle = Gdx.files.internal(FilePaths.ENTITY_SHOP.getFilePath());
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


