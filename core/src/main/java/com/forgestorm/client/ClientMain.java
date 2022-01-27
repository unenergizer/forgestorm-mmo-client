package com.forgestorm.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.forgestorm.client.game.abilities.AbilityManager;
import com.forgestorm.client.game.audio.AudioManager;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.movement.ClientMovementProcessor;
import com.forgestorm.client.game.movement.ClientPlayerMovementManager;
import com.forgestorm.client.game.movement.EntityMovementManager;
import com.forgestorm.client.game.movement.EntityTracker;
import com.forgestorm.client.game.rpg.EntityShopManager;
import com.forgestorm.client.game.rpg.FactionManager;
import com.forgestorm.client.game.rpg.Skills;
import com.forgestorm.client.game.screens.AssetLoadingScreen;
import com.forgestorm.client.game.screens.GameScreen;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.WindowManager;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.item.ItemStackManager;
import com.forgestorm.client.game.world.item.inventory.MoveInventoryEvents;
import com.forgestorm.client.game.world.item.trade.TradeManager;
import com.forgestorm.client.game.world.maps.DoorManager;
import com.forgestorm.client.game.world.maps.RegionManager;
import com.forgestorm.client.game.world.maps.WorldManager;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.client.network.ConnectionManager;
import com.forgestorm.client.network.game.ClientGameConnection;
import com.forgestorm.client.network.game.Consumer;
import com.forgestorm.client.network.game.LoginCredentials;
import com.forgestorm.client.network.game.packet.in.AiEntityDataPacketIn;
import com.forgestorm.client.network.game.packet.in.BankManagePacketIn;
import com.forgestorm.client.network.game.packet.in.CharacterCreatorPacketIn;
import com.forgestorm.client.network.game.packet.in.CharactersMenuLoadPacketIn;
import com.forgestorm.client.network.game.packet.in.ChatMessagePacketIn;
import com.forgestorm.client.network.game.packet.in.ClientMoveResyncPacketIn;
import com.forgestorm.client.network.game.packet.in.DoorInteractPacketIn;
import com.forgestorm.client.network.game.packet.in.EntityAppearancePacketIn;
import com.forgestorm.client.network.game.packet.in.EntityAttributesUpdatePacketIn;
import com.forgestorm.client.network.game.packet.in.EntityDamagePacketIn;
import com.forgestorm.client.network.game.packet.in.EntityDespawnPacketIn;
import com.forgestorm.client.network.game.packet.in.EntityHealthPacketIn;
import com.forgestorm.client.network.game.packet.in.EntityMovePacketIn;
import com.forgestorm.client.network.game.packet.in.EntitySpawnPacketIn;
import com.forgestorm.client.network.game.packet.in.EntityUpdatePacketIn;
import com.forgestorm.client.network.game.packet.in.InitClientPrivilegePacketIn;
import com.forgestorm.client.network.game.packet.in.InitScreenPacketIn;
import com.forgestorm.client.network.game.packet.in.InitializeGameWorldPacketIn;
import com.forgestorm.client.network.game.packet.in.InspectPlayerPacketIn;
import com.forgestorm.client.network.game.packet.in.InventoryPacketIn;
import com.forgestorm.client.network.game.packet.in.MovingEntityTeleportPacketIn;
import com.forgestorm.client.network.game.packet.in.PingPacketIn;
import com.forgestorm.client.network.game.packet.in.PlayerTradePacketIn;
import com.forgestorm.client.network.game.packet.in.ProfileRequestPacketIn;
import com.forgestorm.client.network.game.packet.in.SkillExperiencePacketIn;
import com.forgestorm.client.network.game.packet.in.TileWarpPacketIn;
import com.forgestorm.client.network.game.packet.in.WorldBuilderPacketIn;
import com.forgestorm.client.network.game.packet.in.WorldChunkPartPacketIn;
import com.forgestorm.client.network.game.shared.EventBus;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ClientMain extends Game {

    private final LoginCredentials loginCredentials = new LoginCredentials();

    private static ClientMain clientMain;
    private ConnectionManager connectionManager;
    private GameScreen gameScreen;

    @Setter
    private boolean ignoreRevisionNumber;
    @Setter
    private boolean needsUpdate = false;
    @Setter
    private int remoteRevisionNumber = -1;

    @Setter
    private boolean isContentDeveloper = false;
    @Setter
    private boolean isAdmin = false;
    @Setter
    private boolean isModerator = false;

    private final FileManager fileManager = new FileManager();
    private AssetLoadingScreen assetLoadingScreen;

    private AudioManager audioManager;
    private EntityTracker entityTracker;
    private WindowManager windowManager;
    private StageHandler stageHandler;
    private FactionManager factionManager;
    private WorldBuilder worldBuilder;
    private WorldManager worldManager;
    private ClientMovementProcessor clientMovementProcessor;
    private ClientPlayerMovementManager clientPlayerMovementManager;
    private EntityMovementManager entityMovementManager;
    private MouseManager mouseManager;
    private ItemStackManager itemStackManager;
    private Skills skills;
    private EntityShopManager entityShopManager;
    private MoveInventoryEvents moveInventoryEvents;
    private AbilityManager abilityManager;
    private TradeManager tradeManager;
    private DoorManager doorManager;
    private RegionManager regionManager;

    private InputMultiplexer inputMultiplexer;

    @Setter
    private UserInterfaceType userInterfaceType;

    @Setter
    private boolean ideRun;

    @Setter
    private boolean forceLocalHost;

    @Setter
    private boolean playIntroMusic;

    private ClientMain() {
    }

    public static ClientMain getInstance() {
        if (clientMain == null) clientMain = new ClientMain();
        return clientMain;
    }

    @Override
    public void create() {
        // front load all assets
        setScreen(assetLoadingScreen = new AssetLoadingScreen(fileManager));
    }

    public void initGameManagers() {
        // load input
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        // load managers
        audioManager = new AudioManager();
        factionManager = new FactionManager();
        worldBuilder = new WorldBuilder();
        worldManager = new WorldManager();
        windowManager = new WindowManager();
        clientMovementProcessor = new ClientMovementProcessor();
        clientPlayerMovementManager = new ClientPlayerMovementManager();
        entityMovementManager = new EntityMovementManager();
        mouseManager = new MouseManager();
        itemStackManager = new ItemStackManager();
        skills = new Skills();
        entityTracker = new EntityTracker();
        tradeManager = new TradeManager();
        entityShopManager = new EntityShopManager();
        moveInventoryEvents = new MoveInventoryEvents();
        abilityManager = new AbilityManager();
        doorManager = new DoorManager();
        regionManager = new RegionManager();

        // load screens
        stageHandler = new StageHandler();
        gameScreen = new GameScreen(stageHandler);
        setScreen(gameScreen);
        stageHandler.setUserInterface(UserInterfaceType.LOGIN);

        initializeNetwork();
    }

    @Override
    public void render() {
        if (connectionManager == null) {
            assetLoadingScreen.render(Gdx.graphics.getDeltaTime());
            return;
        }
        ClientGameConnection clientGameConnection = connectionManager.getClientGameConnection();

        if (clientGameConnection.isConnected()) {
            clientGameConnection.getEventBus().gameThreadPublish();
        }

        super.render();

        if (clientGameConnection.isConnected()) {
            connectionManager.getOutputStreamManager().sendPackets(clientGameConnection.getClientHandler());
        }
    }

    @Override
    public void dispose() {
        connectionManager.disconnect();
        EntityManager.getInstance().dispose();
        abilityManager.dispose();
        stageHandler.dispose();
        gameScreen.dispose();
        worldManager.dispose();
        fileManager.dispose();
    }

    private void initializeNetwork() {
        connectionManager = new ConnectionManager(
                fileManager.getNetworkSettingsData(),
                loginCredentials,
                new Consumer<EventBus>() {
                    @Override
                    public void accept(EventBus eventBus) {
                        eventBus.registerListener(new PingPacketIn());
                        eventBus.registerListener(new InitScreenPacketIn());
                        eventBus.registerListener(new EntityMovePacketIn());
                        eventBus.registerListener(new EntitySpawnPacketIn());
                        eventBus.registerListener(new EntityDespawnPacketIn());
                        eventBus.registerListener(new InitializeGameWorldPacketIn());
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
                        eventBus.registerListener(new ProfileRequestPacketIn());
                        eventBus.registerListener(new ClientMoveResyncPacketIn());
                        eventBus.registerListener(new CharacterCreatorPacketIn());
                        eventBus.registerListener(new WorldBuilderPacketIn());
                        eventBus.registerListener(new WorldChunkPartPacketIn());
                        eventBus.registerListener(new TileWarpPacketIn());
                        eventBus.registerListener(new DoorInteractPacketIn());
                    }
                });
    }

    public void gameWorldQuit() {
        entityTracker.gameQuitReset();
        skills.gameQuitReset();
        clientMovementProcessor.resetInput();
        abilityManager.gameQuitReset();
        tradeManager.gameQuitReset();
        stageHandler.resetUI();
        EntityManager.getInstance().dispose();
    }
}