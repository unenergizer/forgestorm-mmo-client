package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.game.world.item.inventory.ShopItemStackInfo;

import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class EntityShopLoader extends AsynchronousAssetLoader<EntityShopLoader.EntityShopDataWrapper, EntityShopLoader.EntityShopParameter> {

    static class EntityShopParameter extends AssetLoaderParameters<EntityShopDataWrapper> {
    }

    private EntityShopDataWrapper entityShopDataWrapper = null;

    EntityShopLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, EntityShopParameter parameter) {
        entityShopDataWrapper = null;
        entityShopDataWrapper = new EntityShopDataWrapper();
        Yaml yaml = new Yaml();
        Map<Integer, Map<Integer, Map<String, Object>>> root = yaml.load(file.read());

        entityShopDataWrapper.setShopItemListMap(new HashMap<Short, List<ShopItemStackInfo>>());

        short count = 0;
        for (Map<Integer, Map<String, Object>> shopObject : root.values()) {
            List<ShopItemStackInfo> shopItemStackInfos = new ArrayList<ShopItemStackInfo>();
            for (Map<String, Object> shopItemObject : shopObject.values()) {
                ShopItemStackInfo itemStackInfo = new ShopItemStackInfo(
                        (Integer) shopItemObject.get("id"),
                        (Integer) shopItemObject.get("price"));
                shopItemStackInfos.add(itemStackInfo);
            }
            entityShopDataWrapper.getShopItemListMap().put(count++, shopItemStackInfos);
        }
    }

    @Override
    public EntityShopDataWrapper loadSync(AssetManager manager, String fileName, FileHandle file, EntityShopParameter parameter) {
        return entityShopDataWrapper;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, EntityShopParameter parameter) {
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public class EntityShopDataWrapper {
        private Map<Short, List<ShopItemStackInfo>> shopItemListMap = null;
    }
}


