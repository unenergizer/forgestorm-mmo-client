package com.forgestorm.client.game.world.item.inventory;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.ItemStackSlot;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.shared.game.world.entities.AppearanceType;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.item.WearableItemStack;
import com.forgestorm.shared.game.world.item.inventory.InventoryType;

import static com.forgestorm.client.util.Log.println;

// TODO: dropping items is not handled. The dropping of items would have to behave like a
// TODO: movement or it would have to wait on a response from the server.
public class MoveInventoryEvents {

    private static final boolean PRINT_DEBUG = false;

    public void changeEquipment(ItemStackSlot itemStackTargetSlot, ItemStackSlot sourceItemStackSlot) {
        println(getClass(), "changeEquipment()", true, PRINT_DEBUG);
        if (itemStackTargetSlot.getInventoryType() == InventoryType.EQUIPMENT) {
            equipItem(itemStackTargetSlot, sourceItemStackSlot.getItemStack());
        } else if (sourceItemStackSlot.getInventoryType() == InventoryType.EQUIPMENT) {
            println(getClass(), "From equipment to other inventory", true, PRINT_DEBUG);
            if (itemStackTargetSlot.getItemStack() != null) { // Swapping
                equipItem(sourceItemStackSlot, itemStackTargetSlot.getItemStack());
            } else { // Removing equipment
                println(getClass(), "Removing equipment", true, PRINT_DEBUG);

                switch (sourceItemStackSlot.getAcceptedItemStackTypes()[0]) {
                    case HELM:
                        EntityManager.getInstance().getPlayerClient().removeBodyPart(AppearanceType.HELM_TEXTURE);
                        break;
                    case CHEST:
                        EntityManager.getInstance().getPlayerClient().removeBodyPart(AppearanceType.CHEST_TEXTURE);
                        break;
                    case PANTS:
                        EntityManager.getInstance().getPlayerClient().removeBodyPart(AppearanceType.PANTS_TEXTURE);
                        break;
                    case SHOES:
                        EntityManager.getInstance().getPlayerClient().removeBodyPart(AppearanceType.SHOES_TEXTURE);
                        break;
                    case BOW:
                    case SWORD:
                        EntityManager.getInstance().getPlayerClient().removeBodyPart(AppearanceType.LEFT_HAND);
                        break;
                    case SHIELD:
                        EntityManager.getInstance().getPlayerClient().removeBodyPart(AppearanceType.RIGHT_HAND);
                        break;
                    case GLOVES:
                        EntityManager.getInstance().getPlayerClient().removeBodyPart(AppearanceType.GLOVES_COLOR);
                        break;
                }
            }
        }
        ClientMain.getInstance().getStageHandler().getEquipmentWindow().rebuildPreviewTable();
    }

    private void equipItem(ItemStackSlot itemStackSlot, ItemStack equipItem) {
        switch (itemStackSlot.getAcceptedItemStackTypes()[0]) {
            case HELM:
                EntityManager.getInstance().getPlayerClient().setBodyPart(AppearanceType.HELM_TEXTURE, ((WearableItemStack) equipItem).getTextureId());
                break;
            case CHEST:
                EntityManager.getInstance().getPlayerClient().setBodyPart(AppearanceType.CHEST_TEXTURE, ((WearableItemStack) equipItem).getTextureId());
                break;
            case PANTS:
                EntityManager.getInstance().getPlayerClient().setBodyPart(AppearanceType.PANTS_TEXTURE, ((WearableItemStack) equipItem).getTextureId());
                break;
            case SHOES:
                EntityManager.getInstance().getPlayerClient().setBodyPart(AppearanceType.SHOES_TEXTURE, ((WearableItemStack) equipItem).getTextureId());
                break;
            case BOW:
            case SWORD:
                EntityManager.getInstance().getPlayerClient().setBodyPart(AppearanceType.LEFT_HAND, ((WearableItemStack) equipItem).getTextureId());
                break;
            case SHIELD:
                EntityManager.getInstance().getPlayerClient().setBodyPart(AppearanceType.RIGHT_HAND, ((WearableItemStack) equipItem).getTextureId());
                break;
            case GLOVES:
                EntityManager.getInstance().getPlayerClient().setBodyPart(AppearanceType.GLOVES_COLOR, ((WearableItemStack) equipItem).getColor());
                break;
        }
    }
}