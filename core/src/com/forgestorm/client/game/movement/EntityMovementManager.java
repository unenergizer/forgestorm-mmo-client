package com.forgestorm.client.game.movement;

import com.badlogic.gdx.math.Interpolation;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.entities.Player;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.Location;

import static com.forgestorm.client.util.Log.println;

public class EntityMovementManager {

    public void tick(float delta) {
        for (Player player : EntityManager.getInstance().getPlayerEntityList().values()) {
            if (!MoveUtil.isEntityMoving(player)) continue;
            updateEntitiesPosition(player, delta);
        }
        for (MovingEntity entity : EntityManager.getInstance().getAiEntityList().values()) {
            if (!MoveUtil.isEntityMoving(entity)) continue;
            updateEntitiesPosition(entity, delta);
        }
    }

    public void updateEntityFutureLocation(MovingEntity entity, Location futureLocation) {
        if (MoveUtil.isEntityMoving(entity)) {
            println(getClass(), "The Entity is already moving!");
        }

        Location currentLocation = entity.getCurrentMapLocation();

        int xDiff = Math.abs(futureLocation.getX() - currentLocation.getX());
        int yDiff = Math.abs(futureLocation.getY() - currentLocation.getY());

        if (xDiff + yDiff > 1) {
            println(getClass(), "The entity is being told to move diagonally!");
        }

        entity.setWalkTime(0f);
        entity.setFutureMapLocation(futureLocation);
        entity.setFacingDirection(MoveUtil.getMoveDirection(entity.getCurrentMapLocation(), futureLocation));

        // Check if entity shop should close
        if (!(entity instanceof PlayerClient)) {
            PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
            StageHandler stageHandler = ClientMain.getInstance().getStageHandler();
            MovingEntity shopOwner = stageHandler.getPagedItemStackWindow().getShopOwnerEntity();
            if (entity != shopOwner) return;
            // The
            if (!shopOwner.getFutureMapLocation().isWithinDistance(playerClient.getCurrentMapLocation(), (short) 5) ||
                    !shopOwner.getCurrentMapLocation().isWithinDistance(playerClient.getCurrentMapLocation(), (short) 5)) {
                stageHandler.getChatWindow().appendChatMessage("[RED]You are too far away from shop owner. Closing shop.");
                stageHandler.getPagedItemStackWindow().closePagedWindow(false);
            }
        }
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
