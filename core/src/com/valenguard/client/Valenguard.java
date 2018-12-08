package com.valenguard.client;

import com.badlogic.gdx.Game;
import com.valenguard.client.game.assets.FileManager;
import com.valenguard.client.game.input.MouseManager;
import com.valenguard.client.game.maps.MapManager;
import com.valenguard.client.game.movement.ClientMovementProcessor;
import com.valenguard.client.game.movement.ClientPlayerMovementManager;
import com.valenguard.client.game.movement.EntityMovementManager;
import com.valenguard.client.game.screens.GameScreen;
import com.valenguard.client.game.screens.LoginScreen;
import com.valenguard.client.game.screens.ScreenType;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.network.ClientConnection;
import com.valenguard.client.network.Consumer;
import com.valenguard.client.network.PlayerSession;
import com.valenguard.client.network.packet.in.EntityAppearanceChange;
import com.valenguard.client.network.packet.in.EntityDespawn;
import com.valenguard.client.network.packet.in.EntityMoveUpdate;
import com.valenguard.client.network.packet.in.EntitySpawn;
import com.valenguard.client.network.packet.in.InitializeClientSession;
import com.valenguard.client.network.packet.in.InitializeGameMap;
import com.valenguard.client.network.packet.in.InventoryUpdate;
import com.valenguard.client.network.packet.in.PingIn;
import com.valenguard.client.network.packet.in.ReceiveChatMessage;
import com.valenguard.client.network.packet.out.OutputStreamManager;
import com.valenguard.client.network.shared.EventBus;
import com.valenguard.client.network.shared.ServerConstants;
import com.valenguard.client.util.Log;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Valenguard extends Game {

    private static final boolean PRINT_DEBUG = true;

    private static Valenguard valenguard;
    public static GameScreen gameScreen;
    public static LoginScreen loginScreen;
    public static ClientConnection clientConnection;

    private StageHandler stageHandler;
    private FileManager fileManager;
    private MapManager mapManager;
    private ClientMovementProcessor clientMovementProcessor;
    private ClientPlayerMovementManager clientPlayerMovementManager;
    private EntityMovementManager entityMovementManager;
    private MouseManager mouseManager;
    private OutputStreamManager outputStreamManager;

    private ScreenType screenType;

    @Setter
    private boolean ideRun;

    private Valenguard() {
    }

    public static Valenguard getInstance() {
        if (valenguard == null) valenguard = new Valenguard();
        return valenguard;
    }

    @Override
    public void create() {
        Log.println(getClass(), "Invoked: create()", false, PRINT_DEBUG);

        // init managers
        outputStreamManager = new OutputStreamManager();
        clientConnection = new ClientConnection();
        fileManager = new FileManager();
        mapManager = new MapManager(ideRun);
        stageHandler = new StageHandler();
        clientMovementProcessor = new ClientMovementProcessor();
        clientPlayerMovementManager = new ClientPlayerMovementManager();
        entityMovementManager = new EntityMovementManager();
        mouseManager = new MouseManager();

        // init screens
        gameScreen = new GameScreen();
        loginScreen = new LoginScreen();
        setScreen(ScreenType.LOGIN);
    }

    public void setScreen(ScreenType screenType) {
        this.screenType = screenType;
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
    public void render() {
        if (clientConnection.isConnected()) clientConnection.getEventBus().gameThreadPublish();
        super.render();
        if (clientConnection.isConnected()) outputStreamManager.sendPackets();
    }

    @Override
    public void dispose() {
        Log.println(getClass(), "Invoked: dispose()", false, PRINT_DEBUG);
        fileManager.dispose();
        mapManager.dispose();
        stageHandler.dispose();

        gameScreen.dispose();
        gameScreen = null;
        loginScreen.dispose();
        loginScreen = null;
    }

    public void initializeNetwork(PlayerSession playerSession) {
        Log.println(getClass(), "Invoked: initializeNetwork()", false, PRINT_DEBUG);
        clientConnection.openConnection(
                playerSession,
                ServerConstants.SERVER_ADDRESS,
                ServerConstants.SERVER_PORT,
                new Consumer<EventBus>() {
                    @Override
                    public void accept(EventBus eventBus) {
                        eventBus.registerListener(new PingIn());
                        eventBus.registerListener(new InitializeClientSession());
                        eventBus.registerListener(new EntityMoveUpdate());
                        eventBus.registerListener(new EntitySpawn());
                        eventBus.registerListener(new EntityDespawn());
                        eventBus.registerListener(new InitializeGameMap());
                        eventBus.registerListener(new ReceiveChatMessage());
                        eventBus.registerListener(new EntityAppearanceChange());
                        eventBus.registerListener(new InventoryUpdate());
                    }
                });
    }
}
