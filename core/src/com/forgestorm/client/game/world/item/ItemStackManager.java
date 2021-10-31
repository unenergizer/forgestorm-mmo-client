package com.forgestorm.client.game.world.item;

import com.forgestorm.client.ClientMain;
import com.forgestorm.shared.game.world.item.ItemStack;

import java.util.List;

public class ItemStackManager {

    /**
     * A list of all {@link com.forgestorm.shared.game.world.item.ItemStack} found in the game. This list is loaded into
     * memory for quick access. {@link com.forgestorm.shared.game.world.item.ItemStack} in this list are never directly modified.
     * To use an {@link com.forgestorm.shared.game.world.item.ItemStack} from here, it must be cloned.
     */
    private final com.forgestorm.shared.game.world.item.ItemStack[] itemStacks;

    public ItemStackManager() {
        // Load all items from file and store in memory for quick reference.
        List<com.forgestorm.shared.game.world.item.ItemStack> loadedItemStacks = ClientMain.getInstance().getFileManager().getItemStackData().getItemStackList();
        itemStacks = new com.forgestorm.shared.game.world.item.ItemStack[loadedItemStacks.size()];
        loadedItemStacks.toArray(itemStacks);
    }

    /**
     * Creates a clone of the specified {@link com.forgestorm.shared.game.world.item.ItemStack} from the master list in memory.
     *
     * @param id     The ID of the {@link com.forgestorm.shared.game.world.item.ItemStack} found in the file.
     * @param amount The size of the {@link com.forgestorm.shared.game.world.item.ItemStack} to create.
     * @return A {@link com.forgestorm.shared.game.world.item.ItemStack} ready to be placed in an inventory or the world.
     */
    public com.forgestorm.shared.game.world.item.ItemStack makeItemStack(int id, int amount) {
        com.forgestorm.shared.game.world.item.ItemStack itemStack = (ItemStack) itemStacks[id].clone();
        itemStack.setAmount(amount);
        return itemStack;
    }

    public int getItemStackArraySize() {
        return itemStacks.length;
    }
}
