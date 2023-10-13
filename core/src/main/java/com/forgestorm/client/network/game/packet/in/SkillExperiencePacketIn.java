package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.rpg.SkillOpcodes;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.EXPERIENCE)
public class SkillExperiencePacketIn implements PacketListener<SkillExperiencePacketIn.SkillExperiencePacket> {

    private final static boolean PRINT_DEBUG = false;
    private final ClientMain clientMain;

    public SkillExperiencePacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

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

        clientMain.getSkills()
                .getSkill(SkillOpcodes.getSkillOpcode(packetData.skillOpcode))
                .addExperience(packetData.experienceGained);
    }

    @AllArgsConstructor
    static class SkillExperiencePacket extends PacketData {
        private final byte skillOpcode;
        private final int experienceGained;
    }
}
