package com.valenguard.client;

import com.badlogic.gdx.Game;
import com.valenguard.client.game.abilities.AbilityManager;
import com.valenguard.client.game.audio.AudioManager;
import com.valenguard.client.game.input.MouseManager;
import com.valenguard.client.game.movement.ClientMovementProcessor;
import com.valenguard.client.game.movement.ClientPlayerMovementManager;
import com.valenguard.client.game.movement.EntityMovementManager;
import com.valenguard.client.game.movement.EntityTracker;
import com.valenguard.client.game.rpg.EntityShopManager;
import com.valenguard.client.game.rpg.Skills;
import com.valenguard.client.game.screens.CharacterSelectScreen;
import com.valenguard.client.game.screens.GameScreen;
import com.valenguard.client.game.screens.LoginScreen;
import com.valenguard.client.game.screens.ScreenType;
import com.valenguard.client.game.screens.WindowManager;
import com.valenguard.client.game.screens.effects.EffectManager;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.item.ItemStackManager;
import com.valenguard.client.game.world.item.inventory.MoveInventoryEvents;
import com.valenguard.client.game.world.item.trade.TradeManager;
import com.valenguard.client.game.world.maps.MapManager;
import com.valenguard.client.io.FileManager;
import com.valenguard.client.io.NetworkSettingsLoader;
import com.valenguard.client.network.ConnectionManager;
import com.valenguard.client.network.game.ClientGameConnection;
import com.valenguard.client.network.game.Consumer;
import com.valenguard.client.network.game.LoginCredentials;
import com.valenguard.client.network.game.packet.in.AiEntityDataPacketIn;
import com.valenguard.client.network.game.packet.in.BankManagePacketIn;
import com.valenguard.client.network.game.packet.in.CharactersMenuLoadPacketIn;
import com.valenguard.client.network.game.packet.in.ChatMessagePacketIn;
import com.valenguard.client.network.game.packet.in.EntityAppearancePacketIn;
import com.valenguard.client.network.game.packet.in.EntityAttributesUpdatePacketIn;
import com.valenguard.client.network.game.packet.in.EntityDamagePacketIn;
import com.valenguard.client.network.game.packet.in.EntityDespawnPacketIn;
import com.valenguard.client.network.game.packet.in.EntityHealthPacketIn;
import com.valenguard.client.network.game.packet.in.EntityMovePacketIn;
import com.valenguard.client.network.game.packet.in.EntitySpawnPacketIn;
import com.valenguard.client.network.game.packet.in.EntityUpdatePacketIn;
import com.valenguard.client.network.game.packet.in.InitClientPrivilegePacketIn;
import com.valenguard.client.network.game.packet.in.InitScreenPacketIn;
import com.valenguard.client.network.game.packet.in.InitializeGameMapPacketIn;
import com.valenguard.client.network.game.packet.in.InspectPlayerPacketIn;
import com.valenguard.client.network.game.packet.in.InventoryPacketIn;
import com.valenguard.client.network.game.packet.in.MovingEntityTeleportPacketIn;
import com.valenguard.client.network.game.packet.in.PingPacketIn;
import com.valenguard.client.network.game.packet.in.PlayerTradePacketIn;
import com.valenguard.client.network.game.packet.in.SkillExperiencePacketIn;
import com.valenguard.client.network.game.packet.out.OutputStreamManager;
import com.valenguard.client.network.game.shared.EventBus;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;

@Getter
public class Valenguard extends Game {

    private static final boolean PRINT_DEBUG = false;

    private final LoginCredentials loginCredentials = new LoginCredentials();

    private static Valenguard valenguard;
    public static ConnectionManager connectionManager;
    public static GameScreen gameScreen;
    public static CharacterSelectScreen characterSelectScreen;
    public static LoginScreen loginScreen;

    @Setter
    private boolean isAdmin = false;
    @Setter
    private boolean isModerator = false;

    private AudioManager audioManager;
    private EntityTracker entityTracker;
    private WindowManager windowManager;
    private StageHandler stageHandler;
    private FileManager fileManager;
    private MapManager mapManager;
    private ClientMovementProcessor clientMovementProcessor;
    private ClientPlayerMovementManager clientPlayerMovementManager;
    private EntityMovementManager entityMovementManager;
    private MouseManager mouseManager;
    private OutputStreamManager outputStreamManager;
    private ItemStackManager itemStackManager;
    private Skills skills;
    private EntityShopManager entityShopManager;
    private MoveInventoryEvents moveInventoryEvents;
    private AbilityManager abilityManager;
    private EffectManager effectManager;

