package com.valenguard.client.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.valenguard.client.game.assets.GameAtlas;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.valenguard.client.util.Log.printEmptyLine;
import static com.valenguard.client.util.Log.println;

class ItemLoader {

    private static final boolean PRINT_DEBUG = false;

    List<ItemStack> loadItems() {
        FileHandle fileHandle = Gdx.files.internal("data" + File.separator + "item" + File.separator + "Items.yaml");
        Yaml yaml = new Yaml();
        Map<String, Map<String, Object>> root = yaml.load(fileHandle.read());

        List<ItemStack> itemStacks = new ArrayList<ItemStack>();

        for (Map.Entry<String, Map<String, Object>> entry : root.entrySet()) {
            int itemId = Integer.parseInt(entry.getKey());
            Map<String, Object> itemNode = entry.getValue();

            ItemStack itemStack = new ItemStack(itemId);

            String name = (String) itemNode.get("name");
            String desc = (String) itemNode.get("desc");
            ItemStackType type = ItemStackType.valueOf((String) itemNode.get("type"));
            GameAtlas atlas = GameAtlas.valueOf((String) itemNode.get("atlas"));
            String region = (String) itemNode.get("region");

            Integer wearable = (Integer) itemNode.get("wearable");
            if (wearable != null) {
                itemStack = new WearableItemStack(itemId);
                ((WearableItemStack) itemStack).setTextureId(wearable.shortValue());
            }

            itemStack.setName(name);
            itemStack.setDescription(desc);
            itemStack.setItemStackType(type);
            itemStack.setGameAtlas(atlas);
            itemStack.setTextureRegion(region);
            itemStack.setAmount(-1);

            println(getClass(), "ID: " + itemId, false, PRINT_DEBUG);
            println(getClass(), "Name: " + name, false, PRINT_DEBUG);
            println(getClass(), "Description: " + desc, false, PRINT_DEBUG);
            println(getClass(), "ItemStackType: " + type, false, PRINT_DEBUG);
            println(getClass(), "Atlas: " + atlas, false, PRINT_DEBUG);
            println(getClass(), "Region: " + region, false, PRINT_DEBUG);
            printEmptyLine(PRINT_DEBUG);

            itemStacks.add(itemStack);
        }

        return itemStacks;
    }
}
