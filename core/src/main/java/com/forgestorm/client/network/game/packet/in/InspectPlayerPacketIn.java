package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.INSPECT_PLAYER)
public class InspectPlayerPacketIn implements PacketListener<InspectPlayerPacketIn.InspectPlayerPacket> {

    private static final boolean PRINT_DEBUG = false;
    private final ClientMain clientMain;

    public InspectPlayerPacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

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

        clientMain.getStageHandler().getCharacterInspectionWindow().inspectCharacter(packetData.itemIds);
    }

    @AllArgsConstructor
    static class InspectPlayerPacket extends PacketData {
        private final int[] itemIds;
    }
}
