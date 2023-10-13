package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.PROFILE_REQUEST)
public class ProfileRequestPacketIn implements PacketListener<ProfileRequestPacketIn.XenforoProfilePacket> {

    private final static boolean PRINT_DEBUG = false;

    private final ClientMain clientMain;
    public ProfileRequestPacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        String accountName = clientHandler.readString();
        int xenForoUserID = clientHandler.readInt();
        int messageCount = clientHandler.readInt();
        int trophyPoints = clientHandler.readInt();
        String gravatarHash = clientHandler.readString();
        int reactionScore = clientHandler.readInt();

        return new XenforoProfilePacket(accountName, xenForoUserID, messageCount, trophyPoints, gravatarHash, reactionScore);
    }

    @Override
    public void onEvent(XenforoProfilePacket packetData) {
        println(getClass(), "AccountName: " + packetData.getAccountName(), false, PRINT_DEBUG);
        println(getClass(), "XenforoUserID: " + packetData.xenforoUserID, false, PRINT_DEBUG);
        println(getClass(), "MessageCount: " + packetData.messageCount, false, PRINT_DEBUG);
        println(getClass(), "TrophyPoints: " + packetData.trophyPoints, false, PRINT_DEBUG);
        println(getClass(), "GravatarHash: " + packetData.gravatarHash, false, PRINT_DEBUG);
        println(getClass(), "ReactionScore: " + packetData.reactionScore, false, PRINT_DEBUG);

        clientMain.getStageHandler().getPlayerProfileWindow().packetResponse(packetData);
    }

    @Getter
    @AllArgsConstructor
    public static class XenforoProfilePacket extends PacketData {
        private final String accountName;
        private final int xenforoUserID;
        private final int messageCount;
        private final int trophyPoints;
        private final String gravatarHash;
        private final int reactionScore;
    }
}
