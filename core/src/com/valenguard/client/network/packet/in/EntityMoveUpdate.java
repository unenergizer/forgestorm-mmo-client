package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.entities.Entity;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.MoveDirection;
import com.valenguard.client.maps.data.Location;
import com.valenguard.client.movement.MoveUtil;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketListener;

public class EntityMoveUpdate implements PacketListener {

    @Opcode(getOpcode = Opcodes.ENTITY_MOVE_UPDATE)
    public void onEntityMoveUpdate(ClientHandler clientHandler) {
        short entityId = clientHandler.readShort();
        int futureX = clientHandler.readInt();
        int futureY = clientHandler.readInt();

        Entity entity = EntityManager.getInstance().getEntity(entityId);
        MoveDirection moveDirection = MoveUtil.getMoveDirection(entity.getCurrentMapLocation().getX(), entity.getCurrentMapLocation().getY(), futureX, futureY);

        System.out.println("ENTITY: " + entity);
        System.out.println("MOVE DIRECTION: " + moveDirection);
        System.out.println("X: " + futureX + ", Y:" + futureY);

        if (MoveUtil.isEntityMoving(entity)) {

            // Client and server are off...
            if (entity.getFutureLocationRequest() != null) {
                System.out.println("Server/Client are no longer in sync");
            }

            entity.setFutureLocationRequest(new Location(entity.getMapName(), futureX, futureY));
        } else {
            Valenguard.getInstance().getEntityMovementManager().updateEntityFutureLocation(entity, new Location(entity.getMapName(), futureX, futureY));
        }
    }
}
