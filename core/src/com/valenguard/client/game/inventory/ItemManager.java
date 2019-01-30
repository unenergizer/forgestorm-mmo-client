package com.valenguard.client.game.inventory;

import java.util.List;

public class ItemManager {

    /**
     * A list of all {@link ItemStack} found in the game. This list is loaded into
     * memory for quick access. {@link ItemStack} in this list are never directly modified.
     * To use an {@link ItemStack} from here, it must be cloned.
     */
    private ItemStack[] itemStacks;

    public ItemManager() {
        init();
    }

    /**
     * Load all items from file and store in memory for quick reference.
     */
    private void init() {
        ItemLoader itemLoader = new ItemLoader();
        List<ItemStack> loadedItemStacks = itemLoader.loadItems();
        itemStacks = new ItemStack[loadedItemStacks.size()];
        loadedItemStacks.toArray(itemStacks);
    }

    /**
     * Creates a clone of the specified {@link ItemStack} from the master list in memory.
     *
     * @param id     The ID of the {@link ItemStack} found in the file.
     * @param amount The size of the {@link ItemStack} to create.
     * @return A {@link ItemStack} ready to be placed in an inventory or the world.
     */
    public ItemStack makeItemStack(int id, int amount) {
        ItemStack itemStack = (ItemStack) itemStacks[id].clone();
        itemStack.setAmount(amount);
        return itemStack;
    }
}
