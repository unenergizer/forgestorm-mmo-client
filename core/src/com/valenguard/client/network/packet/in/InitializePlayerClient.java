package com.valenguard.client.network.packet.in;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.valenguard.client.Valenguard;
import com.valenguard.client.assets.GameMap;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.network.ClientConnection;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketListener;
import com.valenguard.client.screens.ScreenType;

public class InitializePlayerClient implements PacketListener {

    private static final String TAG = InitializePlayerClient.class.getSimpleName();

    @Opcode(getOpcode = Opcodes.INIT_PLAYER_CLIENT)
    public void onInitializePlayerClient(ClientHandler clientHandler) {

        final ClientConnection client = ClientConnection.getInstance();

        // TODO: Check for authentication

        // Network connection was successful.
        Gdx.app.debug(TAG, "Connection successful!");
        client.threadSafeConnectionMessage("Connection successful!", Color.GREEN);

        // PACKET READ START
        final boolean loginSuccess = clientHandler.readBoolean();
        final short entityID = clientHandler.readShort();
        final String mapName = clientHandler.readString();
        // PACKET READ END

        Gdx.app.debug(TAG, "[PACKET] loginSuccess: " + loginSuccess
                + " , EntityID: " + entityID
                + " , Map: " + mapName);


        PlayerClient playerClient = new PlayerClient();
        playerClient.setEntityId(entityID);

        // todo move the mapName to a more static context
        playerClient.setMapName(mapName);

        // Setup the player client entity
        EntityManager.getInstance().setPlayerClient(playerClient);

        // Load the right map (on the libGDX thread)
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                // Set map to show client
                // TODO: Remove this call here and put in game screen. Use client player data to get map name.
                Valenguard.gameScreen.setGameMap(GameMap.getMapByName(mapName));

                // Now switch to the game screen!
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        // Wait a few seconds to show the connected message
                        Valenguard.getInstance().setScreen(ScreenType.GAME);
                    }
                }, 1);
            }
        });
    }
}
