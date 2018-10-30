package com.valenguard.client.network.packet.in;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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

        float delay = (Valenguard.getInstance().getPing() / 1000f);

        //
//        System.out.println("MOVE DIRECTION: " + moveDirection);
//        System.out.println("X: " + futureX + ", Y:" + futureY);

        int currentX = entity.getCurrentMapLocation().getX();
        int currentY = entity.getCurrentMapLocation().getY();

        System.out.println();
        System.out.println(packetId + " Current -> [" + currentX + ", " + currentY + "]");
        System.out.println(packetId + " Future -> [" + futureX + ", " + futureY + "]");

        int difX = Math.abs(currentX - futureX);
        int difY = Math.abs(currentY - futureY);

        int totalDifference = difX + difY;

        if (totalDifference == 0) {
            System.err.println("For some reason we got a difference of 0");
        }

        if (MoveUtil.isEntityMoving(entity)) {

            // Client and server are off...
            if (entity.getFutureLocationRequest() != null) {
                System.err.println("The future location was not null but we are already moving.");
            }

            System.out.println("Server Tells client where to move but the client has not finished their last movement.");

            entity.setFutureLocationRequest(new Location(entity.getMapName(), futureX, futureY));
        } else {

            System.out.println("Starting a new move");

            if (totalDifference > 1) {
                System.err.println("Started a new move but the tile is more than 1 away from us: " + totalDifference);
            }


            // entity.setWalkTime(delay);
            Valenguard.getInstance().getEntityMovementManager().updateEntityFutureLocation(entity, new Location(entity.getMapName(), futureX, futureY));
        }
    }
}
