package com.valenguard.client.network.game.packet.in;

import com.badlogic.gdx.graphics.Color;
import com.valenguard.client.game.world.entities.Appearance;
import com.valenguard.client.game.world.entities.Entity;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.EntityType;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("ConstantConditions")
@Opcode(getOpcode = Opcodes.APPEARANCE)
public class EntityAppearancePacketIn implements PacketListener<EntityAppearancePacketIn.EntityAppearancePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final EntityType entityType = EntityType.getEntityType(clientHandler.readByte());
        final EntityAppearancePacket entityAppearancePacket = new EntityAppearancePacket(entityId, entityType);

        switch (entityType) {
            case CLIENT_PLAYER:
            case PLAYER:
            case NPC:
                entityAppearancePacket.setHairTexture(clientHandler.readByte());
                entityAppearancePacket.setHelmTexture(clientHandler.readByte());
                entityAppearancePacket.setChestTexture(clientHandler.readByte());
                entityAppearancePacket.setPantsTexture(clientHandler.readByte());
                entityAppearancePacket.setShoesTexture(clientHandler.readByte());
                entityAppearancePacket.setHairColor(new Color(clientHandler.readInt()));
                entityAppearancePacket.setEyeColor(new Color(clientHandler.readInt()));
                entityAppearancePacket.setSkinColor(new Color(clientHandler.readInt()));
                entityAppearancePacket.setGlovesColor(new Color(clientHandler.readInt()));
                break;
            case MONSTER:
            case ITEM_STACK:
            case SKILL_NODE:
                entityAppearancePacket.setMonsterBodyTexture(clientHandler.readByte());
                break;
        }

        return entityAppearancePacket;
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

        switch (packetData.entityType) {
            case CLIENT_PLAYER:
            case PLAYER:
            case NPC:
                appearance.setHairTexture(packetData.hairTexture);
                appearance.setHelmTexture(packetData.helmTexture);
                appearance.setChestTexture(packetData.chestTexture);
                appearance.setPantsTexture(packetData.pantsTexture);
                appearance.setShoesTexture(packetData.shoesTexture);
                appearance.setHairColor(packetData.hairColor);
                appearance.setEyeColor(packetData.eyeColor);
                appearance.setSkinColor(packetData.skinColor);
                appearance.setGlovesColor(packetData.glovesColor);
                break;
            case MONSTER:
            case ITEM_STACK:
            case SKILL_NODE:
                appearance.setMonsterBodyTexture(packetData.monsterBodyTexture);
                break;
        }
    }

    @Getter
    @Setter
    class EntityAppearancePacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;

        private byte monsterBodyTexture;
        private byte hairTexture;
        private byte helmTexture;
        private byte chestTexture;
        private byte pantsTexture;
        private byte shoesTexture;
        private Color hairColor;
        private Color eyeColor;
        private Color skinColor;
        private Color glovesColor;

        EntityAppearancePacket(short entityId, EntityType entityType) {
            this.entityId = entityId;
            this.entityType = entityType;
        }
    }
}
