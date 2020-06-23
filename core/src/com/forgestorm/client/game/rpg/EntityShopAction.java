package com.forgestorm.client.game.rpg;

import lombok.Getter;

@Getter
public class EntityShopAction {

    private ShopOpcodes shopOpcode;

    private short entityId;
    private short shopSlot;

    public EntityShopAction(ShopOpcodes shopOpcode) {
        this.shopOpcode = shopOpcode;
    }

    public EntityShopAction(ShopOpcodes shopOpcode, short data) {
        this.shopOpcode = shopOpcode;
        if (shopOpcode == ShopOpcodes.START_SHOPPING) {
            entityId = data;
        } else if (shopOpcode == ShopOpcodes.BUY) {
            shopSlot = data;
        }
    }
}
