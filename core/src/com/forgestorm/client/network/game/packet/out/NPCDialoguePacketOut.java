package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.entities.NPC;
import com.forgestorm.client.network.game.shared.Opcodes;

public class NPCDialoguePacketOut extends AbstractClientPacketOut {

    private short entityId;

    public NPCDialoguePacketOut(NPC npc) {
        super(Opcodes.NPC_DIALOGUE);
        entityId = npc.getServerEntityID();
    }

    @Override
    void createPacket(ForgeStormOutputStream write) {
        write.writeShort(entityId);
    }
}
