package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.INSPECT_PLAYER)
public class InspectPlayerPacketIn implements PacketListener<InspectPlayerPacketIn.InspectPlayerPacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        int[] itemIds = new int[ClientConstants.EQUIPMENT_INVENTORY_SIZE];

        for (int i = 0; i < ClientConstants.EQUIPMENT_INVENTORY_SIZE; i++) {
            itemIds[i] = clientHandler.readInt();
        }

        return new InspectPlayerPacket(itemIds);
    }

    @Override
    public void onEvent(InspectPlayerPacket packetData) {
        if (PRINT_DEBUG) {
            StringBuilder stringBuilder = new StringBuilder("Items: ");
            for (int i = 0; i < ClientConstants.EQUIPMENT_INVENTORY_SIZE; i++) {
                stringBuilder.append(packetData.itemIds[i]).append(", ");
            }
            println(getClass(), stringBuilder.toString(), false, true);
        }

        ActorUtil.getStageHandler().getCharacterInspectionWindow().inspectCharacter(packetData.itemIds);
    }

    @AllArgsConstructor
    class InspectPlayerPacket extends PacketData {
        private int[] itemIds;
    }
}
