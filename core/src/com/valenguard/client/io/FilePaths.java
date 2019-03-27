package com.valenguard.client.io;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FilePaths {

    // Root
    NETWORK_SETTINGS("Network.yaml"),

    // Item
    ENTITY_SHOP("item/ShopItems.yaml"),
    ITEM_STACK("item/ItemStacks.yaml"),

    // Maps
    MAPS("maps");

    private String filePath;

    public String getFilePath() {
        return "data/" + filePath;
    }
}
