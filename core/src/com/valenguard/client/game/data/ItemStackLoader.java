package com.valenguard.client.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.inventory.ItemStackType;
import com.valenguard.client.game.inventory.WearableItemStack;
import com.valenguard.client.game.rpg.Attributes;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.valenguard.client.util.Log.println;

class ItemStackLoader {

    private static final boolean PRINT_DEBUG = false;

    /**
     * Load all items from file and store in memory for quick reference.
     */
    List<ItemStack> loadItems() {

        println(getClass(), "====== START LOADING ITEMS ======", false, PRINT_DEBUG);

        FileHandle fileHandle = Gdx.files.internal("data" + File.separator + "item" + File.separator + "Items.yaml");
        Yaml yaml = new Yaml();
        Map<String, Map<String, Object>> root = yaml.load(fileHandle.read());

        List<ItemStack> itemStacks = new ArrayList<ItemStack>();

        for (Map.Entry<String, Map<String, Object>> entry : root.entrySet()) {
            int itemId = Integer.parseInt(entry.getKey());
            Map<String, Object> itemNode = entry.getValue();

            ItemStack itemStack = new ItemStack(itemId);

            /*
             * Get universal item information
             */
            String name = (String) itemNode.get("name");
            String desc = (String) itemNode.get("desc");
            ItemStackType type = ItemStackType.valueOf((String) itemNode.get("type"));
            GameAtlas atlas = GameAtlas.valueOf((String) itemNode.get("atlas"));
            String region = (String) itemNode.get("region");

            /*
             * Get wearable item data
             */
            Integer wearable = (Integer) itemNode.get("wearable");
            if (wearable != null) {
                itemStack = new WearableItemStack(itemId);
                ((WearableItemStack) itemStack).setTextureId(wearable.shortValue());
            }

            /*
             * Get item stats
             */
            Attributes attributes = new Attributes();
            Integer stat;

            stat = (Integer) itemNode.get("damage");
            if (stat != null) attributes.setDamage(stat);

            stat = (Integer) itemNode.get("armor");
            if (stat != null) attributes.setArmor(stat);

            itemStack.setName(name);
            itemStack.setDescription(desc);
            itemStack.setItemStackType(type);
            itemStack.setGameAtlas(atlas);
            itemStack.setTextureRegion(region);
            itemStack.setAmount(-1);
            itemStack.setAttributes(attributes);

            println(getClass(), "ID: " + itemId, false, PRINT_DEBUG);
            println(getClass(), "Name: " + name, false, PRINT_DEBUG);
            println(getClass(), "Description: " + desc, false, PRINT_DEBUG);
            println(getClass(), "ItemStackType: " + type, false, PRINT_DEBUG);
            println(getClass(), "Atlas: " + atlas, false, PRINT_DEBUG);
            println(getClass(), "Region: " + region, false, PRINT_DEBUG);

            println(getClass(), "Damage: " + attributes.getDamage(), false, PRINT_DEBUG && attributes.getDamage() != 0);
            println(getClass(), "Armor: " + attributes.getArmor(), false, PRINT_DEBUG && attributes.getArmor() != 0);

            println(PRINT_DEBUG);

            itemStacks.add(itemStack);
        }

        println(getClass(), "====== END LOADING ITEMS ======", false, PRINT_DEBUG);
        return itemStacks;
    }
}
