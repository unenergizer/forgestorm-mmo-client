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
import com.forgestorm.client.network.game.packet.in.*;
import com.forgestorm.client.network.game.shared.EventBus;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ClientMain extends Game {

    private final LoginCredentials loginCredentials = new LoginCredentials();

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

    private FileManager fileManager;
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
    private EntityManager entityManager;

    private InputMultiplexer inputMultiplexer;

    @Setter
    private UserInterfaceType userInterfaceType;

    @Setter
    private boolean ideRun;

    @Setter
    private boolean forceLocalHost;

    @Setter
    private boolean playIntroMusic;

    public ClientMain() {
    }

    @Override
    public void create() {
        Gdx.app.log("ForgeStorm", "Create called!");
        // front load all assets
        fileManager = new FileManager(this);
        fileManager.initFileManager();
        setScreen(assetLoadingScreen = new AssetLoadingScreen(this));
    }

    public void initGameManagers() {
        // load input
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        // load managers
        entityManager = new EntityManager(this);
        audioManager = new AudioManager(this);
        factionManager = new FactionManager(this);
        worldBuilder = new WorldBuilder(this);
        worldManager = new WorldManager(this);
        windowManager = new WindowManager();
        clientMovementProcessor = new ClientMovementProcessor(this);
        clientPlayerMovementManager = new ClientPlayerMovementManager(this);
        entityMovementManager = new EntityMovementManager(this);
        mouseManager = new MouseManager(this);
        itemStackManager = new ItemStackManager(this);
        skills = new Skills(this);
        entityTracker = new EntityTracker(this);
        tradeManager = new TradeManager(this);
        entityShopManager = new EntityShopManager(this);
        moveInventoryEvents = new MoveInventoryEvents(this);
        abilityManager = new AbilityManager(this);
        doorManager = new DoorManager(this);
        regionManager = new RegionManager(this);

        // load screens
        stageHandler = new StageHandler(this);
        gameScreen = new GameScreen(this);
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
        entityManager.dispose();
        abilityManager.dispose();
        stageHandler.dispose();
        gameScreen.dispose();
        worldManager.dispose();
        fileManager.dispose();
    }

    private void initializeNetwork() {
        final ClientMain clientMain = this;
        connectionManager = new ConnectionManager(clientMain,
                fileManager.getNetworkSettingsData(),
                loginCredentials,
                new Consumer<EventBus>() {
                    @Override
                    public void accept(EventBus eventBus) {
                        eventBus.registerListener(new PingPacketIn(clientMain));
                        eventBus.registerListener(new InitScreenPacketIn(clientMain));
                        eventBus.registerListener(new EntityMovePacketIn(clientMain));
                        eventBus.registerListener(new EntitySpawnPacketIn(clientMain));
                        eventBus.registerListener(new EntityDespawnPacketIn(clientMain));
                        eventBus.registerListener(new InitializeGameWorldPacketIn(clientMain));
                        eventBus.registerListener(new ChatMessagePacketIn(clientMain));
                        eventBus.registerListener(new EntityAppearancePacketIn(clientMain));
                        eventBus.registerListener(new InventoryPacketIn(clientMain));
                        eventBus.registerListener(new SkillExperiencePacketIn(clientMain));
                        eventBus.registerListener(new EntityAttributesUpdatePacketIn(clientMain));
                        eventBus.registerListener(new MovingEntityTeleportPacketIn(clientMain));
                        eventBus.registerListener(new EntityDamagePacketIn(clientMain));
                        eventBus.registerListener(new EntityHealthPacketIn(clientMain));
                        eventBus.registerListener(new PlayerTradePacketIn(clientMain));
                        eventBus.registerListener(new AiEntityDataPacketIn(clientMain));
                        eventBus.registerListener(new CharactersMenuLoadPacketIn(clientMain));
                        eventBus.registerListener(new BankManagePacketIn(clientMain));
                        eventBus.registerListener(new EntityUpdatePacketIn(clientMain));
                        eventBus.registerListener(new InitClientPrivilegePacketIn(clientMain));
                        eventBus.registerListener(new InspectPlayerPacketIn(clientMain));
                        eventBus.registerListener(new ProfileRequestPacketIn(clientMain));
                        eventBus.registerListener(new ClientMoveResyncPacketIn(clientMain));
                        eventBus.registerListener(new CharacterCreatorPacketIn(clientMain));
                        eventBus.registerListener(new WorldBuilderPacketIn(clientMain));
                        eventBus.registerListener(new WorldChunkPartPacketIn(clientMain));
                        eventBus.registerListener(new TileWarpPacketIn(clientMain));
                        eventBus.registerListener(new DoorInteractPacketIn(clientMain));
                        eventBus.registerListener(new TileImageStatusesPacketIn(clientMain));
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
        entityManager.dispose();
    }
}
