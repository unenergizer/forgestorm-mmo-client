package com.forgestorm.shared.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.ClientMain;
import com.forgestorm.shared.game.rpg.Attributes;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.item.ItemStackType;
import com.forgestorm.shared.game.world.item.WearableItemStack;
import com.forgestorm.shared.io.type.GameAtlas;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.forgestorm.client.util.Log.println;

/**
 * Load all items from file and store in memory for quick reference.
 */
public class ItemStackLoader extends AsynchronousAssetLoader<ItemStackLoader.ItemStackData, ItemStackLoader.ItemStackParameter> {

    static class ItemStackParameter extends AssetLoaderParameters<ItemStackData> {
    }

    private static final boolean PRINT_DEBUG = false;
    private final ClientMain clientMain;
    private ItemStackData itemStackData = null;

    public ItemStackLoader(ClientMain clientMain, FileHandleResolver resolver) {
        super(resolver);
        this.clientMain = clientMain;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, ItemStackParameter parameter) {
        itemStackData = null;
        itemStackData = new ItemStackData();
        Yaml yaml = new Yaml();
        Map<Integer, Map<String, Object>> root = yaml.load(file.read());

        itemStackData.setItemStackList(new ArrayList<ItemStack>());

        for (Map.Entry<Integer, Map<String, Object>> entry : root.entrySet()) {
            int itemId = entry.getKey();
            Map<String, Object> itemNode = entry.getValue();

            ItemStack itemStack = new ItemStack(itemId);

            /*
             * Get universal item information
             */
            String name = (String) itemNode.get("name");
            String desc = (String) itemNode.get("desc");
            ItemStackType type = ItemStackType.valueOf((String) itemNode.get("type"));
            GameAtlas atlas = GameAtlas.valueOf((String) itemNode.get("atlas"));
            String textureRegionName = (String) itemNode.get("region");
            Integer stackable = (Integer) itemNode.get("stackable");
            boolean isConsumable = (Boolean) itemNode.get("consume");

            /*
             * Get wearable item data
             */
            Integer wearable = (Integer) itemNode.get("wearable");
            if (wearable != null) {
                itemStack = new WearableItemStack(itemId);
                ((WearableItemStack) itemStack).setTextureId(wearable.shortValue());
            }

            Integer color = (Integer) itemNode.get("color");
            if (type == ItemStackType.GLOVES) {
                itemStack = new WearableItemStack(itemId);
                ((WearableItemStack) itemStack).setColor(color);
            }

            /*
             * Get Skill Information
             */
            Integer skillID = (Integer) itemNode.get("skillID");
            if (skillID != null) {
                itemStack.setSkillID(skillID);
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

            TextureRegion textureRegion = clientMain.getFileManager().getAtlas(GameAtlas.ITEMS).findRegion(textureRegionName);
            itemStack.setTextureRegion(textureRegion);
            itemStack.setTextureRegionName(textureRegionName);

            if (stackable != null) {
                itemStack.setStackable(stackable);
            } else {
                itemStack.setStackable(1);
            }

            itemStack.setAmount(-1);
            itemStack.setAttributes(attributes);
            itemStack.setConsumable(isConsumable);

            println(getClass(), "ID: " + itemId, false, PRINT_DEBUG);
            println(getClass(), "Name: " + name, false, PRINT_DEBUG);
            println(getClass(), "Description: " + desc, false, PRINT_DEBUG);
            println(getClass(), "ItemStackType: " + type, false, PRINT_DEBUG);
            println(getClass(), "Atlas: " + atlas, false, PRINT_DEBUG);
            println(getClass(), "Region: " + textureRegionName, false, PRINT_DEBUG);
            println(getClass(), "Damage: " + attributes.getDamage(), false, PRINT_DEBUG && attributes.getDamage() != 0);
            println(getClass(), "Armor: " + attributes.getArmor(), false, PRINT_DEBUG && attributes.getArmor() != 0);
            if (itemStack instanceof WearableItemStack) {
                println(getClass(), "TextureId: " + ((WearableItemStack) itemStack).getTextureId(), false, PRINT_DEBUG);
                println(getClass(), "Color: " + ((WearableItemStack) itemStack).getColor(), false, PRINT_DEBUG);
            }
            println(getClass(), "SkillId: " + skillID, false, PRINT_DEBUG && skillID != null);
            println(PRINT_DEBUG);

            itemStackData.getItemStackList().add(itemStack);
        }

        println(getClass(), "====== END LOADING ITEMS ======", false, PRINT_DEBUG);
    }

    @Override
    public ItemStackData loadSync(AssetManager manager, String fileName, FileHandle file, ItemStackParameter parameter) {
        return itemStackData;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, ItemStackParameter parameter) {
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public static class ItemStackData {
        private List<ItemStack> itemStackList = null;
    }
}
