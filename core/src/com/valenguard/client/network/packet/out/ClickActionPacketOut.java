package com.valenguard.client.network.packet.out;

import com.valenguard.client.game.input.ClickAction;
import com.valenguard.client.network.shared.Opcodes;

public class ClickActionPacketOut extends ClientAbstractOutPacket {

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
