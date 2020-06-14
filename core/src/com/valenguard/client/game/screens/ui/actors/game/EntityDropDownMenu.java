package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.input.ClickAction;
import com.valenguard.client.game.movement.AbstractPostProcessor;
import com.valenguard.client.game.movement.ClientMovementProcessor;
import com.valenguard.client.game.movement.InputData;
import com.valenguard.client.game.movement.MoveUtil;
import com.valenguard.client.game.rpg.EntityShopAction;
import com.valenguard.client.game.rpg.ShopOpcodes;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.LeftAlignTextButton;
import com.valenguard.client.game.screens.ui.actors.dev.entity.EntityEditor;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.screens.ui.actors.game.paging.EntityShopWindow;
import com.valenguard.client.game.world.entities.AiEntity;
import com.valenguard.client.game.world.entities.Entity;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.EntityType;
import com.valenguard.client.game.world.entities.ItemStackDrop;
import com.valenguard.client.game.world.entities.MovingEntity;
import com.valenguard.client.game.world.entities.NPC;
import com.valenguard.client.game.world.entities.Player;
import com.valenguard.client.game.world.entities.PlayerClient;
import com.valenguard.client.game.world.item.BankActions;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackType;
import com.valenguard.client.game.world.item.trade.TradePacketInfoOut;
import com.valenguard.client.game.world.item.trade.TradeStatusOpcode;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.game.world.maps.MapUtil;
import com.valenguard.client.game.world.maps.Tile;
import com.valenguard.client.network.game.packet.out.BankManagePacketOut;
import com.valenguard.client.network.game.packet.out.ClickActionPacketOut;
import com.valenguard.client.network.game.packet.out.EntityShopPacketOut;
import com.valenguard.client.network.game.packet.out.InspectPlayerPacketOut;
import com.valenguard.client.network.game.packet.out.PlayerTradePacketOut;
import com.valenguard.client.util.MoveNode;
import com.valenguard.client.util.PathFinding;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.valenguard.client.util.Log.println;

public class EntityDropDownMenu extends HideableVisWindow implements Buildable {

    private final EntityDropDownMenu dropDownMenu;
    private StageHandler stageHandler;
    private VisTable dropDownTable = new VisTable();

    private final PathFinding pathFinding = new PathFinding();

