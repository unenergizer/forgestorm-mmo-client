package com.valenguard.client.network.game.packet.out;

import com.badlogic.gdx.graphics.Color;
import com.valenguard.client.game.screens.ui.actors.dev.entity.EntityEditorData;
import com.valenguard.client.game.world.entities.EntityType;
import com.valenguard.client.network.game.shared.Opcodes;

public class AdminEditorEntityPacketOut extends AbstractClientPacketOut {

    private final EntityEditorData entityEditorData;

    public AdminEditorEntityPacketOut(EntityEditorData entityEditorData) {
        super(Opcodes.ADMIN_EDITOR_ENTITY);
        this.entityEditorData = entityEditorData;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        if (entityEditorData == null) return;

        write.writeByte(entityEditorData.getEntityType().getEntityTypeByte());

        // Editor data
        write.writeBoolean(entityEditorData.isSpawn());
        write.writeBoolean(entityEditorData.isSave());

        // Basic data
        write.writeShort(entityEditorData.getEntityID());
        write.writeString(entityEditorData.getName());
        if (entityEditorData.getEntityType() == EntityType.MONSTER) {
            write.writeByte(entityEditorData.getEntityAlignment().getEntityAlignmentByte());
        } else if (entityEditorData.getEntityType() == EntityType.NPC) {
            write.writeString(entityEditorData.getFaction());
        }
        write.writeInt(entityEditorData.getHealth());
        write.writeInt(entityEditorData.getDamage());
        write.writeInt(entityEditorData.getExpDrop());
        write.writeInt(entityEditorData.getDropTable());
        write.writeFloat(entityEditorData.getWalkSpeed());
        write.writeFloat(entityEditorData.getProbStop());
        write.writeFloat(entityEditorData.getProbWalk());
        write.writeShort(entityEditorData.getShopId());
        write.writeBoolean(entityEditorData.isBankKeeper());

        // World data
        write.writeString(entityEditorData.getSpawnLocation().getMapName());
        write.writeShort(entityEditorData.getSpawnLocation().getX());
        write.writeShort(entityEditorData.getSpawnLocation().getY());

        // Appearance data

        if (entityEditorData.getEntityType() == EntityType.MONSTER) {
            write.writeByte(entityEditorData.getMonsterBodyTexture());
        } else if (entityEditorData.getEntityType() == EntityType.NPC) {
            write.writeByte(entityEditorData.getHairTexture());
            write.writeByte(entityEditorData.getHelmTexture());
            write.writeByte(entityEditorData.getChestTexture());
            write.writeByte(entityEditorData.getPantsTexture());
            write.writeByte(entityEditorData.getShoesTexture());
            write.writeInt(Color.rgba8888(entityEditorData.getHairColor()));
            write.writeInt(Color.rgba8888(entityEditorData.getEyesColor()));
            write.writeInt(Color.rgba8888(entityEditorData.getSkinColor()));
            write.writeInt(Color.rgba8888(entityEditorData.getGlovesColor()));
        }
    }
}