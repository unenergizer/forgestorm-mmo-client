package com.valenguard.client.network.packet.in;

import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.Appearance;
import com.valenguard.client.game.entities.Entity;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.APPEARANCE)
public class EntityAppearancePacketIn implements PacketListener<EntityAppearancePacketIn.EntityAppearancePacket> {

    private static final int COLOR_INDEX = 0x01;
    private static final int BODY_INDEX = 0x02;
    private static final int HEAD_INDEX = 0x04;
    private static final int ARMOR_INDEX = 0x08;
    private static final int HELM_INDEX = 0x10;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final byte appearanceBits = clientHandler.readByte();
        final short[] textureIds = new short[4];
        byte colorId = -1;

        System.out.println("INCOMING APPEARANCE PACKET");

        if ((appearanceBits & COLOR_INDEX) != 0) {

            System.out.println("COLOR CHANGE");

            colorId = clientHandler.readByte();
        }
        if ((appearanceBits & BODY_INDEX) != 0) {

            System.out.println("BODY CHANGE");

            textureIds[Appearance.BODY] = clientHandler.readShort();
        }
        if ((appearanceBits & HEAD_INDEX) != 0) {

            System.out.println("HEAD CHANGE");

            textureIds[Appearance.HEAD] = clientHandler.readShort();
        }
        if ((appearanceBits & ARMOR_INDEX) != 0) {

            System.out.println("ARMOR CHANGE");

            textureIds[Appearance.ARMOR] = clientHandler.readShort();
        }
        if ((appearanceBits & HELM_INDEX) != 0) {

            System.out.println("HELM CHANGE");

            textureIds[Appearance.HELM] = clientHandler.readShort();
        }

        return new EntityAppearancePacket(
                entityId,
                appearanceBits,
                colorId,
                textureIds
        );
    }

    @Override
    public void onEvent(EntityAppearancePacket packetData) {
        Entity entity = EntityManager.getInstance().getEntity(packetData.entityId);
        Appearance appearance = entity.getAppearance();
        boolean updatedTextureId = false;

        if ((packetData.appearanceBits & COLOR_INDEX) != 0) {
            appearance.setColorId(packetData.colorId);
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

            System.out.println("UPDATING THE ARMOR!");

            appearance.getTextureIds()[Appearance.ARMOR] = packetData.textureIds[Appearance.ARMOR];
            updatedTextureId = true;
        }
        if ((packetData.appearanceBits & HELM_INDEX) != 0) {

            System.out.println("UPDATING THE HELM");

            appearance.getTextureIds()[Appearance.HELM] = packetData.textureIds[Appearance.HELM];
            updatedTextureId = true;
        }

        if (updatedTextureId) {
            MovingEntity movingEntity = (MovingEntity) entity;
            System.out.println("ENTITY : " + entity.getClass().getSimpleName());
            movingEntity.loadTextures(GameAtlas.ENTITY_CHARACTER);
        }
    }

    @AllArgsConstructor
    class EntityAppearancePacket extends PacketData {
        private final short entityId;
        private final byte appearanceBits;
        private final byte colorId;
        private final short[] textureIds;
    }
}
