package com.forgestorm.client.game.world.item.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ShopItemStackInfo {
    private int itemId;
    private int price;
}
