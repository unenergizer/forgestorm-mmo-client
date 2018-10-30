package com.valenguard.client;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.valenguard.client.assets.FileManager;
import com.valenguard.client.maps.MapManager;
import com.valenguard.client.movement.ClientPlayerMovementManager;
import com.valenguard.client.movement.EntityMovementManager;
import com.valenguard.client.movement.MouseManager;
import com.valenguard.client.network.ClientConnection;
import com.valenguard.client.network.PingManager;
import com.valenguard.client.network.packet.in.EntityDespawn;
import com.valenguard.client.network.packet.in.EntityMoveUpdate;
import com.valenguard.client.network.packet.in.EntitySpawn;
import com.valenguard.client.network.packet.in.InitializePlayerClient;
import com.valenguard.client.network.packet.in.PingIn;
import com.valenguard.client.network.shared.EventBus;
import com.valenguard.client.network.shared.ServerConstants;
import com.valenguard.client.screens.GameScreen;
import com.valenguard.client.screens.LoginScreen;
import com.valenguard.client.screens.ScreenType;
import com.valenguard.client.screens.stage.UiManager;
import com.valenguard.client.util.Consumer;

import lombok.Getter;

@Getter
public class Valenguard extends Game {

    private static final String TAG = Valenguard.class.getSimpleName();
    private static Valenguard valenguard;

    public static GameScreen gameScreen;
    public static LoginScreen loginScreen;

    private FileManager fileManager;
    private MapManager mapManager;
    private UiManager uiManager;
    private PingManager pingManager;
    private ClientPlayerMovementManager clientPlayerMovementManager;
    private EntityMovementManager entityMovementManager;
    private MouseManager mouseManager;

    private Valenguard() {
    }

    public static Valenguard getInstance() {
        if (valenguard == null) valenguard = new Valenguard();
        return valenguard;
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        // init managers
        fileManager = new FileManager();
        mapManager = new MapManager();
        uiManager = new UiManager();
        pingManager = new PingManager();
        clientPlayerMovementManager = new ClientPlayerMovementManager();
        entityMovementManager = new EntityMovementManager();
        mouseManager = new MouseManager();

        // init screens
        gameScreen = new GameScreen();
        loginScreen = new LoginScreen();
        setScreen(ScreenType.LOGIN);
    }

    public void setScreen(ScreenType screenType) {
        switch (screenType) {
            case LOGIN:
                setScreen(loginScreen);
                break;
            case GAME:
                setScreen(gameScreen);
                break;
        }
    }

    @Override
    public void dispose() {
        // dispose classes and assets
        fileManager.dispose();
        mapManager.dispose();
        uiManager.dispose();

        // dispose screens
        gameScreen.dispose();
        loginScreen.dispose();
    }

    public void initializeNetwork() {
        ClientConnection.getInstance().openConnection(
                ServerConstants.SERVER_ADDRESS,
                ServerConstants.SERVER_PORT,
                new Consumer<EventBus>() {
                    @Override
                    public void accept(EventBus eventBus) {
                        eventBus.registerListener(new PingIn());
                        eventBus.registerListener(new InitializePlayerClient());
                        eventBus.registerListener(new EntityMoveUpdate());
                        eventBus.registerListener(new EntitySpawn());
                        eventBus.registerListener(new EntityDespawn());
                    }
                });
    }
}
