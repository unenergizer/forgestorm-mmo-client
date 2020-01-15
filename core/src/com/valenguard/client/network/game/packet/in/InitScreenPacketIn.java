package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.game.screens.UserInterfaceType;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.INIT_SCREEN)
public class InitScreenPacketIn implements PacketListener<InitScreenPacketIn.InitCharacterSessionPacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        UserInterfaceType userInterfaceType = UserInterfaceType.getScreenType(clientHandler.readByte());
        println(getClass(), "ScreenSwitch: " + userInterfaceType, false, PRINT_DEBUG);

        return new InitCharacterSessionPacket(userInterfaceType);
    }

    @Override
    public void onEvent(InitCharacterSessionPacket packetData) {

        switch (packetData.userInterfaceType) {
            case LOGIN:
                ActorUtil.getStageHandler().setUserInterface(UserInterfaceType.LOGIN);
                break;
            case CHARACTER_SELECT:
                // Network connection was successful.
//                Valenguard.connectionManager.threadSafeConnectionMessage("Connection successful!");
                ActorUtil.getStageHandler().setUserInterface(UserInterfaceType.CHARACTER_SELECT);

                // Fade this screen in!
                ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getFadeWindow(), 0.2f);
                ActorUtil.getStageHandler().getEscapeWindow().disableButtons(false);
                break;
            case GAME:
                ActorUtil.getStageHandler().setUserInterface(UserInterfaceType.GAME);
                break;
        }
    }

    @AllArgsConstructor
    class InitCharacterSessionPacket extends PacketData {
        private UserInterfaceType userInterfaceType;
    }
}
