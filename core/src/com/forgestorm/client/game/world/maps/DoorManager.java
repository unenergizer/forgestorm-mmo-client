package com.forgestorm.client.game.world.maps;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.DoorProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.network.game.packet.out.DoorInteractPacketOut;

public class DoorManager {

    public void playerClientToggleDoor(int tileX, int tileY) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        GameWorld gameWorld = playerClient.getGameMap();
        Location playerLocation = playerClient.getCurrentMapLocation();

        Tile tile = gameWorld.getTile(LayerDefinition.COLLIDABLES, tileX, tileY);
        if (tile == null) return;

        TileImage tileImage = tile.getTileImage();

        if (tileImage == null) return;
        if (!tileImage.containsProperty(TilePropertyTypes.DOOR)) return;
        if (isTooFarAway(playerLocation, tileX, tileY)) return;

        DoorProperty doorProperty = (DoorProperty) tileImage.getProperty(TilePropertyTypes.DOOR);
        DoorStatus doorStatus = doorProperty.getDoorStatus();

        // Do the opposite of the current door status
        switch (doorStatus) {
            case OPEN:
                // Set the door as closed
                doorStatus = DoorStatus.CLOSED;
                tile.getTileImage().getTileAnimation().playAnimation(TileAnimation.AnimationControls.PLAY_BACKWARDS);
                break;
            case CLOSED:
                // Set the door as open
                doorStatus = DoorStatus.OPEN;
                tile.getTileImage().getTileAnimation().playAnimation(TileAnimation.AnimationControls.PLAY_NORMAL);
                break;
        }

        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(getClass(), (short) 20);
        doorProperty.setDoorStatus(doorStatus);
        new DoorInteractPacketOut(doorStatus, tileX, tileY).sendPacket();
    }

    public void networkToggleDoor(DoorStatus doorStatus, int tileX, int tileY) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        Tile tile = playerClient.getGameMap().getTile(LayerDefinition.COLLIDABLES, tileX, tileY);
        TileImage tileImage = tile.getTileImage();

        if (tileImage == null) return;
        if (!tileImage.containsProperty(TilePropertyTypes.DOOR)) return;

        // Set new door status
        DoorProperty doorProperty = (DoorProperty) tileImage.getProperty(TilePropertyTypes.DOOR);
        doorProperty.setDoorStatus(doorStatus);

        // Play animation
        switch (doorStatus) {
            case OPEN:
                tile.getTileImage().getTileAnimation().playAnimation(TileAnimation.AnimationControls.PLAY_NORMAL);
                break;
            case CLOSED:
                tile.getTileImage().getTileAnimation().playAnimation(TileAnimation.AnimationControls.PLAY_BACKWARDS);
                break;
        }

        // Don't play the sound if the user is too far away from the door
        if (isTooFarAway(playerClient.getCurrentMapLocation(), tileX, tileY)) return;
        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(getClass(), (short) 20);
    }

    private boolean isTooFarAway(Location playerClientLocation, int x1, int y1) {
        int x2 = playerClientLocation.getX();
        int y2 = playerClientLocation.getY();

        double distance = Math.abs(x2 - x1) + Math.abs(y2 - y1);

        return distance > ClientConstants.MAX_INTERACT_DISTANCE;
    }

    public boolean isDoorwayTraversable(Tile tile) {
        TileImage tileImage = tile.getTileImage();
        if (tileImage == null) return true;
        if (!tileImage.containsProperty(TilePropertyTypes.DOOR)) return true;

        DoorProperty doorProperty = (DoorProperty) tileImage.getProperty(TilePropertyTypes.DOOR);
        return doorProperty.getDoorStatus() == DoorStatus.OPEN;
    }

    public enum DoorStatus {
        OPEN,
        CLOSED,
        LOCKED;

        public static DoorStatus getDoorStatus(byte enumIndex) {
            for (DoorStatus doorStatus : DoorStatus.values()) {
                if ((byte) doorStatus.ordinal() == enumIndex) return doorStatus;
            }
            throw new RuntimeException("DoorStatus type miss match! Byte Received: " + enumIndex);
        }

        public static byte getByte(DoorStatus doorStatus) {
            return (byte) doorStatus.ordinal();
        }
    }
}
