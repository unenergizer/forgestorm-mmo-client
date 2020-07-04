package com.forgestorm.client.network.game.packet.out;

import com.badlogic.gdx.graphics.Color;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.EntityEditorData;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.ItemStackDropData;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.MonsterData;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.NPCData;
import com.forgestorm.client.network.game.shared.Opcodes;

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
        write.writeBoolean(entityEditorData.isDelete());

        // World data
        write.writeString(entityEditorData.getSpawnLocation().getMapName());
        write.writeShort(entityEditorData.getSpawnLocation().getX());
        write.writeShort(entityEditorData.getSpawnLocation().getY());

        write.writeShort(entityEditorData.getEntityID());

        switch (entityEditorData.getEntityType()) {
            case NPC:
                NPCData npcData = (NPCData) entityEditorData;

                write.writeString(npcData.getName());
                write.writeString(npcData.getFaction());
                write.writeInt(npcData.getHealth());
                write.writeInt(npcData.getDamage());
                write.writeInt(npcData.getExpDrop());
                write.writeInt(npcData.getDropTable());
                write.writeFloat(npcData.getWalkSpeed());
                write.writeFloat(npcData.getProbStop());
                write.writeFloat(npcData.getProbWalk());
                write.writeShort(npcData.getShopId());
                write.writeBoolean(npcData.isBankKeeper());

                // Appearance data
                write.writeByte(npcData.getHairTexture());
                write.writeByte(npcData.getHelmTexture());
                write.writeByte(npcData.getChestTexture());
                write.writeByte(npcData.getPantsTexture());
                write.writeByte(npcData.getShoesTexture());
                write.writeInt(Color.rgba8888(npcData.getHairColor()));
                write.writeInt(Color.rgba8888(npcData.getEyesColor()));
                write.writeInt(Color.rgba8888(npcData.getSkinColor()));
                write.writeInt(Color.rgba8888(npcData.getGlovesColor()));
                break;
            case MONSTER:
                MonsterData monsterData = (MonsterData) entityEditorData;

                write.writeString(monsterData.getName());
                write.writeByte(monsterData.getEntityAlignment().getEntityAlignmentByte());
                write.writeInt(monsterData.getHealth());
                write.writeInt(monsterData.getDamage());
                write.writeInt(monsterData.getExpDrop());
                write.writeInt(monsterData.getDropTable());
                write.writeFloat(monsterData.getWalkSpeed());
                write.writeFloat(monsterData.getProbStop());
                write.writeFloat(monsterData.getProbWalk());
                write.writeShort(monsterData.getShopId());
                write.writeBoolean(monsterData.isBankKeeper());

                // Appearance data
                write.writeByte(monsterData.getMonsterBodyTexture());
                break;
            case ITEM_STACK:
                ItemStackDropData itemStackDropData = (ItemStackDropData) entityEditorData;

                write.writeInt(itemStackDropData.getItemStackId());
                write.writeInt(itemStackDropData.getAmount());
                write.writeInt(itemStackDropData.getRespawnTimeMin());
                write.writeInt(itemStackDropData.getRespawnTimeMax());
                break;
        }
    }
}