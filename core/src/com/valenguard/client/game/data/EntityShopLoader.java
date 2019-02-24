package com.valenguard.client.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.List;
import java.util.Map;

class EntityShopLoader {

    @SuppressWarnings("unchecked")
    static Map<Integer, List<Integer>> loadFromFile() {
        FileHandle fileHandle = Gdx.files.internal("data" + File.separator + "item" + File.separator + "ShopItems.yaml");
        Yaml yaml = new Yaml();
        Iterable<Object> iterable = yaml.loadAll(fileHandle.read());

        Map<Integer, List<Integer>> map = null;

        for (Object object : iterable) map = (Map<Integer, List<Integer>>) object;
        return map;
    }
}


