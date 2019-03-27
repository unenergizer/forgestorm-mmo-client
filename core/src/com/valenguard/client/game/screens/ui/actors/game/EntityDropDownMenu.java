package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.input.ClickAction;
import com.valenguard.client.game.movement.AbstractPostProcessor;
import com.valenguard.client.game.movement.ClientMovementProcessor;
import com.valenguard.client.game.movement.InputData;
import com.valenguard.client.game.movement.MoveUtil;
import com.valenguard.client.game.rpg.EntityShopAction;
import com.valenguard.client.game.rpg.ShopOpcodes;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.world.entities.AiEntity;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.EntityType;
import com.valenguard.client.game.world.entities.MovingEntity;
import com.valenguard.client.game.world.entities.Player;
import com.valenguard.client.game.world.entities.PlayerClient;
import com.valenguard.client.game.world.item.trade.TradePacketInfoOut;
import com.valenguard.client.game.world.item.trade.TradeStatusOpcode;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.network.game.packet.out.ClickActionPacketOut;
import com.valenguard.client.network.game.packet.out.EntityShopPacketOut;
import com.valenguard.client.network.game.packet.out.PlayerTradePacketOut;
import com.valenguard.client.util.MoveNode;
import com.valenguard.client.util.PathFinding;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EntityDropDownMenu extends HideableVisWindow implements Buildable {

    private final EntityDropDownMenu dropDownMenu;
    private VisTable dropDownTable = new VisTable();

    private final PathFinding pathFinding = new PathFinding();

    public EntityDropDownMenu() {
        super("Choose Option");
        this.dropDownMenu = this;
    }

    @Override
    public Actor build() {
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

    public void toggleMenu(List<MovingEntity> movingEntityList, float x, float y) {
        cleanUpDropDownMenu(false);
        dropDownTable = new VisTable();
        setPosition(x, y);

        for (MovingEntity movingEntity : movingEntityList) {
            dropDownTable.add(new MenuEntry(movingEntity)).expand().fill().row();
        }

        addWalkHereButton(dropDownTable, movingEntityList.get(0).getCurrentMapLocation());
        addCancelButton(dropDownTable);

        dropDownMenu.add(dropDownTable).expand().fill();

        pack();

        ActorUtil.fadeInWindow(dropDownMenu);
    }

    private void sendTradeRequest(Player player) {
        Valenguard.getInstance().getEntityTracker().cancelTracking();
        new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_REQUEST_INIT_TARGET, player.getServerEntityID())).sendPacket();
        ActorUtil.getStageHandler().getTradeWindow().setTradeTarget(player);
        ActorUtil.getStageHandler().getChatWindow().appendChatMessage("[Client] Sending trade request...");
        cleanUpDropDownMenu(true);
    }

    private void addWalkHereButton(VisTable visTable, final Location toLocation) {
        VisTextButton walkHereButton = new VisTextButton("Walk Here");
        visTable.add(walkHereButton).expand().fill().row();

        walkHereButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                Location clientLocation = EntityManager.getInstance().getPlayerClient().getCurrentMapLocation();
                Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                if (testMoveNodes == null) return;

                Queue<MoveNode> moveNodes = new LinkedList<MoveNode>();
                for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                    moveNodes.add(testMoveNodes.remove());
                }

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
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void cleanUpDropDownMenu(boolean closeWindow) {
        if (closeWindow) ActorUtil.fadeOutWindow(dropDownMenu);
        dropDownTable.remove();
    }

    class MenuEntry extends VisTable {

        private final MovingEntity clickedEntity;
        private PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        MenuEntry(MovingEntity clickedEntity) {
            this.clickedEntity = clickedEntity;

            addAttackButton();
            addTradeButton();
            addShopButton();
            addFollowButton();
        }

        // Attack, Trade, Shop, Follow, Exit, Walk Here

        private void addAttackButton() {
            VisTextButton attackButton = new VisTextButton("Attack " + clickedEntity.getEntityName());
            add(attackButton).expand().fill().row();

            attackButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedEntity.getFutureMapLocation();

                    if (clientLocation.isWithinDistance(toLocation, (short) 1)) {
                        // The player is requesting to interact with the entity.
                        if (!MoveUtil.isEntityMoving(playerClient)) {
                            new ClickActionPacketOut(new ClickAction(ClickAction.RIGHT, clickedEntity)).sendPacket();
                        }
                    } else {
                        Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                        if (testMoveNodes == null) return;

                        Queue<MoveNode> moveNodes = new LinkedList<MoveNode>();
                        for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                            moveNodes.add(testMoveNodes.remove());
                        }

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
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedEntity.getFutureMapLocation();

                    if (clientLocation.isWithinDistance(toLocation, (short) 1)) {
                        // The player is requesting to interact with the entity.
                        if (!MoveUtil.isEntityMoving(playerClient)) {
                            sendTradeRequest((Player) clickedEntity);
                        }
                    } else {
                        Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                        if (testMoveNodes == null) return;

                        Queue<MoveNode> moveNodes = new LinkedList<MoveNode>();
                        for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                            moveNodes.add(testMoveNodes.remove());
                        }

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
                    final EntityShopWindow entityShopWindow = ActorUtil.getStageHandler().getEntityShopWindow();
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedEntity.getFutureMapLocation();

                    if (clientLocation.isWithinDistance(toLocation, (short) 1)) {
                        // The player is requesting to interact with the entity.
                        if (!MoveUtil.isEntityMoving(playerClient)) {
                            new EntityShopPacketOut(new EntityShopAction(ShopOpcodes.START_SHOPPING, clickedEntity.getServerEntityID())).sendPacket();
                            entityShopWindow.loadShop(clickedEntity, ((AiEntity) clickedEntity).getShopID());
                        }
                    } else {
                        Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                        if (testMoveNodes == null) return;

                        Queue<MoveNode> moveNodes = new LinkedList<MoveNode>();
                        for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                            moveNodes.add(testMoveNodes.remove());
                        }

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
            VisTextButton followButton = new VisTextButton("Follow " + clickedEntity.getEntityName());
            add(followButton).expand().fill().row();

            followButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Location clientLocation = playerClient.getFutureMapLocation();
                    Location toLocation = clickedEntity.getFutureMapLocation();

                    Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                    if (testMoveNodes == null) return;

                    Queue<MoveNode> moveNodes = new LinkedList<MoveNode>();
                    for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                        moveNodes.add(testMoveNodes.remove());
                    }

                    Valenguard.getInstance().getEntityTracker().startTracking(clickedEntity);
                    Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
                            new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, null));
                    cleanUpDropDownMenu(true);
                }
            });
        }
    }
}
