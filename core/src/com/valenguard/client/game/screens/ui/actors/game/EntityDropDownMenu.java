package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
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
import com.valenguard.client.game.screens.ui.actors.dev.entity.EntityEditor;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
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

import java.util.List;
import java.util.Queue;

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
            if (entity instanceof MovingEntity)
                dropDownTable.add(new MenuEntry((MovingEntity) entity)).expand().fill().row();
        }

        addWalkHereButton(dropDownTable, entityList.get(0).getCurrentMapLocation());
        addCancelButton(dropDownTable);

        pack();

        ActorUtil.fadeInWindow(dropDownMenu);
    }

    private void sendTradeRequest(Player player) {
        Valenguard.getInstance().getEntityTracker().cancelTracking();
        new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_REQUEST_INIT_TARGET, player.getServerEntityID())).sendPacket();
        stageHandler.getTradeWindow().setTradeTarget(player);
        stageHandler.getChatWindow().appendChatMessage("[Client] Sending trade request...");
        cleanUpDropDownMenu(true);
    }

    private void addWalkHereButton(VisTable visTable, final Location toLocation) {
        VisTextButton walkHereButton = new VisTextButton("Walk Here");
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

                Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
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
        VisTextButton cancelButton = new VisTextButton("Cancel");
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
        dropDownTable.clearChildren();
    }

    class EditorMenuEntry extends VisTable {

        private final Entity clickedEntity;
        private PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

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

            VisTextButton editEntityButton = new VisTextButton("Edit " + clickedEntity.getEntityName());
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

        private final MovingEntity clickedEntity;
        private PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        MenuEntry(MovingEntity clickedEntity) {
            this.clickedEntity = clickedEntity;

            addOpenBankButton();
            addTradeButton();
            addShopButton();
            addTalkButton();
            addInspectPlayerButton();
            addTargetButton();
            addAttackButton();
            addFollowButton();
        }

        // Talk, OpenBank, Attack, Trade, Shop, Follow, Exit, Walk Here

        private void addTalkButton() {
            if (!(clickedEntity instanceof NPC)) return;

            VisTextButton talkButton = new VisTextButton("Talk");
            add(talkButton).expand().fill().row();

            talkButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {

                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedEntity.getFutureMapLocation();
                    attemptTraverseTalk(clientLocation, toLocation);

                }
            });
        }

        private void addInspectPlayerButton() {
            if (clickedEntity instanceof AiEntity) return;

            VisTextButton inspectPlayerButton = new VisTextButton("Inspect " + clickedEntity.getEntityName());
            add(inspectPlayerButton).expand().fill().row();

            inspectPlayerButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);

                    new InspectPlayerPacketOut(clickedEntity.getServerEntityID()).sendPacket();

                    stageHandler.getChatWindow().appendChatMessage("[YELLOW]Inspecting player " + clickedEntity.getEntityName() + " equipment.");
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addOpenBankButton() {
            if (clickedEntity.getEntityType() == EntityType.CLIENT_PLAYER || clickedEntity.getEntityType() == EntityType.PLAYER)
                return;
            if (!((AiEntity) clickedEntity).isBankKeeper()) return;

            VisTextButton openBankButton = new VisTextButton("Open Bank");
            add(openBankButton).fill().row();

            openBankButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);

                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedEntity.getFutureMapLocation();

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

            Valenguard.getInstance().getEntityTracker().startTracking(clickedEntity);
            Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
                    new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, new AbstractPostProcessor() {
                        @Override
                        public void postMoveAction() {
                            new BankManagePacketOut(BankActions.PLAYER_REQUEST_OPEN).sendPacket();
                        }
                    }));
        }

        private boolean traverseToBankAccessPoint(Location clientLocation) {
            // Check to see if there is a bank access point around where the entity is
            Location clickedEntityLocation = clickedEntity.getFutureMapLocation();
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

            Valenguard.getInstance().getEntityTracker().startTracking(clickedEntity);
            Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
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

            Valenguard.getInstance().getEntityTracker().startTracking(clickedEntity);
            Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes,
                    new AbstractPostProcessor() {
                        @Override
                        public void postMoveAction() {
                            Valenguard.getInstance().getEntityTracker().cancelTracking();
                            NPC npc = (NPC) clickedEntity;
                            npc.chat();
                        }
                    }));

        }

        private boolean locationHasBankAccess(Location location) {
            Tile bankAccessTile = MapUtil.getTileByLocation(location);
            return bankAccessTile != null && bankAccessTile.isFlagSet(Tile.BANK_ACCESS);
        }

        private void addTargetButton() {
            VisTextButton attackButton = new VisTextButton("Target " + clickedEntity.getEntityName());
            add(attackButton).expand().fill().row();

            attackButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    playerClient.setTargetEntity(clickedEntity);
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addAttackButton() {
            VisTextButton attackButton = new VisTextButton("Attack " + clickedEntity.getEntityName());
            add(attackButton).expand().fill().row();

            attackButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedEntity.getFutureMapLocation();
                    playerClient.setTargetEntity(clickedEntity);

                    if (clientLocation.isWithinDistance(toLocation, (short) 1)) {
                        // The player is requesting to interact with the entity.
                        if (!MoveUtil.isEntityMoving(playerClient)) {
                            new ClickActionPacketOut(new ClickAction(ClickAction.RIGHT, clickedEntity)).sendPacket();
                        }
                    } else {
                        Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                        if (testMoveNodes == null) {
                            stageHandler.getChatWindow().appendChatMessage("No suitable path to attack.");
                            cleanUpDropDownMenu(true);
                            return;
                        }

                        Queue<MoveNode> moveNodes = pathFinding.removeLastNode(testMoveNodes);

                        Valenguard.getInstance().getEntityTracker().startTracking(clickedEntity);
                        Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
                                new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, new AbstractPostProcessor() {
                                    @Override
                                    public void postMoveAction() {
                                        new ClickActionPacketOut(new ClickAction(ClickAction.RIGHT, clickedEntity)).sendPacket();
                                    }
                                }));
                    }
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addTradeButton() {
            if (clickedEntity.getEntityType() != EntityType.PLAYER) return;
            VisTextButton tradeButton = new VisTextButton("Trade with " + clickedEntity.getEntityName());
            add(tradeButton).expand().fill().row();

            tradeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedEntity.getFutureMapLocation();

                    if (clientLocation.isWithinDistance(toLocation, (short) 1)) {
                        // The player is requesting to interact with the entity.
                        if (!MoveUtil.isEntityMoving(playerClient)) {
                            sendTradeRequest((Player) clickedEntity);
                        }
                    } else {
                        Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                        if (testMoveNodes == null) {
                            stageHandler.getChatWindow().appendChatMessage("No suitable walk path.");
                            cleanUpDropDownMenu(true);
                            return;
                        }

                        Queue<MoveNode> moveNodes = pathFinding.removeLastNode(testMoveNodes);

                        Valenguard.getInstance().getEntityTracker().startTracking(clickedEntity);
                        Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
                                new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, new AbstractPostProcessor() {
                                    @Override
                                    public void postMoveAction() {
                                        sendTradeRequest((Player) clickedEntity);
                                    }
                                }));
                    }
                }
            });
        }

        private void addShopButton() {
            if (clickedEntity.getEntityType() == EntityType.PLAYER) return;
            if (((AiEntity) clickedEntity).getShopID() < 0) return;
            VisTextButton shopButton = new VisTextButton("Shop " + clickedEntity.getEntityName());
            add(shopButton).expand().fill().row();

            shopButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    final EntityShopWindow entityShopWindow = stageHandler.getEntityShopWindow();
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedEntity.getFutureMapLocation();

                    if (clientLocation.isWithinDistance(toLocation, ClientConstants.MAX_SHOP_DISTANCE)) {
                        // The player is requesting to interact with the entity.
                        if (!MoveUtil.isEntityMoving(playerClient)) {
                            new EntityShopPacketOut(new EntityShopAction(ShopOpcodes.START_SHOPPING, clickedEntity.getServerEntityID())).sendPacket();
                            entityShopWindow.loadShop(clickedEntity, ((AiEntity) clickedEntity).getShopID());
                        }
                    } else {
                        Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                        if (testMoveNodes == null) {
                            stageHandler.getChatWindow().appendChatMessage("[RED]You are too far away to use this shop.");
                            return;
                        }

                        Queue<MoveNode> moveNodes = pathFinding.removeLastNode(testMoveNodes);

                        Valenguard.getInstance().getEntityTracker().startTracking(clickedEntity);
                        Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
                                new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, new AbstractPostProcessor() {
                                    @Override
                                    public void postMoveAction() {
                                        Valenguard.getInstance().getEntityTracker().cancelTracking();
                                        new EntityShopPacketOut(new EntityShopAction(ShopOpcodes.START_SHOPPING, clickedEntity.getServerEntityID())).sendPacket();
                                        entityShopWindow.loadShop(clickedEntity, ((AiEntity) clickedEntity).getShopID());
                                    }
                                }));
                    }
                    cleanUpDropDownMenu(true);
                }
            });
        }

        private void addFollowButton() {
            if (clickedEntity instanceof AiEntity && ((AiEntity) clickedEntity).isBankKeeper())
                return;

            VisTextButton followButton = new VisTextButton("Follow " + clickedEntity.getEntityName());
            add(followButton).expand().fill().row();

            followButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(EntityDropDownMenu.class, (short) 0);
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedEntity.getFutureMapLocation();

                    Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                    if (testMoveNodes == null) {
                        stageHandler.getChatWindow().appendChatMessage("No suitable follow path.");
                        cleanUpDropDownMenu(true);
                        return;
                    }

                    Queue<MoveNode> moveNodes = pathFinding.removeLastNode(testMoveNodes);

                    Valenguard.getInstance().getEntityTracker().startTracking(clickedEntity);
                    Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
                            new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, null));
                    cleanUpDropDownMenu(true);
                }
            });
        }
    }
}
