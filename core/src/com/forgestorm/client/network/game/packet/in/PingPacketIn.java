package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.network.game.packet.out.PingPacketOut;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.PING)
public class PingPacketIn implements PacketListener<PingPacketIn.PingInPacket> {

    private ClientHandler clientHandler;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        return new PingInPacket(System.currentTimeMillis());
    }

    @Override
    public void onEvent(PingInPacket packetData) {
        long currentTime = System.currentTimeMillis();
        long processTime = currentTime - packetData.packetReceivedTime;
        long networkTime = currentTime - clientHandler.getPingSendTime();
        long ping = networkTime - processTime;
        clientHandler.setClientPing(ping);

//        println(getClass(), "Ping: " + ping);
        ClientMain.getInstance().getStageHandler().getPing().setPing(ping);
        new PingPacketOut(clientHandler).sendPacket();
    }

    @AllArgsConstructor
    class PingInPacket extends PacketData {
        private long packetReceivedTime;
    }
}
