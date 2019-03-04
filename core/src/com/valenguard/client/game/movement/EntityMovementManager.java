package com.valenguard.client.game.movement;

import com.badlogic.gdx.math.Interpolation;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.Player;
import com.valenguard.client.game.maps.data.Location;

public class EntityMovementManager {

    public void tick(float delta) {
        for (Player player : EntityManager.getInstance().getPlayerEntityList().values()) {
            if (!MoveUtil.isEntityMoving(player)) continue;
            updateEntitiesPosition(player, delta);
        }
        for (MovingEntity entity : EntityManager.getInstance().getMovingEntityList().values()) {
            if (!MoveUtil.isEntityMoving(entity)) continue;
            updateEntitiesPosition(entity, delta);
        }
    }

    public void updateEntityFutureLocation(MovingEntity entity, Location futureLocation) {
        entity.setWalkTime(0f);
        entity.setFutureMapLocation(futureLocation);
        entity.setFacingDirection(MoveUtil.getMoveDirection(entity.getCurrentMapLocation(), futureLocation));
    }

    private void updateEntitiesPosition(MovingEntity entity, float delta) {
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

    private void finishMove(MovingEntity entity) {
        entity.getCurrentMapLocation().set(entity.getFutureMapLocation());
        entity.setDrawY(entity.getFutureMapLocation().getX() * ClientConstants.TILE_SIZE);
        entity.setDrawY(entity.getFutureMapLocation().getY() * ClientConstants.TILE_SIZE);
    }

    private void continueMove(MovingEntity entity) {
        entity.getCurrentMapLocation().set(entity.getFutureMapLocation());
        entity.setFutureMapLocation(entity.getFutureLocationRequests().remove());
        entity.setFacingDirection(MoveUtil.getMoveDirection(entity.getCurrentMapLocation(), entity.getFutureMapLocation()));
        entity.setWalkTime(0f);
    }

}
