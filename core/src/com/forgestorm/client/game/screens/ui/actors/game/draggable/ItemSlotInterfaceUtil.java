package com.forgestorm.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisLabel;

public class ItemSlotInterfaceUtil {

    private ItemSlotInterfaceUtil() {
    }

    public static void displayItemAmount(ItemStack itemStack, VisLabel amountLabel, Stack stack) {
        if (itemStack.getAmount() <= 1) return;
        int itemStackAmount = itemStack.getAmount();
        String displayText = String.valueOf(itemStackAmount);
        if (itemStackAmount >= 100000 && itemStackAmount < 1000000) {
            displayText = itemStackAmount / 1000 + "K";
        } else if (itemStackAmount >= 1000000) {
            displayText = itemStackAmount / 1000000 + "M";
        }

        amountLabel.setText(displayText);
        amountLabel.setAlignment(Alignment.BOTTOM_RIGHT.getAlignment());
        stack.add(amountLabel);
    }

}
