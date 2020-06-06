package com.valenguard.client.game.screens.ui.actors.game.paging;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.rpg.EntityShopManager;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackManager;
import com.valenguard.client.game.world.item.inventory.ShopItemStackInfo;

import java.util.ArrayList;
import java.util.List;

public class SkillBookWindow extends PagedWindow {

    public SkillBookWindow() {
        super("Skill Book", 2, 6);
    }

    public void openWindow() {
        loadPagedWindow();
    }

    @Override
    void windowClosedAction() {
    }

    @Override
    List<PagedWindowSlot> loadPagedWindowSlots() {
        // TODO REMOVE ALL OF THIS AND REDO USING SPELL BOOK SPECIFIC CODE!
        short shopID = 0;
        EntityShopManager entityShopManager = Valenguard.getInstance().getEntityShopManager();
        ItemStackManager itemStackManager = Valenguard.getInstance().getItemStackManager();

        // Generate shop slots
        List<PagedWindowSlot> windowSlots = new ArrayList<PagedWindowSlot>();
        for (int i = 0; i < entityShopManager.getShopItemList(shopID).size(); i++) {
            ShopItemStackInfo shopItemStackInfo = entityShopManager.getShopItemStackInfo(shopID, i);
            ItemStack itemStack = itemStackManager.makeItemStack(entityShopManager.getItemIdForShop(shopID, i), 1);
            windowSlots.add(new SpellBookSlot(stageHandler, itemStack, shopItemStackInfo.getPrice(), (short) i));
        }

        return windowSlots;
    }
}