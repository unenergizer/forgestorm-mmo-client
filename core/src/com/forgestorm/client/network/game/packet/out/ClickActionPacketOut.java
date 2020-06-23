package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.input.ClickAction;
import com.forgestorm.client.network.game.shared.Opcodes;

public class ClickActionPacketOut extends AbstractClientPacketOut {

    private final ClickAction clickAction;

    public ClickActionPacketOut(ClickAction clickAction) {
        super(Opcodes.CLICK_ACTION);
        this.clickAction = clickAction;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        write.writeByte(clickAction.getClickAction());
        write.writeByte(clickAction.getClickedEntity().getEntityType().getEntityTypeByte());
        write.writeShort(clickAction.getClickedEntity().getServerEntityID());
    }
}
