package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.EXPERIENCE)
public class SkillExperiencePacketIn implements PacketListener<SkillExperiencePacketIn.SkillExperiencePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final byte skillOpcode = clientHandler.readByte();
        final int experienceGained = clientHandler.readInt();
        return new SkillExperiencePacket(skillOpcode, experienceGained);
    }

    @Override
    public void onEvent(SkillExperiencePacket packetData) {
        Valenguard.getInstance().getSkills()
                .getSkill(packetData.skillOpcode)
                .addExperience(packetData.experienceGained);
    }

    @AllArgsConstructor
    class SkillExperiencePacket extends PacketData {
        private byte skillOpcode;
        private int experienceGained;
    }
}
