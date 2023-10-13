package com.forgestorm.client.game.world.maps;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.tile.Tile;
import com.forgestorm.client.game.world.maps.tile.TileAnimation;
import com.forgestorm.client.game.world.maps.tile.TileImage;
import com.forgestorm.client.game.world.maps.tile.properties.DoorProperty;
import com.forgestorm.client.network.game.packet.out.DoorInteractPacketOut;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;

import static com.forgestorm.client.util.Log.println;

public class DoorManager {
    
    private final ClientMain clientMain;
    private final EntityManager entityManager;
    
    public DoorManager(ClientMain clientMain) {
        this.clientMain = clientMain;
        this.entityManager = clientMain.getEntityManager();
    }

    public void playerClientToggleDoor(int tileX, int tileY) {
        PlayerClient playerClient = entityManager.getPlayerClient();
        GameWorld gameWorld = playerClient.getGameMap();
        Location playerLocation = playerClient.getCurrentMapLocation();

        Tile tile = gameWorld.getTile(LayerDefinition.WORLD_OBJECTS, tileX, tileY, playerLocation.getZ());
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
                tile.getTileImage().getTileAnimation().playAnimation(TileAnimation.PlaybackType.PLAY_BACKWARDS);
                break;
            case CLOSED:
                // Set the door as open
                doorStatus = DoorStatus.OPEN;
                tile.getTileImage().getTileAnimation().playAnimation(TileAnimation.PlaybackType.PLAY_NORMAL);
                break;
        }

        clientMain.getAudioManager().getSoundManager().playSoundFx(getClass(), (short) 20);
        doorProperty.setDoorStatus(doorStatus);
        new DoorInteractPacketOut(clientMain, doorStatus, tileX, tileY, playerLocation.getZ()).sendPacket();
    }

    public void networkToggleDoor(DoorStatus doorStatus, int tileX, int tileY, short worldZ, boolean playAnimation) {
        PlayerClient playerClient = entityManager.getPlayerClient();
        Tile tile = playerClient.getGameMap().getTile(LayerDefinition.WORLD_OBJECTS, tileX, tileY, worldZ);
        TileImage tileImage = tile.getTileImage();

        if (tileImage == null) {
            println(getClass(), "TileImage null for door location. Location:" + tileX + "/" + tileY + tileX + "/" + worldZ, true);
            return;
        }
        if (!tileImage.containsProperty(TilePropertyTypes.DOOR)) {
            println(getClass(), "TileImage does not contain the door property. Location:" + tileX + "/" + tileY + tileX + "/" + worldZ, true);
            return;
        }

        // Set new door status
        DoorProperty doorProperty = (DoorProperty) tileImage.getProperty(TilePropertyTypes.DOOR);
        doorProperty.setDoorStatus(doorStatus);
        println(getClass(), "Door status: " + doorStatus);

        // Play animation
        TileAnimation tileAnimation = tile.getTileImage().getTileAnimation();

        switch (doorStatus) {
            case OPEN:
                if (playAnimation) {
                    tileAnimation.playAnimation(TileAnimation.PlaybackType.PLAY_NORMAL);
                } else {
                    // Set the visible animation frame to the image of a door/gate that is open.
                    tileAnimation.setActiveFrame(tileAnimation.getLastFrameTileID());
                }
                break;
            case CLOSED:
                if (playAnimation) {
                    tileAnimation.playAnimation(TileAnimation.PlaybackType.PLAY_BACKWARDS);
                } else {
                    // Set the visible animation frame to the image of a door/gate that is closed.
                    tileAnimation.setActiveFrame(tileAnimation.getFirstFrameTileID());
                }
                break;
        }

        // Don't play the sound if the user is too far away from the door
        if (!playAnimation || isTooFarAway(playerClient.getCurrentMapLocation(), tileX, tileY)) return;
        clientMain.getAudioManager().getSoundManager().playSoundFx(getClass(), (short) 20, tileX, tileY, worldZ);
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
