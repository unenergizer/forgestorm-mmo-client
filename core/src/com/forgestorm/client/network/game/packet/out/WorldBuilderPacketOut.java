package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.network.game.shared.Opcodes;

public class WorldBuilderPacketOut extends AbstractClientPacketOut {

    private final LayerDefinition layerDefinition;
    private final int textureId;
    private final short tileX, tileY;

    public WorldBuilderPacketOut(LayerDefinition layerDefinition, int textureId, short tileX, short tileY) {
        super(Opcodes.WORLD_BUILDER);
        this.layerDefinition = layerDefinition;
        this.textureId = textureId;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    @Override
    protected void createPacket(ForgeStormOutputStream write) {
        write.writeByte(layerDefinition.getLayerDefinitionByte());
        write.writeInt(textureId);
        write.writeShort(tileX);
        write.writeShort(tileY);
    }
}
