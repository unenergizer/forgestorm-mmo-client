package com.valenguard.client.movement;

import com.badlogic.gdx.math.Interpolation;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.entities.Entity;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.maps.data.Location;

public class EntityMovementManager {

    public void tick(float delta) {
        for (Entity entity : EntityManager.getInstance().getEntities().values()) {
            if (!MoveUtil.isEntityMoving(entity)) continue;
            // Do not allow the PlayerClientEntity to be interpolated here and in movement manager.
            if (entity instanceof PlayerClient) continue;
            updateEntitiesPosition(entity, delta);
        }
    }

    public void updateEntityFutureLocation(Entity entity, Location futureLocation) {
        entity.setWalkTime(0f);
        entity.setFutureMapLocation(futureLocation);
        entity.setFacingDirection(MoveUtil.getMoveDirection(entity.getCurrentMapLocation(), futureLocation));
    }

    private void updateEntitiesPosition(Entity entity, float delta) {
        entity.setWalkTime(entity.getWalkTime() + delta);

        int currentX = entity.getCurrentMapLocation().getX();
        int currentY = entity.getCurrentMapLocation().getY();

        int futureX = entity.getFutureMapLocation().getX();
        int futureY = entity.getFutureMapLocation().getY();

        entity.setDrawX(Interpolation.linear.apply(currentX, futureX, entity.getWalkTime() / entity.getMoveSpeed()) * ClientConstants.TILE_SIZE);
        entity.setDrawY(Interpolation.linear.apply(currentY, futureY, entity.getWalkTime() / entity.getMoveSpeed()) * ClientConstants.TILE_SIZE);

        if (entity.getWalkTime() <= entity.getMoveSpeed()) return;

        if (entity.getFutureLocationRequests().isEmpty()) {

            // If the server is running really slow it's possible we may need to wait
            // on another packet for movement. That or the entity just isn't requesting to
            // move again.
            finishMove(entity);
        } else {
            continueMove(entity);
        }
    }

    private void finishMove(Entity entity) {
        entity.getCurrentMapLocation().set(entity.getFutureMapLocation());
        entity.setDrawY(entity.getFutureMapLocation().getX() * ClientConstants.TILE_SIZE);
        entity.setDrawY(entity.getFutureMapLocation().getY() * ClientConstants.TILE_SIZE);
    }

    private void continueMove(Entity entity) {
        entity.getCurrentMapLocation().set(entity.getFutureMapLocation());
        entity.setFutureMapLocation(entity.getFutureLocationRequests().remove());
        entity.setFacingDirection(MoveUtil.getMoveDirection(entity.getCurrentMapLocation(), entity.getFutureMapLocation()));
        entity.setWalkTime(0f);
    }

}
