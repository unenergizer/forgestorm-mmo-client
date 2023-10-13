package com.forgestorm.client.game.screens.ui.actors.game.paging;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.item.ItemStackManager;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.item.ItemStackType;

import java.util.ArrayList;
import java.util.List;

public class SkillBookWindow extends PagedWindow {

    public SkillBookWindow(ClientMain clientMain) {
        super(clientMain, "Skill Book - NOT FUNCTIONAL", 2, 6);
    }

    public void openWindow() {
        loadPagedWindow();
    }

    @Override
    void windowClosedAction() {
    }

    @Override
    List<PagedWindowSlot> loadPagedWindowSlots() {
        ItemStackManager itemStackManager = stageHandler.getClientMain().getItemStackManager();
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