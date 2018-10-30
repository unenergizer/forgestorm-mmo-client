package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.entities.Entity;
import com.valenguard.client.entities.EntityManager;
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
        int packetId = clientHandler.readInt();

        Entity entity = EntityManager.getInstance().getEntity(entityId);

        long deltaPacketTime = System.currentTimeMillis() - Valenguard.getInstance().getPing();

        float delay = (deltaPacketTime / 1000f);

        System.out.println(packetId + " -> [" + futureX + ", " + futureY + "] , [" + deltaPacketTime + "," + delay + "]");

        //
//        System.out.println("MOVE DIRECTION: " + moveDirection);
//        System.out.println("X: " + futureX + ", Y:" + futureY);

        int currentX = entity.getCurrentMapLocation().getX();
        int currentY = entity.getCurrentMapLocation().getY();

        int difX = Math.abs(currentX - futureX);
        int difY = Math.abs(currentY - futureY);

        int totalDifference = difX + difY;

        if (totalDifference == 0) {
            System.out.println("For some reason we got a difference of 0");
        }

        if (totalDifference > 1) {
            System.err.println("Out of sync by a difference of: " + totalDifference);
        }

        if (MoveUtil.isEntityMoving(entity)) {

            // Client and server are off...
            if (entity.getFutureLocationRequest() != null) {
                System.out.println("Server/Client are no longer in sync");
            }

            entity.setFutureLocationRequest(new Location(entity.getMapName(), futureX, futureY));
        } else {
            entity.setWalkTime(delay);
            Valenguard.getInstance().getEntityMovementManager().updateEntityFutureLocation(entity, new Location(entity.getMapName(), futureX, futureY));
        }
    }
}
