package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.inventory.TradePacketInfoOut;
import com.valenguard.client.game.inventory.TradeStatusOpcode;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.game.movement.ClientMovementProcessor;
import com.valenguard.client.game.movement.InputData;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.network.packet.out.PlayerTradePacketOut;
import com.valenguard.client.util.MoveNode;
import com.valenguard.client.util.PathFinding;

import java.util.LinkedList;
import java.util.Queue;

public class DropDownMenu extends HideableVisWindow implements Buildable {

    private MovingEntity movingEntity;
    private TextButton follow;
    private TextButton trade;
    private TextButton cancel;

    private final PathFinding pathFinding = new PathFinding();

    public DropDownMenu() {
        super("Choose Option");
    }

    @Override
    public Actor build() {

        // TODO: Determine when player clicks off the menu on the screen to auto close the menu
        // TODO: fill the entire screen with an invisible button? then detect its click to close the menu

        addCloseButton();
        VisTable buttonTable = new VisTable();

        follow = new TextButton("Follow", VisUI.getSkin());
        trade = new TextButton("Trade", VisUI.getSkin());
        cancel = new TextButton("Cancel", VisUI.getSkin());

        buttonTable.add(follow).expand().fill().row();
        buttonTable.add(trade).expand().fill().row();
        buttonTable.add(cancel).expand().fill();

        add(buttonTable).expand().fill();

        follow.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                setVisible(false);

                PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
                Location clientLocation = playerClient.getCurrentMapLocation();
                Location toLocation = movingEntity.getFutureMapLocation();

                Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), toLocation.getX(), toLocation.getY(), clientLocation.getMapName(), false);

                if (testMoveNodes == null) return;

                Queue<MoveNode> moveNodes = new LinkedList<MoveNode>();
                for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                    moveNodes.add(testMoveNodes.remove());
                }

                Valenguard.getInstance().getEntityTracker().startTracking(movingEntity);
                Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
                        new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes));
            }
        });

        trade.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
                new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_REQUEST_INIT_TARGET, movingEntity.getServerEntityID())).sendPacket();
                Valenguard.getInstance().getStageHandler().getTradeWindow().setTargetPlayer(movingEntity);
                Valenguard.getInstance().getStageHandler().getChatWindow().appendChatMessage("[Client] Sending trade request...");
            }
        });

        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
                closeOnEscape();
            }
        });


        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        pack();
        setVisible(false);
        return this;
    }

    public void toggleMenu(MovingEntity movingEntity, float x, float y) {
        this.movingEntity = movingEntity;
        setPosition(x, y);
        setVisible(true);
    }
}
