package com.valenguard.client.network.packet.in;

import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketListener;

public class EntityDespawn implements PacketListener {

    @Opcode(getOpcode = Opcodes.ENTITY_DESPAWN)
    public void onEntityExitMap(ClientHandler clientHandler) {
        EntityManager.getInstance().removeEntity(clientHandler.readShort());
    }
}
