package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.INSPECT_PLAYER)
public class InspectPlayerPacketIn implements PacketListener<InspectPlayerPacketIn.ChatMessagePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        int itemIds[] = new int[ClientConstants.EQUIPMENT_INVENTORY_SIZE];

        for (int i = 0; i < ClientConstants.EQUIPMENT_INVENTORY_SIZE; i++) {
            itemIds[i] = clientHandler.readInt();
        }

        return new ChatMessagePacket(itemIds);
    }

    @Override
    public void onEvent(ChatMessagePacket packetData) {
        StringBuilder stringBuilder = new StringBuilder("Items: ");
        for (int i = 0; i < ClientConstants.EQUIPMENT_INVENTORY_SIZE; i++) {
            stringBuilder.append(packetData.itemIds[i]).append(", ");
        }
        ActorUtil.getStageHandler().getChatWindow().appendChatMessage(stringBuilder.toString());
    }

    @AllArgsConstructor
    class ChatMessagePacket extends PacketData {
        private int itemIds[];
    }
}
