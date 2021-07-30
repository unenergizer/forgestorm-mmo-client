package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.maps.Tile;
import com.forgestorm.client.game.world.maps.TileAnimation;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.DOOR_INTERACT)
public class DoorInteractPacketIn implements PacketListener<DoorInteractPacketIn.DoorStatusPacket> {

    private final static boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {

        byte doorStatus = clientHandler.readByte();
        int tileX = clientHandler.readInt();
        int tileY = clientHandler.readInt();

        return new DoorStatusPacket(DoorStatus.getDoorStatus(doorStatus), tileX, tileY);
    }

    @Override
    public void onEvent(DoorStatusPacket packetData) {
        println(getClass(), "DoorStatus: " + packetData.doorStatus, false, PRINT_DEBUG);
        println(getClass(), "TileX: " + packetData.tileX, false, PRINT_DEBUG);
        println(getClass(), "TileY: " + packetData.tileY, false, PRINT_DEBUG);

        Tile tile = EntityManager.getInstance().getPlayerClient().getGameMap().getTile(LayerDefinition.COLLIDABLES, packetData.tileX, packetData.tileY);

        if (tile != null && tile.getTileImage().containsProperty(TilePropertyTypes.DOOR)) {
            println(getClass(), "Door tile found!", false, PRINT_DEBUG);
            if (tile.getTileImage().getTileAnimation() != null) {
                tile.getTileImage().getTileAnimation().playAnimation(TileAnimation.AnimationControls.PLAY_BACKWARDS);
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(getClass(), (short) 20);
            }
        }
    }

    @AllArgsConstructor
    class DoorStatusPacket extends PacketData {
        private final DoorStatus doorStatus;
        private final int tileX;
        private final int tileY;
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
