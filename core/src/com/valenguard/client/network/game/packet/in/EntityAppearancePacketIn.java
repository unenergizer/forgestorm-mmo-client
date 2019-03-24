package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.Appearance;
import com.valenguard.client.game.entities.Entity;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.EntityType;
import com.valenguard.client.game.entities.MovingEntity;
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

    private static final boolean PRINT_DEBUG = false;

    private static final int COLOR_INDEX = 0x01;
    private static final int BODY_INDEX = 0x02;
    private static final int HEAD_INDEX = 0x04;
    private static final int ARMOR_INDEX = 0x08;
    private static final int HELM_INDEX = 0x10;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();
        final byte appearanceBits = clientHandler.readByte();
        final short[] textureIds = new short[4];
        byte colorId = -1;

        if ((appearanceBits & COLOR_INDEX) != 0) {
            colorId = clientHandler.readByte();
        }
        if ((appearanceBits & BODY_INDEX) != 0) {
            textureIds[Appearance.BODY] = clientHandler.readShort();
        }
        if ((appearanceBits & HEAD_INDEX) != 0) {
            textureIds[Appearance.HEAD] = clientHandler.readShort();
        }
        if ((appearanceBits & ARMOR_INDEX) != 0) {
            textureIds[Appearance.ARMOR] = clientHandler.readShort();
        }
        if ((appearanceBits & HELM_INDEX) != 0) {
            textureIds[Appearance.HELM] = clientHandler.readShort();
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
            appearance.setColor(ColorList.getColorList(packetData.colorId).getColor());
        }
        if ((packetData.appearanceBits & BODY_INDEX) != 0) {
            appearance.getTextureIds()[Appearance.BODY] = packetData.textureIds[Appearance.BODY];
            updatedTextureId = true;
        }
        if ((packetData.appearanceBits & HEAD_INDEX) != 0) {
            appearance.getTextureIds()[Appearance.HEAD] = packetData.textureIds[Appearance.HEAD];
            updatedTextureId = true;
        }
        if ((packetData.appearanceBits & ARMOR_INDEX) != 0) {

            println(getClass(), "UPDATING THE ARMOR!", false, PRINT_DEBUG);

            appearance.getTextureIds()[Appearance.ARMOR] = packetData.textureIds[Appearance.ARMOR];
            updatedTextureId = true;
        }
        if ((packetData.appearanceBits & HELM_INDEX) != 0) {

            println(getClass(), "UPDATING THE HELM", false, PRINT_DEBUG);

            appearance.getTextureIds()[Appearance.HELM] = packetData.textureIds[Appearance.HELM];
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
