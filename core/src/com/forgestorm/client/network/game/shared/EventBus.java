package com.forgestorm.client.network.game.shared;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.forgestorm.client.util.Log.println;

@SuppressWarnings("unchecked")
public class EventBus {

    private static final boolean PRINT_DEBUG = true;
    private final Map<Byte, PacketListener> packetListenerMap = new ConcurrentHashMap<Byte, PacketListener>();
    private final Queue<PacketData> decodedPackets = new ConcurrentLinkedQueue<PacketData>();

    /**
     * Prepares the server to listen to a particular packet.
     * Registers Opcodes found via annotations in the PacketListener class.
     *
     * @param packetListener The PacketListener we will listen for.
     */
    public void registerListener(PacketListener packetListener) {
        boolean foundAnnotation = false;
        for (Annotation annotation : packetListener.getClass().getAnnotations()) {
            if (!annotation.annotationType().equals(Opcode.class)) continue;
            if (foundAnnotation)
                throw new RuntimeException("Cannot have more than one annotation for packet listeners: " + packetListener);
            foundAnnotation = true;
            packetListenerMap.put(((Opcode) annotation).getOpcode(), packetListener);
        }
        if (!foundAnnotation)
            throw new RuntimeException("Could not find an annotation for the packet listener: " + packetListener);
    }

    public void decodeListenerOnNetworkThread(byte opcode, ClientHandler clientHandler) {
        PacketListener packetListener = getPacketListener(opcode);
        println(getClass(), "PACKET IN: " + packetListener, false, PRINT_DEBUG);
        if (packetListener == null) return;
        PacketData packetData = packetListener.decodePacket(clientHandler);
        packetData.setOpcode(opcode);
        decodedPackets.add(packetData);
    }

    private PacketListener getPacketListener(byte opcode) {
        PacketListener packetListener = packetListenerMap.get(opcode);
        if (packetListener == null)
            println(getClass(), "Callback data was null for " + opcode + ". Is the event registered?", false);
        return packetListener;
    }

    public void gameThreadPublish() {
        PacketData packetData;
        while ((packetData = decodedPackets.poll()) != null) {
            publishOnGameThread(packetData);
        }
    }

    private void publishOnGameThread(PacketData packetData) {
        PacketListener packetListener = getPacketListener(packetData.getOpcode());
        if (packetListener == null) return;
        //noinspection unchecked
        packetListener.onEvent(packetData);
    }

    public void disconnect() {
        decodedPackets.clear();
        println(getClass(), "Event bus dispose", false, PRINT_DEBUG);
    }
}
