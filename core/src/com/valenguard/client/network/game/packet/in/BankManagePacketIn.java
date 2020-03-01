package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.game.draggable.BankWindow;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.item.BankActions;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

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
