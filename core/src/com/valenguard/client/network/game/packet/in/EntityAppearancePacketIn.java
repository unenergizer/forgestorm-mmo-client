package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.game.world.entities.Appearance;
import com.valenguard.client.game.world.entities.Entity;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.EntityType;
import com.valenguard.client.game.world.entities.MovingEntity;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;
import com.valenguard.client.util.ColorList;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.APPEARANCE)
public class EntityAppearancePacketIn implements PacketListener<EntityAppearancePacketIn.EntityAppearancePacket> {

    private static final boolean PRINT_DEBUG = true;

    // Cannot exceed -0x80 (Sign bit). Extend to short if
    // the number of indexes goes beyond 8.
    private static final byte COLOR_INDEX = 0x01;
    private static final byte BODY_INDEX = 0x02;
    private static final byte HEAD_INDEX = 0x04;
    private static final byte HELM_INDEX = 0x08;
    private static final byte CHEST_INDEX = 0x10;
    private static final byte PANTS_INDEX = 0x20;
    private static final byte SHOES_INDEX = 0x40;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();
        final byte appearanceBits = clientHandler.readByte();
        final short[] textureIds = new short[6];
        byte colorId = -1;

        println(getClass(), "Appearance packet in!");

        if ((appearanceBits & COLOR_INDEX) != 0) {
            colorId = clientHandler.readByte();
        }
        if ((appearanceBits & BODY_INDEX) != 0) {
            textureIds[Appearance.BODY] = clientHandler.readShort();
        }
        if ((appearanceBits & HEAD_INDEX) != 0) {
            textureIds[Appearance.HEAD] = clientHandler.readShort();
        }
        if ((appearanceBits & HELM_INDEX) != 0) {
            textureIds[Appearance.HELM] = clientHandler.readShort();
        }
        if ((appearanceBits & CHEST_INDEX) != 0) {
            textureIds[Appearance.CHEST] = clientHandler.readShort();
        }
        if ((appearanceBits & PANTS_INDEX) != 0) {
            textureIds[Appearance.PANTS] = clientHandler.readShort();
        }
        if ((appearanceBits & SHOES_INDEX) != 0) {
            textureIds[Appearance.SHOES] = clientHandler.readShort();
        }

        return new EntityAppearancePacket(
                entityId,
                EntityType.getEntityType(entityType),
                appearanceBits,
                colorId,
                textureIds
        );
    }

    @Override
    public void onEvent(EntityAppearancePacket packetData) {
        Entity entity = null;

        switch (packetData.entityType) {
            case CLIENT_PLAYER:
                // NOTE: We do the appearance change locally on the client!
                break;
            case PLAYER:
                entity = EntityManager.getInstance().getPlayerEntity(packetData.entityId);
                break;
            case ITEM_STACK:
                break;
            case NPC:
            case MONSTER:
                entity = EntityManager.getInstance().getAiEntity(packetData.entityId);
                break;
            case SKILL_NODE:
                entity = EntityManager.getInstance().getStationaryEntity(packetData.entityId);
                break;
        }

        Appearance appearance = entity.getAppearance();
        boolean updatedTextureId = false;

        if ((packetData.appearanceBits & COLOR_INDEX) != 0) {
            appearance.setColor(ColorList.getType(packetData.colorId).getColor());
        }
        if ((packetData.appearanceBits & BODY_INDEX) != 0) {
            appearance.getTextureIds()[Appearance.BODY] = packetData.textureIds[Appearance.BODY];
            updatedTextureId = true;
        }
        if ((packetData.appearanceBits & HEAD_INDEX) != 0) {
            appearance.getTextureIds()[Appearance.HEAD] = packetData.textureIds[Appearance.HEAD];
            updatedTextureId = true;
        }
        if ((packetData.appearanceBits & HELM_INDEX) != 0) {

            println(getClass(), "UPDATING THE HELM", false, PRINT_DEBUG);

            appearance.getTextureIds()[Appearance.HELM] = packetData.textureIds[Appearance.HELM];
            updatedTextureId = true;
        }
        if ((packetData.appearanceBits & CHEST_INDEX) != 0) {

            println(getClass(), "UPDATING THE CHEST!", false, PRINT_DEBUG);

            appearance.getTextureIds()[Appearance.CHEST] = packetData.textureIds[Appearance.CHEST];
            updatedTextureId = true;
        }
        if ((packetData.appearanceBits & PANTS_INDEX) != 0) {

            println(getClass(), "UPDATING THE PANTS!", false, PRINT_DEBUG);

            appearance.getTextureIds()[Appearance.PANTS] = packetData.textureIds[Appearance.PANTS];
            updatedTextureId = true;
        }
        if ((packetData.appearanceBits & SHOES_INDEX) != 0) {

            println(getClass(), "UPDATING THE SHOES!", false, PRINT_DEBUG);

            appearance.getTextureIds()[Appearance.SHOES] = packetData.textureIds[Appearance.SHOES];
            updatedTextureId = true;
        }

        if (updatedTextureId) {
            if (entity instanceof MovingEntity) {
                MovingEntity movingEntity = (MovingEntity) entity;
                println(getClass(), "ENTITY : " + entity.getClass().getSimpleName(), false, PRINT_DEBUG);
                movingEntity.loadTextures(GameAtlas.ENTITY_CHARACTER);
            }
        }
    }

    @AllArgsConstructor
    class EntityAppearancePacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
        private final byte appearanceBits;
        private final byte colorId;
        private final short[] textureIds;
    }
}
