package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.entities.Direction;
import com.valenguard.client.entities.Entity;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketListener;

public class EntityMoveUpdate implements PacketListener {

    @Opcode(getOpcode = Opcodes.ENTITY_MOVE_UPDATE)
    public void onEntityMoveUpdate(ClientHandler clientHandler) {
        short entityId = clientHandler.readShort();
        Entity entity = EntityManager.getInstance().getEntity(entityId);
        Direction direction = Direction.getDirection(clientHandler.readByte());

        Valenguard.getInstance().getMovementManager().addEntityToMove(entity, direction);
    }
}
