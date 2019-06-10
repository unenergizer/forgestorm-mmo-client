package com.valenguard.client.network.game.packet.out;

import com.badlogic.gdx.graphics.Color;
import com.valenguard.client.game.screens.ui.actors.dev.NPCEditorData;
import com.valenguard.client.network.game.shared.Opcodes;

public class AdminEditorNPCPacketOut extends AbstractClientPacketOut {

    private final NPCEditorData npcEditorData;

    public AdminEditorNPCPacketOut(NPCEditorData npcEditorData) {
        super(Opcodes.ADMIN_EDITOR_NPC);
        this.npcEditorData = npcEditorData;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        if (npcEditorData == null) return;

        // Editor data
        write.writeBoolean(npcEditorData.isSpawn());
        write.writeBoolean(npcEditorData.isSave());

        // Basic data
        write.writeShort(npcEditorData.getEntityID());
        write.writeString(npcEditorData.getName());
        write.writeString(npcEditorData.getFaction());
        write.writeInt(npcEditorData.getHealth());
        write.writeInt(npcEditorData.getDamage());
        write.writeInt(npcEditorData.getExpDrop());
        write.writeInt(npcEditorData.getDropTable());
        write.writeFloat(npcEditorData.getWalkSpeed());
        write.writeFloat(npcEditorData.getProbStop());
        write.writeFloat(npcEditorData.getProbWalk());
        write.writeShort(npcEditorData.getShopId());
        write.writeBoolean(npcEditorData.isBankKeeper());

        // World data
        write.writeString(npcEditorData.getSpawnLocation().getMapName());
        write.writeShort(npcEditorData.getSpawnLocation().getX());
        write.writeShort(npcEditorData.getSpawnLocation().getY());

        // Appearance data
        write.writeByte(npcEditorData.getHairTexture());
        write.writeByte(npcEditorData.getHelmTexture());
        write.writeByte(npcEditorData.getChestTexture());
        write.writeByte(npcEditorData.getPantsTexture());
        write.writeByte(npcEditorData.getShoesTexture());
        write.writeInt(Color.rgba8888(npcEditorData.getHairColor()));
        write.writeInt(Color.rgba8888(npcEditorData.getEyesColor()));
        write.writeInt(Color.rgba8888(npcEditorData.getSkinColor()));
        write.writeInt(Color.rgba8888(npcEditorData.getGlovesColor()));
    }
}