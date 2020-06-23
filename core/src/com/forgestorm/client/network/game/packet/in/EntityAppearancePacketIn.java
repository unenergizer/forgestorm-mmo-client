package com.forgestorm.client.network.game.packet.in;

import com.badlogic.gdx.graphics.Color;
import com.forgestorm.client.game.world.entities.Appearance;
import com.forgestorm.client.game.world.entities.Entity;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@SuppressWarnings("ConstantConditions")
@Opcode(getOpcode = Opcodes.APPEARANCE)
public class EntityAppearancePacketIn implements PacketListener<EntityAppearancePacketIn.EntityAppearancePacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final EntityType entityType = EntityType.getEntityType(clientHandler.readByte());
        final EntityAppearancePacket entityAppearancePacket = new EntityAppearancePacket(entityId, entityType);


        println(getClass(), "EntityID: " + entityId, false, PRINT_DEBUG);
        println(getClass(), "EntityType: " + entityType, false, PRINT_DEBUG);

        switch (entityType) {
            case CLIENT_PLAYER:
            case PLAYER:
            case NPC:
                byte hairTexture = clientHandler.readByte();
                byte helmTexture = clientHandler.readByte();
                byte chestTexture = clientHandler.readByte();
                byte pantsTexture = clientHandler.readByte();
                byte shoesTexture = clientHandler.readByte();
                int hairColor = clientHandler.readInt();
                int eyeColor = clientHandler.readInt();
                int skinColor = clientHandler.readInt();
                int glovesColor = clientHandler.readInt();
                byte leftHandTexture = clientHandler.readByte();
                byte rightHandTexture = clientHandler.readByte();

                entityAppearancePacket.setHairTexture(hairTexture);
                entityAppearancePacket.setHelmTexture(helmTexture);
                entityAppearancePacket.setChestTexture(chestTexture);
                entityAppearancePacket.setPantsTexture(pantsTexture);
                entityAppearancePacket.setShoesTexture(shoesTexture);
                entityAppearancePacket.setHairColor(new Color(hairColor));
                entityAppearancePacket.setEyeColor(new Color(eyeColor));
                entityAppearancePacket.setSkinColor(new Color(skinColor));
                entityAppearancePacket.setGlovesColor(new Color(glovesColor));
                entityAppearancePacket.setLeftHandTexture(leftHandTexture);
                entityAppearancePacket.setRightHandTexture(rightHandTexture);

                println(getClass(), "HairTexture: " + hairTexture, false, PRINT_DEBUG);
                println(getClass(), "HelmTexture: " + helmTexture, false, PRINT_DEBUG);
                println(getClass(), "ChestTexture: " + chestTexture, false, PRINT_DEBUG);
                println(getClass(), "PantsTexture: " + pantsTexture, false, PRINT_DEBUG);
                println(getClass(), "ShoesTexture: " + shoesTexture, false, PRINT_DEBUG);
                println(getClass(), "HairColor: " + hairColor, false, PRINT_DEBUG);
                println(getClass(), "EyesColor: " + eyeColor, false, PRINT_DEBUG);
                println(getClass(), "SkinColor: " + skinColor, false, PRINT_DEBUG);
                println(getClass(), "GlovesColor: " + glovesColor, false, PRINT_DEBUG);
                println(getClass(), "LeftHand: " + leftHandTexture, false, PRINT_DEBUG);
                println(getClass(), "RightHand: " + rightHandTexture, false, PRINT_DEBUG);
                break;
            case MONSTER:
            case ITEM_STACK:
            case SKILL_NODE:
                byte monsterBodyTexture = clientHandler.readByte();

                entityAppearancePacket.setMonsterBodyTexture(monsterBodyTexture);

                println(getClass(), "MonsterBodyTexture: " + monsterBodyTexture, false, PRINT_DEBUG);
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
                println(getClass(), "Updating humaniod appearance");

                appearance.setHairTexture(packetData.hairTexture);
                appearance.setHelmTexture(packetData.helmTexture);
                appearance.setChestTexture(packetData.chestTexture);
                appearance.setPantsTexture(packetData.pantsTexture);
                appearance.setShoesTexture(packetData.shoesTexture);
                appearance.setHairColor(packetData.hairColor);
                appearance.setEyeColor(packetData.eyeColor);
                appearance.setSkinColor(packetData.skinColor);
                appearance.setGlovesColor(packetData.glovesColor);
                appearance.setLeftHandTexture(packetData.leftHandTexture);
                appearance.setRightHandTexture(packetData.rightHandTexture);

                println(getClass(), "HairTexture: " + packetData.getHairTexture(), false, PRINT_DEBUG);
                println(getClass(), "HelmTexture: " + packetData.getHelmTexture(), false, PRINT_DEBUG);
                println(getClass(), "ChestTexture: " + packetData.getChestTexture(), false, PRINT_DEBUG);
                println(getClass(), "PantsTexture: " + packetData.getPantsTexture(), false, PRINT_DEBUG);
                println(getClass(), "ShoesTexture: " + packetData.getShoesTexture(), false, PRINT_DEBUG);
                println(getClass(), "HairColor: " + packetData.getHairColor(), false, PRINT_DEBUG);
                println(getClass(), "EyesColor: " + packetData.getEyeColor(), false, PRINT_DEBUG);
                println(getClass(), "SkinColor: " + packetData.getSkinColor(), false, PRINT_DEBUG);
                println(getClass(), "GlovesColor: " + packetData.getGlovesColor(), false, PRINT_DEBUG);
                println(getClass(), "LeftHand: " + packetData.getLeftHandTexture(), false, PRINT_DEBUG);
                println(getClass(), "RightHand: " + packetData.getRightHandTexture(), false, PRINT_DEBUG);

                ((MovingEntity) entity).loadTextures(GameAtlas.ENTITY_CHARACTER);
                break;
            case MONSTER:
            case ITEM_STACK:
            case SKILL_NODE:
                appearance.setMonsterBodyTexture(packetData.monsterBodyTexture);

                println(getClass(), "MonsterBodyTexture: " + packetData.getMonsterBodyTexture(), false, PRINT_DEBUG);
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
        private byte leftHandTexture;
        private byte rightHandTexture;

        EntityAppearancePacket(short entityId, EntityType entityType) {
            this.entityId = entityId;
            this.entityType = entityType;
        }
    }
}