    public EntityDropDownMenu() {
        super("Choose Option");
        this.dropDownMenu = this;
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        add(dropDownTable).grow();

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {
                cleanUpDropDownMenu(true);
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        setVisible(false);
        return this;
    }

    public void toggleMenu(List<Entity> entityList, float x, float y) {
        cleanUpDropDownMenu(false);
        setPosition(x, y);

        for (Entity entity : entityList) {
            dropDownTable.add(new EditorMenuEntry(entity)).expand().fill().row();

            // Adds players, monsters, and npcs;
            dropDownTable.add(new MenuEntry(entity)).expand().fill().row();
        }

        addWalkHereButton(dropDownTable, entityList.get(0).getCurrentMapLocation());
        addCancelButton(dropDownTable);

        pack();
        ActorUtil.fadeInWindow(dropDownMenu);
        toFront();
    }

    private void sendTradeRequest(Player player) {
        Valenguard.getInstance().getEntityTracker().cancelTracking();
        new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_REQUEST_INIT_TARGET, player.getServerEntityID())).sendPacket();
        stageHandler.getTradeWindow().setTradeTarget(player);
        stageHandler.getChatWindow().appendChatMessage("[Client] Sending trade request...");
        cleanUpDropDownMenu(true);
    }

    private void addWalkHereButton(VisTable visTable, final Location toLocation) {
        LeftAlignTextButton walkHereButton = new LeftAlignTextButton("Walk Here");
        visTable.add(walkHereButton).expand().fill().row();

        walkHereButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);

                Location clientLocation = EntityManager.getInstance().getPlayerClient().getCurrentMapLocation();
                Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                if (testMoveNodes == null) {
                    stageHandler.getChatWindow().appendChatMessage("No suitable walk path.");
                    cleanUpDropDownMenu(true);
                    return;
                }

                Queue<MoveNode> moveNodes = pathFinding.removeLastNode(testMoveNodes);

                Valenguard.getInstance().getClientMovementProcessor().postProcessMovement(
                        new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, new AbstractPostProcessor() {
                            @Override
                            public void postMoveAction() {
                            }
                        }));

                cleanUpDropDownMenu(true);
            }
        });
    }

    private void addCancelButton(VisTable visTable) {
        LeftAlignTextButton cancelButton = new LeftAlignTextButton("Cancel");
        visTable.add(cancelButton).expand().fill().row();

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void cleanUpDropDownMenu(boolean closeWindow) {
        if (closeWindow) ActorUtil.fadeOutWindow(dropDownMenu);
        dropDownTable.clearListeners();
        dropDownTable.clearChildren();
    }

    class EditorMenuEntry extends VisTable {

        private final Entity clickedEntity;

        EditorMenuEntry(Entity clickedEntity) {
            this.clickedEntity = clickedEntity;

            addEditEntityButton();
        }

        private void addEditEntityButton() {
            if (clickedEntity.getEntityType() == EntityType.PLAYER ||
                    clickedEntity.getEntityType() == EntityType.CLIENT_PLAYER) return;
            if (!Valenguard.getInstance().isAdmin()) return;
            if (clickedEntity.getEntityType() == EntityType.ITEM_STACK) {
                // If this ItemStackDrop spawned from an Entity Kill, then don't allow editor button
                if (((ItemStackDrop) clickedEntity).isSpawnedFromDropTable()) return;
            }

            LeftAlignTextButton editEntityButton = new LeftAlignTextButton("Edit " + clickedEntity.getEntityName());
            editEntityButton.setColor(Color.YELLOW);
            add(editEntityButton).expand().fill().row();

            editEntityButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);

                    EntityEditor entityEditor = stageHandler.getEntityEditor();
                    if (clickedEntity.getEntityType() == EntityType.MONSTER) {
                        entityEditor.getTabbedPane().switchTab(entityEditor.getMonsterTab());
                        entityEditor.getMonsterTab().loadAiEntity((AiEntity) clickedEntity);
                    } else if (clickedEntity.getEntityType() == EntityType.NPC) {
                        entityEditor.getTabbedPane().switchTab(entityEditor.getNpcTab());
                        entityEditor.getNpcTab().loadAiEntity((AiEntity) clickedEntity);
                    } else if (clickedEntity.getEntityType() == EntityType.ITEM_STACK) {
                        entityEditor.getTabbedPane().switchTab(entityEditor.getItemStackDropTab());
                        entityEditor.getItemStackDropTab().loadEntity((ItemStackDrop) clickedEntity);
                    }
                    ActorUtil.fadeInWindow(entityEditor);

                    stageHandler.getChatWindow().appendChatMessage("[YELLOW]Editing " + clickedEntity.getEntityName() + ".");
                    cleanUpDropDownMenu(true);
                }
            });
        }
    }

    class MenuEntry extends VisTable {

        private MovingEntity clickedMovingEntity;
        private PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        private Color nameColor = Color.GRAY;
        private String entityName;

        MenuEntry(Entity clickedEntity) {

            if (clickedEntity instanceof MovingEntity)
                this.clickedMovingEntity = (MovingEntity) clickedEntity;

            if (clickedEntity instanceof AiEntity) {
                nameColor = ((AiEntity) clickedEntity).getAlignment().getDefaultColor();
            } else if (clickedEntity instanceof ItemStackDrop) {
                nameColor = Color.YELLOW;
            }
            entityName = "[#" + nameColor + "]" + clickedEntity.getEntityName();

            if (clickedEntity instanceof MovingEntity) {
                addOpenBankButton();
                addTradeButton();
                addShopButton();
                addTalkButton();
                addInspectPlayerButton();
                addTargetButton();
                addAttackButton();
                addFollowButton();
                addViewProfileButton();
            } else if (clickedEntity instanceof ItemStackDrop) {
                addPickupButton((ItemStackDrop) clickedEntity);
            }
        }

        private void addViewProfileButton() {
            if (!(clickedMovingEntity instanceof Player)) return;

            LeftAlignTextButton openPlayerProfileButton = new LeftAlignTextButton("Open " + entityName + "'s Profile");
            add(openPlayerProfileButton).expand().fill().row();

            openPlayerProfileButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    stageHandler.getPlayerProfileWindow().openPlayerProfile((Player) clickedMovingEntity);
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addPickupButton(final ItemStackDrop itemStackDrop) {
            // This is only used to get data about the ItemStack drop
            ItemStack itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(itemStackDrop.getItemStackId(), 1);

            LeftAlignTextButton pickupButton;
            if (itemStack.getItemStackType() == ItemStackType.GOLD) {
                pickupButton = new LeftAlignTextButton("Pick up [GOLD]" + entityName);
            } else {
                pickupButton = new LeftAlignTextButton("Pick up " + entityName);
            }

            add(pickupButton).expand().fill().row();

            pickupButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Picking up ItemStacks from the ground

                    Queue<MoveNode> moveNodes = null;
                    PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location location = itemStackDrop.getCurrentMapLocation();

                    if (clientLocation.isWithinDistance(location, (short) 1)) {
                        // The player is requesting to interact with the entity.
                        if (!MoveUtil.isEntityMoving(playerClient)) {
                            println(getClass(), "ItemStack clicked! ID: " + itemStackDrop.getServerEntityID());
                            new ClickActionPacketOut(new ClickAction(ClickAction.LEFT, itemStackDrop)).sendPacket();
                        }
                    } else {
                        // New Entity click so lets cancelTracking entityTracker
                        Valenguard.getInstance().getEntityTracker().cancelTracking();

                        // Top right quad
                        Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), location.getX(), location.getY(), clientLocation.getMapName(), true);
                        if (testMoveNodes == null) return;
                        moveNodes = new LinkedList<MoveNode>();
                        for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                            moveNodes.add(testMoveNodes.remove());
                        }

                        println(getClass(), "Generated path to itemstack");
                    }

                    // Click to walk path finding
                    if (moveNodes == null) {
                        // New Entity click so lets cancelTracking entityTracker
                        Valenguard.getInstance().getEntityTracker().cancelTracking();
                        moveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), location.getX(), location.getY(), clientLocation.getMapName(), false);
                    }

                    if (moveNodes != null) {

                        Valenguard.getInstance().getEntityTracker().startTracking(itemStackDrop);
                        Valenguard.getInstance().getClientMovementProcessor().postProcessMovement(
                                new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, new AbstractPostProcessor() {
                                    @Override
                                    public void postMoveAction() {
                                        new ClickActionPacketOut(new ClickAction(ClickAction.LEFT, itemStackDrop)).sendPacket();
                                    }
                                }));
                    }
                    cleanUpDropDownMenu(true);
                }

            });
        }

        // Talk, OpenBank, Attack, Trade, Shop, Follow, Exit, Walk Here

        private void addTalkButton() {
            if (!(clickedMovingEntity instanceof NPC)) return;

            LeftAlignTextButton talkButton = new LeftAlignTextButton("Talk To " + entityName);
            add(talkButton).expand().fill().row();

            talkButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {

                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedMovingEntity.getFutureMapLocation();
                    attemptTraverseTalk(clientLocation, toLocation);
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addInspectPlayerButton() {
            if (clickedMovingEntity instanceof AiEntity) return;

            LeftAlignTextButton inspectPlayerButton = new LeftAlignTextButton("Inspect " + entityName);
            add(inspectPlayerButton).expand().fill().row();

            inspectPlayerButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);

                    new InspectPlayerPacketOut(clickedMovingEntity.getServerEntityID()).sendPacket();
                    stageHandler.getCharacterInspectionWindow().setPlayerToInspect((Player) clickedMovingEntity);

                    stageHandler.getChatWindow().appendChatMessage("[YELLOW]Inspecting player [GOLD]" + clickedMovingEntity.getEntityName() + "s [YELLOW]equipment.");
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addOpenBankButton() {
            if (clickedMovingEntity.getEntityType() == EntityType.CLIENT_PLAYER || clickedMovingEntity.getEntityType() == EntityType.PLAYER)
                return;
            if (!((AiEntity) clickedMovingEntity).isBankKeeper()) return;

            LeftAlignTextButton openBankButton = new LeftAlignTextButton("Open Bank");
            add(openBankButton).fill().row();

            openBankButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);

                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedMovingEntity.getFutureMapLocation();

                    // Check to see if the player is next to the bank teller
                    if (clientLocation.isWithinDistance(toLocation, ClientConstants.MAX_BANK_DISTANCE)) {
                        new BankManagePacketOut(BankActions.PLAYER_REQUEST_OPEN).sendPacket();
                    } else {
                        attemptBankTraversal(clientLocation, toLocation);
                    }
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void attemptBankTraversal(Location clientLocation, Location toLocation) {
            // Traverse to the bank teller
            Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

            if (testMoveNodes == null) {
                if (!traverseToBankAccessPoint(clientLocation)) {
                    stageHandler.getChatWindow().appendChatMessage("No suitable path to open bank.");
                }

                cleanUpDropDownMenu(true);
                return;
            }

            Queue<MoveNode> moveNodes = pathFinding.removeLastNode(testMoveNodes);

            Valenguard.getInstance().getEntityTracker().startTracking(clickedMovingEntity);
            Valenguard.getInstance().getClientMovementProcessor().postProcessMovement(
                    new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, new AbstractPostProcessor() {
                        @Override
                        public void postMoveAction() {
                            new BankManagePacketOut(BankActions.PLAYER_REQUEST_OPEN).sendPacket();
                        }
                    }));
        }

        private boolean traverseToBankAccessPoint(Location clientLocation) {
            // Check to see if there is a bank access point around where the entity is
            Location clickedEntityLocation = clickedMovingEntity.getFutureMapLocation();
            Location northAccessPoint = new Location(clickedEntityLocation).add((short) 0, (short) 1);
            Location eastAccessPoint = new Location(clickedEntityLocation).add((short) 1, (short) 0);
            Location southAccessPoint = new Location(clickedEntityLocation).add((short) 0, (short) -1);
            Location westAccessPoint = new Location(clickedEntityLocation).add((short) -1, (short) 0);

            Queue<MoveNode> testMoveNodes;
            if (locationHasBankAccess(northAccessPoint)) {
                testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), northAccessPoint.getX(), (short) (northAccessPoint.getY() + 1), clientLocation.getMapName(), false);
            } else if (locationHasBankAccess(eastAccessPoint)) {
                testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), (short) (eastAccessPoint.getX() + 1), eastAccessPoint.getY(), clientLocation.getMapName(), false);
            } else if (locationHasBankAccess(southAccessPoint)) {
                testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), southAccessPoint.getX(), (short) (southAccessPoint.getY() - 1), clientLocation.getMapName(), false);
            } else if (locationHasBankAccess(westAccessPoint)) {
                testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), (short) (westAccessPoint.getX() - 1), westAccessPoint.getY(), clientLocation.getMapName(), false);
            } else {
                return false;
            }

            if (testMoveNodes == null) {
                return false;
            }

            Valenguard.getInstance().getEntityTracker().startTracking(clickedMovingEntity);
            Valenguard.getInstance().getClientMovementProcessor().postProcessMovement(
                    new InputData(ClientMovementProcessor.MovementInput.MOUSE, testMoveNodes, new AbstractPostProcessor() {
                        @Override
                        public void postMoveAction() {
                            new BankManagePacketOut(BankActions.PLAYER_REQUEST_OPEN).sendPacket();
                        }
                    }));

            return true;
        }

        private void attemptTraverseTalk(final Location clientLocation, Location toLocation) {

            Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

            if (testMoveNodes == null) {
                if (!traverseToBankAccessPoint(clientLocation)) {
                    stageHandler.getChatWindow().appendChatMessage("No suitable path to open bank.");
                }

                cleanUpDropDownMenu(true);
                return;
            }

            cleanUpDropDownMenu(true);

            Queue<MoveNode> moveNodes = pathFinding.removeLastNode(testMoveNodes);

            Valenguard.getInstance().getEntityTracker().startTracking(clickedMovingEntity);
            Valenguard.getInstance().getClientMovementProcessor().postProcessMovement(new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes,
                    new AbstractPostProcessor() {
                        @Override
                        public void postMoveAction() {
                            Valenguard.getInstance().getEntityTracker().cancelTracking();
                            NPC npc = (NPC) clickedMovingEntity;
                            npc.chat();
                        }
                    }));

        }

        private boolean locationHasBankAccess(Location location) {
            Tile bankAccessTile = MapUtil.getTileByLocation(location);
            return bankAccessTile != null && bankAccessTile.isFlagSet(Tile.BANK_ACCESS);
        }

        private void addTargetButton() {
            LeftAlignTextButton attackButton = new LeftAlignTextButton("Target " + entityName);
            add(attackButton).expand().fill().row();

            attackButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    playerClient.setTargetEntity(clickedMovingEntity);
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addAttackButton() {
            LeftAlignTextButton attackButton = new LeftAlignTextButton("Attack " + entityName);
            add(attackButton).expand().fill().row();

            attackButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedMovingEntity.getFutureMapLocation();
                    playerClient.setTargetEntity(clickedMovingEntity);

                    if (clientLocation.isWithinDistance(toLocation, (short) 1)) {
                        // The player is requesting to interact with the entity.
                        if (!MoveUtil.isEntityMoving(playerClient)) {
                            new ClickActionPacketOut(new ClickAction(ClickAction.RIGHT, clickedMovingEntity)).sendPacket();
                        }
                    } else {
                        Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                        if (testMoveNodes == null) {
                            stageHandler.getChatWindow().appendChatMessage("No suitable path to attack.");
                            cleanUpDropDownMenu(true);
                            return;
                        }

                        Queue<MoveNode> moveNodes = pathFinding.removeLastNode(testMoveNodes);

                        Valenguard.getInstance().getEntityTracker().startTracking(clickedMovingEntity);
                        Valenguard.getInstance().getClientMovementProcessor().postProcessMovement(
                                new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, new AbstractPostProcessor() {
                                    @Override
                                    public void postMoveAction() {
                                        new ClickActionPacketOut(new ClickAction(ClickAction.RIGHT, clickedMovingEntity)).sendPacket();
                                    }
                                }));
                    }
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addTradeButton() {
            if (clickedMovingEntity.getEntityType() != EntityType.PLAYER) return;
            LeftAlignTextButton tradeButton = new LeftAlignTextButton("Trade with " + entityName);
            add(tradeButton).expand().fill().row();

            tradeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedMovingEntity.getFutureMapLocation();

                    if (clientLocation.isWithinDistance(toLocation, (short) 1)) {
                        // The player is requesting to interact with the entity.
                        if (!MoveUtil.isEntityMoving(playerClient)) {
                            sendTradeRequest((Player) clickedMovingEntity);
                        }
                    } else {
                        Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                        if (testMoveNodes == null) {
                            stageHandler.getChatWindow().appendChatMessage("No suitable walk path.");
                            cleanUpDropDownMenu(true);
                            return;
                        }

                        Queue<MoveNode> moveNodes = pathFinding.removeLastNode(testMoveNodes);

                        Valenguard.getInstance().getEntityTracker().startTracking(clickedMovingEntity);
                        Valenguard.getInstance().getClientMovementProcessor().postProcessMovement(
                                new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, new AbstractPostProcessor() {
                                    @Override
                                    public void postMoveAction() {
                                        sendTradeRequest((Player) clickedMovingEntity);
                                    }
                                }));
                    }
                }
            });
        }

        private void addShopButton() {
            if (clickedMovingEntity.getEntityType() == EntityType.PLAYER) return;
            if (((AiEntity) clickedMovingEntity).getShopID() < 0) return;
            LeftAlignTextButton shopButton = new LeftAlignTextButton("Open Shop");
            add(shopButton).expand().fill().row();

            shopButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    final EntityShopWindow pagedItemStackWindow = stageHandler.getPagedItemStackWindow();
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedMovingEntity.getFutureMapLocation();

                    if (clientLocation.isWithinDistance(toLocation, ClientConstants.MAX_SHOP_DISTANCE)) {
                        // The player is requesting to interact with the entity.
                        if (!MoveUtil.isEntityMoving(playerClient)) {
                            new EntityShopPacketOut(new EntityShopAction(ShopOpcodes.START_SHOPPING, clickedMovingEntity.getServerEntityID())).sendPacket();
                            pagedItemStackWindow.openWindow(clickedMovingEntity, ((AiEntity) clickedMovingEntity).getShopID());
                        }
                    } else {
                        Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                        if (testMoveNodes == null) {
                            stageHandler.getChatWindow().appendChatMessage("[RED]You are too far away to use this shop.");
                            return;
                        }

                        Queue<MoveNode> moveNodes = pathFinding.removeLastNode(testMoveNodes);

                        Valenguard.getInstance().getEntityTracker().startTracking(clickedMovingEntity);
                        Valenguard.getInstance().getClientMovementProcessor().postProcessMovement(
                                new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, new AbstractPostProcessor() {
                                    @Override
                                    public void postMoveAction() {
                                        Valenguard.getInstance().getEntityTracker().cancelTracking();
                                        new EntityShopPacketOut(new EntityShopAction(ShopOpcodes.START_SHOPPING, clickedMovingEntity.getServerEntityID())).sendPacket();
                                        pagedItemStackWindow.openWindow(clickedMovingEntity, ((AiEntity) clickedMovingEntity).getShopID());
                                    }
                                }));
                    }
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addFollowButton() {
            if (clickedMovingEntity instanceof AiEntity && ((AiEntity) clickedMovingEntity).isBankKeeper())
                return;

            LeftAlignTextButton followButton = new LeftAlignTextButton("Follow " + entityName);
            add(followButton).expand().fill().row();

            followButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedMovingEntity.getFutureMapLocation();

                    Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                    if (testMoveNodes == null) {
                        stageHandler.getChatWindow().appendChatMessage("No suitable follow path.");
                        cleanUpDropDownMenu(true);
                        return;
                    }

                    Queue<MoveNode> moveNodes = pathFinding.removeLastNode(testMoveNodes);

                    Valenguard.getInstance().getEntityTracker().startTracking(clickedMovingEntity);
                    Valenguard.getInstance().getClientMovementProcessor().postProcessMovement(
                            new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, null));
                    cleanUpDropDownMenu(true);
                }
            });
        }
    }
}
