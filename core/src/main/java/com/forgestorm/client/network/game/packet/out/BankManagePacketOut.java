package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.item.BankActions;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

public class BankManagePacketOut extends AbstractPacketOut {

    private final BankActions bankAction;

    public BankManagePacketOut(BankActions bankAction) {
        super(Opcodes.BANK_MANAGEMENT);
        this.bankAction = bankAction;
    }

    @Override
    public void createPacket(GameOutputStream write) {
        write.writeByte(bankAction.getTypeByte());
    }
}
