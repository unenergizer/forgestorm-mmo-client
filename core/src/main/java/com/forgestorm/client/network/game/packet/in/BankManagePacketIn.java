package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.BankWindow;
import com.forgestorm.client.game.world.item.BankActions;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.BANK_MANAGEMENT)
public class BankManagePacketIn implements PacketListener<BankManagePacketIn.BankManagePacket> {

    private final ClientMain clientMain;

    public BankManagePacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final byte bankAction = clientHandler.readByte();

        return new BankManagePacket(BankActions.getType(bankAction));
    }

    @Override
    public void onEvent(BankManagePacket packetData) {
        StageHandler stageHandler = clientMain.getStageHandler();
        BankWindow bankWindow = stageHandler.getBankWindow();
        switch (packetData.bankAction) {
            case SERVER_OPEN:
                bankWindow.openWindow();
                clientMain.getEntityManager().getPlayerClient().setBankOpen(true);
                break;
            case SERVER_CLOSE:
                clientMain.getEntityManager().getPlayerClient().closeBankWindow();
                break;
        }
    }

    @AllArgsConstructor
    static class BankManagePacket extends PacketData {
        private final BankActions bankAction;
    }
}
