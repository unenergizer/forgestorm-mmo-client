package com.valenguard.client.game.screens.ui.actors.game.paging;

import com.valenguard.client.ClientMain;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackManager;
import com.valenguard.client.game.world.item.ItemStackType;

import java.util.ArrayList;
import java.util.List;

public class SkillBookWindow extends PagedWindow {

    public SkillBookWindow() {
        super("Skill Book - NOT FUNCTIONAL", 2, 6);
    }

    public void openWindow() {
        loadPagedWindow();
    }

    @Override
    void windowClosedAction() {
    }

    @Override
    List<PagedWindowSlot> loadPagedWindowSlots() {
        ItemStackManager itemStackManager = ClientMain.getInstance().getItemStackManager();
        int size = itemStackManager.getItemStackArraySize();

        // Generate shop slots
        List<PagedWindowSlot> windowSlots = new ArrayList<PagedWindowSlot>();
        for (int i = 0; i < size; i++) {
            ItemStack itemStack = itemStackManager.makeItemStack(i, 1);
            if (itemStack.getItemStackType() != ItemStackType.BOOK_SKILL) continue;
            windowSlots.add(new SkillBookSlot(stageHandler, itemStack));
        }

        return windowSlots;
    }
}