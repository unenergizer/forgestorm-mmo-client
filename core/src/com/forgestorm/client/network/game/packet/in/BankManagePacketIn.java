package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.BankWindow;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.item.BankActions;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.BANK_MANAGEMENT)
public class BankManagePacketIn implements PacketListener<BankManagePacketIn.BankManagePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final byte bankAction = clientHandler.readByte();

        return new BankManagePacket(BankActions.getType(bankAction));
    }

    @Override
    public void onEvent(BankManagePacket packetData) {
        StageHandler stageHandler = ActorUtil.getStageHandler();
        BankWindow bankWindow = stageHandler.getBankWindow();
        switch (packetData.bankAction) {
            case SERVER_OPEN:
                bankWindow.openWindow();
                EntityManager.getInstance().getPlayerClient().setBankOpen(true);
                break;
            case SERVER_CLOSE:
                EntityManager.getInstance().getPlayerClient().closeBankWindow();
                break;
        }
    }

    @AllArgsConstructor
    class BankManagePacket extends PacketData {
        private final BankActions bankAction;
    }
}
