package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.network.game.shared.Opcodes;

import static com.forgestorm.client.util.Log.println;


public class WorldBuilderPacketOut extends AbstractClientPacketOut {

    private static final boolean PRINT_DEBUG = false;

    private final LayerDefinition layerDefinition;
    private final int textureId;
    private final int tileX, tileY;

    public WorldBuilderPacketOut(LayerDefinition layerDefinition, int textureId, int tileX, int tileY) {
        super(Opcodes.WORLD_BUILDER);
        this.layerDefinition = layerDefinition;
        this.textureId = textureId;
        this.tileX = tileX;
        this.tileY = tileY;

        println(getClass(), "LayerDefinition: " + layerDefinition, false, PRINT_DEBUG);
        println(getClass(), "textureId: " + textureId, false, PRINT_DEBUG);
        println(getClass(), "tileX: " + tileX, false, PRINT_DEBUG);
        println(getClass(), "tileY: " + tileY, false, PRINT_DEBUG);
    }

    @Override
    protected void createPacket(ForgeStormOutputStream write) {
        write.writeByte(layerDefinition.getLayerDefinitionByte());
        write.writeInt(textureId);
        write.writeInt(tileX);
        write.writeInt(tileY);
    }
}