    private TradeManager tradeManager;

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
        println(getClass(), "Invoked: create()", false, PRINT_DEBUG);

        // loadItems managers
        audioManager = new AudioManager();
        connectionManager = new ConnectionManager();
        outputStreamManager = new OutputStreamManager();
        fileManager = new FileManager();
        mapManager = new MapManager(ideRun);
        stageHandler = new StageHandler();
        clientMovementProcessor = new ClientMovementProcessor();
        clientPlayerMovementManager = new ClientPlayerMovementManager();
        entityMovementManager = new EntityMovementManager();
        mouseManager = new MouseManager();
        windowManager = new WindowManager();
        itemStackManager = new ItemStackManager();
        skills = new Skills();
        entityTracker = new EntityTracker();
        tradeManager = new TradeManager();
        entityShopManager = new EntityShopManager();
        moveInventoryEvents = new MoveInventoryEvents();
        abilityManager = new AbilityManager();
        effectManager = new EffectManager();

        // loadItems screens
        gameScreen = new GameScreen();
        characterSelectScreen = new CharacterSelectScreen();
        loginScreen = new LoginScreen();
        setScreen(ScreenType.LOGIN);
    }

    public void setScreen(ScreenType screenType) {
        this.screenType = screenType;
        switch (screenType) {
            case LOGIN:
                setScreen(loginScreen);
                break;
            case CHARACTER_SELECT:
                setScreen(characterSelectScreen);
                break;
            case GAME:
                setScreen(gameScreen);
                break;
        }
    }

    @Override
    public void render() {
        ClientGameConnection clientGameConnection = connectionManager.getClientGameConnection();

        if (clientGameConnection.isConnected()) {
            clientGameConnection.getEventBus().gameThreadPublish();
        }

        super.render();

        if (clientGameConnection.isConnected()) {
            outputStreamManager.sendPackets(clientGameConnection.getClientHandler());
        }
    }

    @Override
    public void dispose() {
        println(getClass(), "Invoked: dispose()", false, PRINT_DEBUG);
        fileManager.dispose();
        mapManager.dispose();
        stageHandler.dispose();
        skills = null;

        tradeManager = null;

        gameScreen.dispose();
        gameScreen = null;
        loginScreen.dispose();
        loginScreen = null;
        connectionManager.disconnect();
        connectionManager = null;
        EntityManager.getInstance().dispose();
    }

    public void initializeNetwork() {
        println(getClass(), "Invoked: initializeNetwork()", false, PRINT_DEBUG);

        NetworkSettingsLoader networkSettingsLoader = new NetworkSettingsLoader();
        connectionManager.setupConnection(
                networkSettingsLoader.loadNetworkSettings(),
                loginCredentials,
                new Consumer<EventBus>() {
                    @Override
                    public void accept(EventBus eventBus) {
                        eventBus.registerListener(new PingPacketIn());
                        eventBus.registerListener(new InitScreenPacketIn());
                        eventBus.registerListener(new EntityMovePacketIn());
                        eventBus.registerListener(new EntitySpawnPacketIn());
                        eventBus.registerListener(new EntityDespawnPacketIn());
                        eventBus.registerListener(new InitializeGameMapPacketIn());
                        eventBus.registerListener(new ChatMessagePacketIn());
                        eventBus.registerListener(new EntityAppearancePacketIn());
                        eventBus.registerListener(new InventoryPacketIn());
                        eventBus.registerListener(new SkillExperiencePacketIn());
                        eventBus.registerListener(new EntityAttributesUpdatePacketIn());
                        eventBus.registerListener(new MovingEntityTeleportPacketIn());
                        eventBus.registerListener(new EntityDamagePacketIn());
                        eventBus.registerListener(new EntityHealthPacketIn());
                        eventBus.registerListener(new PlayerTradePacketIn());
                        eventBus.registerListener(new AiEntityDataPacketIn());
                        eventBus.registerListener(new CharactersMenuLoadPacketIn());
                        eventBus.registerListener(new BankManagePacketIn());
                        eventBus.registerListener(new EntityUpdatePacketIn());
                        eventBus.registerListener(new InitClientPrivilegePacketIn());
                        eventBus.registerListener(new InspectPlayerPacketIn());
                    }
                });
    }
}
