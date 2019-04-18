package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.game.world.item.BankActions;
import com.valenguard.client.network.game.shared.Opcodes;

public class BankManagePacketOut extends AbstractClientPacketOut {

    private final BankActions bankAction;

    public BankManagePacketOut(BankActions bankAction) {
        super(Opcodes.BANK_MANAGEMENT);
        this.bankAction = bankAction;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        write.writeByte(bankAction.getTypeByte());
    }
}
