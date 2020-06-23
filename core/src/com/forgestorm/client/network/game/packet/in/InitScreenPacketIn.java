package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

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
//                ClientMain.connectionManager.threadSafeConnectionMessage("Connection successful!");
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
