package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.INIT_SCREEN)
public class InitScreenPacketIn implements PacketListener<InitScreenPacketIn.InitCharacterSessionPacket> {

    private static final boolean PRINT_DEBUG = false;

    private final ClientMain clientMain;

    public InitScreenPacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

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
                clientMain.getStageHandler().setUserInterface(UserInterfaceType.LOGIN);
                break;
            case CHARACTER_SELECT:
                // Network connection was successful.
//                ClientMain.getInstance().getConnectionManager().threadSafeConnectionMessage("Connection successful!");
                clientMain.getStageHandler().setUserInterface(UserInterfaceType.CHARACTER_SELECT);

                // Fade this screen in!
                ActorUtil.fadeOutWindow(clientMain.getStageHandler().getFadeWindow(), 0.2f);
                clientMain.getStageHandler().getEscapeWindow().disableButtons(false);
                break;
            case GAME:
                clientMain.getStageHandler().setUserInterface(UserInterfaceType.GAME);
                break;
        }
    }

    @AllArgsConstructor
    static class InitCharacterSessionPacket extends PacketData {
        private final UserInterfaceType userInterfaceType;
    }
}
