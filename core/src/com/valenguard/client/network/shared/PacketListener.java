package com.valenguard.client.network.shared;

public interface PacketListener <T extends PacketData> {

    PacketData decodePacket(final ClientHandler clientHandler);

    void onEvent(final T packetData);

}
