package com.valenguard.client.network.packet.in;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.valenguard.client.Valenguard;
import com.valenguard.client.assets.GameMap;
import com.valenguard.client.constants.ClientConstants;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.maps.data.Location;
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
        final int entityID3 = clientHandler.readInt();
        final short entityID = 0;
        final String mapName = clientHandler.readString();
        final int x = clientHandler.readInt();
        final int y = clientHandler.readInt();
        // PACKET READ END

        Gdx.app.debug(TAG, "[PACKET] loginSuccess: " + loginSuccess
                + " , EntityID: " + entityID
                + " , Map: " + mapName
                + " , X: " + x
                + " , Y: " + y);

        PlayerClient playerClient = new PlayerClient();
        playerClient.setEntityId(entityID);
        playerClient.setMapName(mapName);
        playerClient.setCurrentMapLocation(new Location(mapName, x, y));
        playerClient.setFutureMapLocation(new Location(mapName, x, y));
        playerClient.setDrawX(x * ClientConstants.TILE_SIZE);
        playerClient.setDrawY(y * ClientConstants.TILE_SIZE);

        EntityManager.getInstance().addEntity(PlayerClient.class, entityID, playerClient);

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
