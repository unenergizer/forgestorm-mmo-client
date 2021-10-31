package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.entities.NPC;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

public class NPCDialoguePacketOut extends AbstractPacketOut {

    private final short entityId;

    public NPCDialoguePacketOut(NPC npc) {
        super(Opcodes.NPC_DIALOGUE);
        entityId = npc.getServerEntityID();
    }

    @Override
    public void createPacket(GameOutputStream write) {
        write.writeShort(entityId);
    }
}
