package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.PROFILE_REQUEST)
public class ProfileRequestPacketIn implements PacketListener<ProfileRequestPacketIn.XenforoProfilePacket> {

    private final static boolean PRINT_DEBUG = false;

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

        ActorUtil.getStageHandler().getPlayerProfileWindow().packetResponse(packetData);
    }

    @Getter
    @AllArgsConstructor
    public static class XenforoProfilePacket extends PacketData {
        private String accountName;
        private int xenforoUserID;
        private int messageCount;
        private int trophyPoints;
        private String gravatarHash;
        private int reactionScore;
    }
}
