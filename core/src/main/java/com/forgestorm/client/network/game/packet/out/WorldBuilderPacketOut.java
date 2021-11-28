package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

import static com.forgestorm.client.util.Log.println;


public class WorldBuilderPacketOut extends AbstractPacketOut {

    private static final boolean PRINT_DEBUG = false;

    private final LayerDefinition layerDefinition;
    private final int textureId;
    private final int tileX, tileY;
    private final short worldZ;

    public WorldBuilderPacketOut(LayerDefinition layerDefinition, int textureId, int tileX, int tileY, short worldZ) {
        super(Opcodes.WORLD_BUILDER);
        this.layerDefinition = layerDefinition;
        this.textureId = textureId;
        this.tileX = tileX;
        this.tileY = tileY;
        this.worldZ = worldZ;

        println(getClass(), "LayerDefinition: " + layerDefinition, false, PRINT_DEBUG);
        println(getClass(), "textureId: " + textureId, false, PRINT_DEBUG);
        println(getClass(), "tileX: " + tileX, false, PRINT_DEBUG);
        println(getClass(), "tileY: " + tileY, false, PRINT_DEBUG);
        println(getClass(), "worldZ: " + worldZ, false, PRINT_DEBUG);
    }

    @Override
    public void createPacket(GameOutputStream write) {
        write.writeByte(layerDefinition.getLayerDefinitionByte());
        write.writeInt(textureId);
        write.writeInt(tileX);
        write.writeInt(tileY);
        write.writeShort(worldZ);
    }
}
