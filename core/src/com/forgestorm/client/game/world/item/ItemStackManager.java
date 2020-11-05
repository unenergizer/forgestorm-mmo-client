package com.forgestorm.client.game.world.item;

import com.forgestorm.client.ClientMain;

import java.util.List;

public class ItemStackManager {

    /**
     * A list of all {@link ItemStack} found in the game. This list is loaded into
     * memory for quick access. {@link ItemStack} in this list are never directly modified.
     * To use an {@link ItemStack} from here, it must be cloned.
     */
    private ItemStack[] itemStacks;

    public ItemStackManager() {
        // Load all items from file and store in memory for quick reference.
        List<ItemStack> loadedItemStacks = ClientMain.getInstance().getFileManager().getItemStackData().getItemStackList();
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

    public int getItemStackArraySize() {
        return itemStacks.length;
    }
}
