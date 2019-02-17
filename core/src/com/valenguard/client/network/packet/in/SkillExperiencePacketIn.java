package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.rpg.SkillOpcodes;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.EXPERIENCE)
public class SkillExperiencePacketIn implements PacketListener<SkillExperiencePacketIn.SkillExperiencePacket> {

    private final static boolean PRINT_DEBUG = true;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final byte skillOpcode = clientHandler.readByte();
        final int experienceGained = clientHandler.readInt();
        return new SkillExperiencePacket(skillOpcode, experienceGained);
    }

    @Override
    public void onEvent(SkillExperiencePacket packetData) {

        println(getClass(), "Opcode: " + SkillOpcodes.getSkillOpcode(packetData.skillOpcode), false, PRINT_DEBUG);
        println(getClass(), "Experience: " + packetData.experienceGained, false, PRINT_DEBUG);

        Valenguard.getInstance().getSkills()
                .getSkill(SkillOpcodes.getSkillOpcode(packetData.skillOpcode))
                .addExperience(packetData.experienceGained);
    }

    @AllArgsConstructor
    class SkillExperiencePacket extends PacketData {
        private byte skillOpcode;
        private int experienceGained;
    }
}
