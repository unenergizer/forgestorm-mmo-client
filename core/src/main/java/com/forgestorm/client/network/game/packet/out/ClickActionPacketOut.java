package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.input.ClickAction;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

public class ClickActionPacketOut extends AbstractPacketOut {

    private final ClickAction clickAction;

    public ClickActionPacketOut(ClickAction clickAction) {
        super(Opcodes.CLICK_ACTION);
        this.clickAction = clickAction;
    }

    @Override
    public void createPacket(GameOutputStream write) {
        write.writeByte(clickAction.getClickAction());
        write.writeByte(clickAction.getClickedEntity().getEntityType().getEntityTypeByte());
        write.writeShort(clickAction.getClickedEntity().getServerEntityID());
    }
}
